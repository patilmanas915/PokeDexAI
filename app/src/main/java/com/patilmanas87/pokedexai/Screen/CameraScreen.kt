package com.patilmanas87.pokedexai.Screen

import android.widget.Toast
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.patilmanas87.pokedexai.R
import com.patilmanas87.pokedexai.Utils.CameraPermissionRequest

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier.fillMaxSize(),
    viewModel: BakingViewModel
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(top = 16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CameraPermissionRequest(
            onPermissionGranted = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.circle_padding_shadow_no_bg),
                        contentDescription = null,
                        modifier = Modifier
                            .size(420.dp)
                            .clip(CircleShape)
                    )
                    if(false){}
                    CameraPreview(controller, Modifier.size(320.dp))
                }
            },
            onPermissionDenied = {
                Text("Please grant camera permission to proceed.")
            }
        )
    }
}

@Preview
@Composable
fun CameraScreenPreview() {
    Box(Modifier.fillMaxSize()) {
        Image(painter = painterResource(R.drawable.camera), null, modifier = Modifier.fillMaxSize())
        Image(
            painter = painterResource(R.drawable.circle_padding_shadow_no_bg),
            null,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .align(Alignment.Center)
        )

    }

}

@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        },
        modifier = modifier.clip(CircleShape)
    )
}