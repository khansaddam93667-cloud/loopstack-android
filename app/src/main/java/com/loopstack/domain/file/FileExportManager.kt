package com.loopstack.domain.file

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileExportManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun exportCodeSnippet(code: String, filename: String = "snippet_${System.currentTimeMillis()}.txt"): Uri? {
        val resolver = context.contentResolver
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/LoopStack")
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write(code.toByteArray())
                }
            }
            uri
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val loopStackDir = File(downloadsDir, "LoopStack")
            if (!loopStackDir.exists()) {
                loopStackDir.mkdirs()
            }
            val file = File(loopStackDir, filename)
            FileOutputStream(file).use { outputStream ->
                outputStream.write(code.toByteArray())
            }
            Uri.fromFile(file)
        }
    }
}
