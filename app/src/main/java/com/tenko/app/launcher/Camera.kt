package com.tenko.app.launcher

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
fun rememberCameraLauncher(
    uri: Uri,
    onImageCaptured: (Boolean) -> Unit
) : ActivityResultLauncher<Uri> {

    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        onImageCaptured(success)
    }
}