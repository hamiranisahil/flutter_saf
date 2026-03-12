package com.example.flutter_saf.src.converters

import android.content.UriPermission
import androidx.documentfile.provider.DocumentFile

fun mapFromDocumentFile(documentFile: DocumentFile): Map<String, Any?> {
    return mapOf(
        "name" to documentFile.name,
        "uri" to documentFile.uri.toString(),
        "isDirectory" to documentFile.isDirectory,
        "isFile" to documentFile.isFile,
        "type" to documentFile.type,
        "length" to documentFile.length(),
        "lastModified" to documentFile.lastModified(),
    )
}

fun mapFromUriPermission(uriPermission: UriPermission): Map<String, Any?> {
    return mapOf(
        "uri" to uriPermission.uri.toString(),
        "isReadPermission" to uriPermission.isReadPermission,
        "isWritePermission" to uriPermission.isWritePermission,
        "persistedTime" to uriPermission.persistedTime,
    )
}