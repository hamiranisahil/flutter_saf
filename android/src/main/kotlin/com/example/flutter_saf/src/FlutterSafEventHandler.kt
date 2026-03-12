package com.example.flutter_saf.src

import android.app.Activity
import android.net.Uri
import androidx.core.net.toUri
import io.flutter.plugin.common.EventChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log

class FlutterSafEventHandler(private val activity: Activity): EventChannel.StreamHandler {
    private var eventSink: EventChannel.EventSink? = null

    private var streamJob: Job? = null

    override fun onListen(
        arguments: Any?,
        events: EventChannel.EventSink?
    ) {
        eventSink = events
        val argMap = arguments as HashMap<*, *>
        when (argMap["method"]) {
            "getContentStream" -> getContentStream(argMap)
        }
    }

    override fun onCancel(arguments: Any?) {
        streamJob?.cancel()
        streamJob = null
        eventSink = null
    }

    private fun getContentStream(argMap: HashMap<*, *>) {
        val uriString = argMap["uri"] as? String
        if (uriString.isNullOrEmpty()) {
            eventSink?.error("INVALID_URI", "URI is null or empty", null)
            return
        }
        streamJob = CoroutineScope(Dispatchers.IO).launch {
            streamFile(uriString)
        }
    }

    private suspend fun streamFile(uri: String) {
        try {
            val contentResolver = activity.contentResolver
            val inputStream = contentResolver.openInputStream(uri.toUri())
                ?: throw Exception("Unable to open URI")
            inputStream.use {
                val chunkSize = determineChunkSize(uri)
                val buffer = ByteArray(chunkSize)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    val chunk = buffer.copyOf(bytesRead)
                    withContext(Dispatchers.Main) {
                        eventSink?.success(chunk)
                    }
                }
            }
            withContext(Dispatchers.Main) {
                eventSink?.endOfStream()
            }
        } catch (e: Exception) {
            eventSink?.error("STREAM_ERROR", "Error streaming file: ${e.message}", null)
        }
    }

    private fun determineChunkSize(uriString: String): Int {
        return when {
            uriString.endsWith(".mp4", true) || uriString.endsWith(".mkv", true) -> 256 * 1024 // 256 KB for video
            uriString.endsWith(".jpg", true) || uriString.endsWith(".png", true) -> 64 * 1024 // 64 KB for images
            else -> 128 * 1024 // default
        }
    }
}