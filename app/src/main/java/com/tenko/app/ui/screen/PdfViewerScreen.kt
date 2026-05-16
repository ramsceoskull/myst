package com.tenko.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.barteksc.pdfviewer.PDFView
import com.tenko.app.R
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.RaisinBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    pdfResId: Int,
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var accepted by rememberSaveable { mutableStateOf(false) }
    var reachedEnd by rememberSaveable { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        painter = painterResource(R.drawable.xmark_solid_full),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = RaisinBlack
                    )
                }

                Text(
                    text = "Términos y condiciones",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                factory = { ctx ->
                    PDFView(ctx, null).apply {
                        fromStream(context.resources.openRawResource(pdfResId))
                            .enableSwipe(true)
                            .swipeHorizontal(false)
                            .pageSnap(true)
                            .pageFling(true)
                            .autoSpacing(true)
                            .spacing(12)
                            .enableAntialiasing(true)
                            .enableDoubletap(true)
                            .defaultPage(0)
                            .onPageChange { page, totalPages ->
                                reachedEnd =
                                    page == totalPages - 1
                            }
                            .load()
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AntiFlashWhite)
                    .padding(16.dp)
            ) {
                Text(
                    text =
                        if (reachedEnd) "Ya puedes aceptar los términos."
                        else "Debes leer el documento completo.",
                    color =
                        if (reachedEnd) Color.Black
                        else Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = accepted,
                        onCheckedChange = { accepted = it },
                        enabled = reachedEnd
                    )

                    Text(text = "Acepto los términos y condiciones")
                }

                Spacer(modifier = Modifier.height(16.dp))

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
    }
}
