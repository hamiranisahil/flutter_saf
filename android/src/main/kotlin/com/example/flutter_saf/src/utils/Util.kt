package com.example.flutter_saf.src.utils

import android.app.Activity
import android.content.Context
import android.content.UriPermission
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.graphics.scale
import androidx.core.net.toUri
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.io.File
import java.io.FileOutputStream

fun getImageThumbnail(context: Context, uri: Uri, width: Int?, height: Int?): Bitmap? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            val originalBitmap = BitmapFactory.decodeStream(input)
            if (originalBitmap != null && width != null && height != null) {
                originalBitmap.scale(width, height)
            } else {
                originalBitmap
            }
        }
    } catch (e: Exception) {
        null
    }

}

fun getVideoThumbnail(context: Context, uri: Uri, width: Int?, height: Int?): Bitmap? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val frame = retriever.frameAtTime
        if (frame != null && width != null && height != null) {
            frame.scale(width, height)
        } else {
            frame
        }
    } catch (e: Exception) {
        null
    }
}

fun getAudioThumbnail(context: Context, uri: Uri, width: Int?, height: Int?): Bitmap? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val art: ByteArray? = retriever.embeddedPicture
        val bitmap = art?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
        if (bitmap != null && width != null && height != null) {
            bitmap.scale(width, height)
        } else {
            bitmap
        }
    } catch (e: Exception) {
        null
    }
}

fun getPersistedUriPermissions(context: Context): List<UriPermission> {
    val persistedUris = context.contentResolver.persistedUriPermissions
    return persistedUris
}

// Utility function
fun readFileContent(uri: String, context: Context): ByteArray {
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(uri.toUri())
    return inputStream?.readBytes() ?: ByteArray(0)
}

fun copyToCache(activity: Activity, uri: Uri): String {
    val resolver = activity.contentResolver

    val cacheDir = activity.cacheDir
    val fileName = queryFileName(activity, uri) ?: "shared_${System.currentTimeMillis()}"
    val cacheFile = File(cacheDir, fileName)

    resolver.openInputStream(uri).use { input ->
        FileOutputStream(cacheFile).use { output ->
            input?.copyTo(output)
        }
    }

    return cacheFile.absolutePath
}

fun queryFileName(activity: Activity, uri: Uri): String? {
    val cursor = activity.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (it.moveToFirst() && nameIndex != -1) {
            return it.getString(nameIndex)
        }
    }
    return null
}

fun clearCacheDir(dir: File?): Boolean {
    if (dir == null || !dir.exists()) return true
    var success = true
    dir.listFiles()?.forEach { file ->
        success = if (file.isDirectory) {
            clearCacheDir(file)
        } else {
            file.delete()
        } && success
    }
    return success
}