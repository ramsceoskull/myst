package com.tenko.app.data.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

suspend fun loadPdfPages(
    context: Context,
    uri: Uri
): List<Bitmap> = withContext(Dispatchers.IO) {
    val file = File(
        context.cacheDir,
        "temp.pdf"
    )

    context.contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }

    val parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    val renderer = PdfRenderer(parcelFileDescriptor)
    val bitmaps = mutableListOf<Bitmap>()

    for (i in 0 until renderer.pageCount) {
        val page = renderer.openPage(i)
        val bitmap = createBitmap(page.width * 2, page.height * 2)

        page.render(
            bitmap,
            null,
            null,
            PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
        )

        bitmaps.add(bitmap)
        page.close()
    }

    renderer.close()
    parcelFileDescriptor.close()

    bitmaps
}