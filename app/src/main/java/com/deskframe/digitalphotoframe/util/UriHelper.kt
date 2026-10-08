package com.deskframe.digitalphotoframe.util

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract

object UriHelper {

    fun takePersistableUriPermission(contentResolver: ContentResolver, uri: Uri) {
        try {
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, flags)
        } catch (_: SecurityException) {
            // Some providers don't support persistable permissions, continue gracefully
        }
    }

    fun listImagesFromTreeUri(context: Context, treeUri: Uri): List<String> {
        val result = mutableListOf<String>()
        try {
            takePersistableUriPermission(context.contentResolver, treeUri)

            val documentId = DocumentsContract.getTreeDocumentId(treeUri)
            val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, documentId)

            val projection = arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_MIME_TYPE
            )

            context.contentResolver.query(
                childrenUri,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                val idIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                val mimeIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE)

                while (cursor.moveToNext()) {
                    val childDocId = cursor.getString(idIndex)
                    val mimeType = cursor.getString(mimeIndex)

                    if (mimeType != null && mimeType.startsWith("image/")) {
                        val fileUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, childDocId)
                        result.add(fileUri.toString())
                    }
                }
            }
        } catch (_: Exception) {
            // Fallback: if tree query fails, return empty list
        }
        return result
    }
}
