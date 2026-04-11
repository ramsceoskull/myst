package com.tenko.myst.ui.screen

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.io.File

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    pdfUrl: String,
//    pdfResId: Int,
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {
    var accepted by remember { mutableStateOf(false) }
    var hasScrolledToEnd by remember { mutableStateOf(false) }
    var scrollState = rememberScrollState()
    val scrollPosition = scrollState.value
    val contentHeight = 1000 // Ajusta a la altura de tu contenido

    /*val context = LocalContext.current

    val pdfFile = remember {
        val file = File(context.cacheDir, "terms.pdf")
        if (!file.exists()) {
            context.resources.openRawResource(pdfResId).use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
        file
    }*/

    /*ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 📄 VISOR PDF PRO
            AndroidView(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                factory = { ctx ->
                    PDFView(ctx).apply {
                        fromFile(pdfFile)
                            .enableSwipe(true)
                            .swipeHorizontal(false)
                            .enableDoubletap(true)
                            .onPageChange { page, pageCount ->
                                if (page == pageCount - 1) {
                                    hasScrolledToEnd = true
                                }
                            }
                            .load()
                    }
                }
                *//*factory = { ctx ->
                    com.github.barteksc.pdfviewer.PDFView(ctx, null).apply {
                        fromFile(pdfFile)
                            .enableSwipe(true)
                            .swipeHorizontal(false)
                            .enableDoubletap(true)
                            .onPageChange { page, pageCount ->
                                if (page == pageCount - 1) {
                                    hasScrolledToEnd = true
                                }
                            }
                            .load()
                    }
                }*//*
            )

            // 🔽 CONTROLES FIJOS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .padding(16.dp)
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = accepted,
                        onCheckedChange = {
                            if (hasScrolledToEnd) accepted = it
                        },
                        enabled = hasScrolledToEnd
                    )

                    Text(
                        if (hasScrolledToEnd)
                            "Acepto los términos y condiciones"
                        else
                            "Debes leer todo el documento para aceptar",
                        color = if (hasScrolledToEnd) Color.Black else Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onAccept,
                    enabled = accepted,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Aceptar y continuar")
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        }
    }*/

    Column(modifier = Modifier.fillMaxSize()) {

        // 🔥 VISOR PDF
        AndroidView(
            modifier = Modifier.weight(1f),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    loadUrl("https://drive.google.com/file/d/1ldgW0gvh45ZUmDggg7RZR_frIdMENyOx/view")
//                    loadUrl("file:///android_res/raw/terms")
//                    loadUrl(pdfUrl)
//                    loadUrl("https://docs.google.com/gview?embedded=true&url=$pdfUrl")
                }
            }
        )

        // 🔽 CONTROLES
        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = accepted,
                    onCheckedChange = { accepted = it }
                )
                Text("Acepto los términos y condiciones")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onAccept,
                enabled = accepted && scrollPosition >= (contentHeight - 100), // Ajusta el umbral, // 🔒 BLOQUEADO SI NO ACEPTA
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continuar")
            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}

/*
@Composable
fun TérminosYCondicionesScreen(onAceptar: () -> Unit) {
    var scrollState = rememberScrollState()
    val scrollPosition = scrollState.value
    val contentHeight = 1000 // Ajusta a la altura de tu contenido

    Column(modifier = Modifier.fillMaxSize()) {
        // Muestra el PDF aquí
        AndroidView(factory = { context ->
            PDFView(context).apply {
                fromAsset("terminos_y_condiciones.pdf") // Cambia por tu ruta
                load()
            }
        }, modifier = Modifier.weight(1f))

        // Botón Aceptar, habilitado solo si scrollea hasta el final
        Button(
            onClick = onAceptar,
            enabled = scrollPosition >= (contentHeight - 100) // Ajusta el umbral
        ) {
            Text("Aceptar Términos y Condiciones")
        }
    }
}*/
