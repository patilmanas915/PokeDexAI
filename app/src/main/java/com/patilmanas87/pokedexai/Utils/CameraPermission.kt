package com.patilmanas87.pokedexai.Utils

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermissionRequest(
    onPermissionGranted: @Composable () -> Unit,
    onPermissionDenied: @Composable () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    when (cameraPermissionState.status) {
        is PermissionStatus.Granted -> {
            onPermissionGranted()
        }
        is PermissionStatus.Denied -> {
            Column {
                Text("Camera permission is required to use this feature.")
                Button(onClick = {
                    cameraPermissionState.launchPermissionRequest()
                }) {
                    Text("Request Camera Permission")
                }
                onPermissionDenied()
            }
        }
    }
}
