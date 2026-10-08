package com.deskframe.digitalphotoframe.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object PhotoStorageHelper {

    suspend fun importAndPersistUris(context: Context, uris: List<Uri>): List<String> = withContext(Dispatchers.IO) {
        val storageDir = File(context.filesDir, "imported_photos").apply { mkdirs() }
        val resultList = mutableListOf<String>()

        for (uri in uris) {
            try {
                if (uri.scheme == "file") {
                    resultList.add(uri.toString())
                    continue
                }

                // Copy stream to private app storage to guarantee permanent offline access
                val extension = getExtension(context, uri) ?: "jpg"
                val targetFile = File(storageDir, "photo_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.$extension")

                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }

                if (targetFile.exists() && targetFile.length() > 0) {
                    resultList.add(Uri.fromFile(targetFile).toString())
                } else {
                    // Fallback to original URI if copy fails
                    resultList.add(uri.toString())
                }
            } catch (_: Exception) {
                resultList.add(uri.toString())
            }
        }
        resultList
    }

    private fun getExtension(context: Context, uri: Uri): String? {
        val mime = context.contentResolver.getType(uri)
        return when (mime) {
            "image/png" -> "png"
            "image/webp" -> "webp"
            "image/gif" -> "gif"
            else -> "jpg"
        }
    }

    fun deleteLocalPhoto(uriString: String) {
        try {
            val uri = Uri.parse(uriString)
            if (uri.scheme == "file") {
                uri.path?.let { File(it).delete() }
            }
        } catch (_: Exception) {
        }
    }

    fun clearAllImportedPhotos(context: Context) {
        try {
            val storageDir = File(context.filesDir, "imported_photos")
            if (storageDir.exists() && storageDir.isDirectory) {
                storageDir.listFiles()?.forEach { it.delete() }
            }
        } catch (_: Exception) {
        }
    }
}
