package com.example.flutter_saf.src

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.example.flutter_saf.src.converters.mapFromDocumentFile
import com.example.flutter_saf.src.converters.mapFromUriPermission
import com.example.flutter_saf.src.utils.clearCacheDir
import com.example.flutter_saf.src.utils.copyToCache
import com.example.flutter_saf.src.utils.getAudioThumbnail
import com.example.flutter_saf.src.utils.getImageThumbnail
import com.example.flutter_saf.src.utils.getPersistedUriPermissions
import com.example.flutter_saf.src.utils.getVideoThumbnail
import com.example.flutter_saf.src.utils.readFileContent
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import android.util.Log

class FlutterSafMethodHandler(private val activity: Activity) : MethodChannel.MethodCallHandler {

    private val REQUEST_DOCUMENT_TREE_CODE = 1
    private var pendingResult: MethodChannel.Result? = null
    private var requestedUri: Uri? = null

    override fun onMethodCall(
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        when (call.method) {
            "getPlatformName" -> result.success("Android ${android.os.Build.VERSION.RELEASE}")
            "requestDirectoryAccess" -> requestDirectoryAccess(call, result)
            "getPersistedDirectoryPermissions" -> getPersistedDirectoryPermissions(result)
            "hasPersistedDirectoryPermission" -> hasPersistedDirectoryPermission(call, result)
            "releasePersistedDirectoryPermissions" -> releasePersistedDirectoryPermissions(call, result)
            "getContent" -> getContent(call, result)
            "getFilesUri" -> getFilesUri(call, result)
            "getThumbnail" -> getThumbnail(call, result)
            "cache" -> cache(call, result)
            "clearCache" -> clearCache(call, result)
            else -> result.notImplemented()
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == REQUEST_DOCUMENT_TREE_CODE) {
            val result = pendingResult ?: return true
            handleDirectoryResult(resultCode, data, result)
            pendingResult = null
            return true
        }
        return false
    }

    private fun handleDirectoryResult(
        resultCode: Int,
        data: Intent?,
        result: MethodChannel.Result
    ) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            result.success(null)
            return
        }

        val treeUri = data.data
        if (treeUri == null) {
            result.error("INVALID_URI", "No URI returned", null)
            return
        }

        val requestedId = DocumentsContract.getTreeDocumentId(requestedUri)
        val returnedId = DocumentsContract.getTreeDocumentId(treeUri)

        if (requestedId != returnedId) {
            result.error("URI_MISMATCH", "Selected folder is not the requested one", null)
            return
        }

        val allowWriteAccess = data.getBooleanExtra("allowWriteAccess", false)
        try {
            takeUriPermission(activity.applicationContext, treeUri, allowWriteAccess)
            result.success(true)
        } catch (e: SecurityException) {
            result.error("PERMISSION_DENIED", "Cannot persist permission: ${e.message}", null)
        }
    }

    private fun takeUriPermission(context: Context, uri: Uri, allowWriteAccess: Boolean) {
        var flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        if (allowWriteAccess) {
            flags = flags or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        }
        context.contentResolver.takePersistableUriPermission(uri, flags)
    }

    /// requestDirectoryAccess
    fun requestDirectoryAccess(call: MethodCall, result: MethodChannel.Result) {
        if (pendingResult != null) {
            result.error("ALREADY_PENDING", "Another request is in progress", null)
            return
        }

        val stringUri = call.argument<String>("uri")
        val allowWriteAccess = call.argument<Boolean?>("allowWriteAccess") ?: false
        requestedUri = stringUri?.toUri()
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        if (requestedUri != null) {
            Log.i("FlutterSafMethodHandler", requestedUri.toString())
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, requestedUri)
        }
        intent.putExtra("allowWriteAccess", allowWriteAccess)
        pendingResult = result
        activity.startActivityForResult(intent, REQUEST_DOCUMENT_TREE_CODE)
    }
    /// END: requestDirectoryAccess

    private fun releasePersistedDirectoryPermissions(call: MethodCall, result: MethodChannel.Result) {
        val uri = call.argument<String>("uri")?.toUri()
        if (uri == null) {
            result.error("INVALID_URI", "Invalid URI", null)
            return
        }

        val resolver = activity.applicationContext.contentResolver
        val permission = resolver.persistedUriPermissions.find { it.uri == uri }

        if (permission == null) {
            result.error("NOT_FOUND", "URI not found in persisted permissions", null)
            return
        }

        val flags = (if (permission.isReadPermission) Intent.FLAG_GRANT_READ_URI_PERMISSION else 0) or
                (if (permission.isWritePermission) Intent.FLAG_GRANT_WRITE_URI_PERMISSION else 0)

        try {
            resolver.releasePersistableUriPermission(uri, flags)
            result.success(null)
        } catch (e: SecurityException) {
            result.error("SECURITY_EXCEPTION", "Failed to release permission: ${e.message}", null)
        }
    }

    /// getPersistedDirectoryPermissions
    private fun getPersistedDirectoryPermissions(result: MethodChannel.Result) {
        val context = activity.applicationContext
        val persistedUris = getPersistedUriPermissions(context)
        val urisMap = persistedUris.map { mapFromUriPermission(it) }
        result.success(urisMap)
    }
    /// END: getPersistedDirectoryPermissions

    /// hasPersistedDirectoryPermission
    private fun hasPersistedDirectoryPermission(call: MethodCall, result: MethodChannel.Result) {
        val stringUri = call.argument<String>("uri")
        val context = activity.applicationContext
        val persistedUris = getPersistedUriPermissions(context)
        val uri = stringUri?.toUri()
        val hasPermission = persistedUris.any { it.uri == uri }
        result.success(hasPermission)

    }
    /// END: getPersistedDirectoryPermissions

    /// getThumbnail
    fun getThumbnail(call: MethodCall, result: MethodChannel.Result) {
        val stringUri = call.argument<String>("uri")
        val width = call.argument<Int?>("width")
        val height = call.argument<Int?>("height")

        if (stringUri == null) {
            result.error("INVALID_URI", "URI is null", null)
            return
        }

        val uri = stringUri.toUri()

        val context = activity.applicationContext
        val mimeType = context.contentResolver.getType(uri) ?: ""
        val bitmap: Bitmap? = when {
            mimeType.startsWith("image") -> getImageThumbnail(context, uri, width, height)
            mimeType.startsWith("video") -> getVideoThumbnail(context, uri, width, height)
            mimeType.startsWith("audio") -> getAudioThumbnail(context, uri, width, height)
            else -> null
        }
        if (bitmap == null) {
            result.error("THUMBNAIL_ERROR", "Unsupported file type or no thumbnail", null)
        } else {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            result.success(stream.toByteArray())
        }
    }
    /// END: getThumbnail

    /// getFilesUri
    private fun getFilesUri(call: MethodCall, result: MethodChannel.Result) {
        val stringUri = call.argument<String>("uri") ?: return result.error(
            "INVALID_URI", "Invalid uri", null
        )
        val pickedDir = DocumentFile.fromTreeUri(activity.applicationContext, stringUri.toUri())
            ?: return result.error(
                "INVALID_URI", "Invalid uri", null
            )
        val files = pickedDir.listFiles()
        if (files.isEmpty()) {
            return result.success(emptyList<Map<String, Any>>())
        }
        val filesUriList = files.map { mapFromDocumentFile(it) }
        result.success(filesUriList)
    }
    /// END: getFilesUri

    private fun getContent(call: MethodCall, result: MethodChannel.Result) {
        val uri = call.argument<String>("uri")
        if (uri == null) {
            return result.error("INVALID_URI", "Invalid uri", null)
        }
        val content = readFileContent(uri, activity.applicationContext)
        result.success(content)
    }

    private fun cache(call: MethodCall, result: MethodChannel.Result) {
        val stringUri = call.argument<String>("uri")
        if (stringUri == null) {
            return result.error("INVALID_URI", "Invalid uri", null)
        }
        val content = copyToCache(activity, stringUri.toUri())
        result.success(content)
    }

    private fun clearCache(call: MethodCall, result: MethodChannel.Result) {
        val cacheDir = activity.cacheDir
        val cleared = clearCacheDir(cacheDir)
        result.success(cleared)
    }


}