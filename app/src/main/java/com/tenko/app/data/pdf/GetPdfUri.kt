package com.tenko.app.data.pdf

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream

fun getPdfUriFromRaw(context: Context, rawResId: Int): Uri {
    val file = File(context.cacheDir, "temp_raw.pdf")

    context.resources.openRawResource(rawResId).use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }

    return file.toUri()
}