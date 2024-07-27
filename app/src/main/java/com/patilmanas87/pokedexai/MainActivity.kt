package com.patilmanas87.pokedexai

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.patilmanas87.pokedexai.Screen.BakingViewModel
import com.patilmanas87.pokedexai.Screen.BakingViewModelFactory
import com.patilmanas87.pokedexai.Screen.Screens
import com.patilmanas87.pokedexai.database.AppDatabase
import com.patilmanas87.pokedexai.navigation.navGraph
import com.patilmanas87.pokedexai.ui.theme.PokeDexAITheme
import java.io.ByteArrayOutputStream

class MainActivity : ComponentActivity() {
    val database by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "pokemon_database_2")
            .build()
    }
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pokemonDao = database.pokemonDao()

        val factory = BakingViewModelFactory(pokemonDao)
        val bakingViewModel = ViewModelProvider(this, factory).get(BakingViewModel::class.java)
        setContent {
            PokeDexAITheme {

                val navController = rememberNavController()

                val currentRoute =
                    navController.currentBackStackEntryAsState().value?.destination?.route
                val controller = remember {
                    LifecycleCameraController(applicationContext).apply {
                        setEnabledUseCases(
                            CameraController.IMAGE_CAPTURE or
                                    CameraController.VIDEO_CAPTURE
                        )
                    }
                }
                val context = LocalContext.current

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,

                    ) {
                    Scaffold(
                        Modifier
                            .fillMaxSize()
                            .fillMaxWidth(),
                        topBar = {
                            Row {
                                Image(
                                    painter = painterResource(R.drawable.top_bar),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }, bottomBar = {
                            if (currentRoute == "loading") {
                            } else if (currentRoute != Screens.camera.route) {
                                Bottombar(navController)
                            } else {
                                BottombarCam(navController, controller, context, bakingViewModel)
                            }
                        }

                    ) { it ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()

                        ) {


                            if (currentRoute != Screens.camera.route) {
                                Image(
                                    painter = painterResource(R.drawable.bg_pokeball),
                                    null, alpha = 0.05F,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(300.dp)
                                        .padding(
                                            start = 50.dp
                                        )
                                )
                            } else {
                                Image(
                                    painter = painterResource(R.drawable.camera),
                                    null,
                                    modifier = Modifier
                                        .fillMaxSize(), contentScale = ContentScale.FillWidth
                                )
                            }
                            if (currentRoute == "loading" ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {
                                    navGraph(navController, controller, viewModel = bakingViewModel)
                                }
                            }else if(currentRoute == Screens.pokemonDetails.route){
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top=80.dp)
                                ) {
                                    navGraph(navController, controller, viewModel = bakingViewModel)}

                            }
                            else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(it)
                                ) {
                                    navGraph(navController, controller, viewModel = bakingViewModel)
                                }
                            }

                        }

                    }

                }
            }
        }
    }


}


@Composable
fun Bottombar(
    navigationController: NavController
) {
    val selected = remember { mutableStateOf(Icons.Default.Home) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
    ) {
        BottomAppBar(
            containerColor = Color.White,
            modifier = Modifier
                .graphicsLayer(
                    shadowElevation = 100f,
                    clip = false
                )
                .fillMaxWidth()
                .height(65.dp)
                .align(Alignment.BottomStart)
        ) {
            val items = listOf(
                Triple(Icons.Filled.Camera, "Camera", Screens.camera.route),
                Triple(Icons.Filled.CatchingPokemon, "Pokemon", Screens.pokemonDetails.route),
                Triple(Icons.Filled.Assignment, "Record", Screens.record.route),
                Triple(Icons.Filled.AccountCircle, "Account", Screens.user.route)
            )
            var count = 0

            items.forEach { (icon, label, route) ->
                count++
                if (count == 3) {
                    Spacer(modifier = Modifier.weight(1f))
                }
                IconButton(
                    onClick = {
                        selected.value = icon
                        navigationController.navigate(route) {
                            popUpTo(Screens.record.route)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = if (selected.value == icon) Color(0xFFDE3039) else Color.LightGray
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (selected.value == icon) Color(0xFFDE3039) else Color.LightGray
                        )
                    }
                }
            }
        }

        IconButton(
            onClick = {
                navigationController.navigate(Screens.camera.route) {
                    popUpTo(Screens.record.route)
                }
            },
            modifier = Modifier
                .size(75.dp)
                .align(Alignment.TopCenter)
                .padding(top = 12.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.imagebluecircle),
                contentDescription = null,
                modifier = Modifier.size(75.dp)
            )
        }
    }

}
fun playSound(context: Context, soundResId: Int) {
    val mediaPlayer = MediaPlayer.create(context, soundResId)
    mediaPlayer.start()
    mediaPlayer.setOnCompletionListener {
        it.release()
    }
}

@Composable
fun BottombarCam(
    navigationController: NavController,
    controller: LifecycleCameraController,
    context: Context,
    BakingViewModel: BakingViewModel
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        BottomAppBar(
            containerColor = Color.LightGray.copy(0.5f),
            modifier = Modifier
                .graphicsLayer(
                    shadowElevation = 100f,
                    clip = false
                )
                .fillMaxWidth()
                .height(65.dp)
                .align(Alignment.BottomStart)
        ) {
            IconButton(
                onClick = {
                    navigationController.navigate(Screens.record.route)
                },
                modifier = Modifier
                    .size(75.dp)
                    .padding()
            ) {
                Icon(
                    Icons.Filled.KeyboardDoubleArrowLeft,
                    contentDescription = null,
                    modifier = Modifier.size(75.dp)
                )
            }
            for (i in 1..5) {
                Spacer(modifier = Modifier.weight(1f))
            }

        }

        IconButton(
            onClick = {
                playSound(context, R.raw.snap)
                controller.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {

                            val buffer = image.planes[0].buffer
                            val bytes = ByteArray(buffer.capacity()).apply { buffer.get(this) }
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
                            val matrix = Matrix().apply { postRotate(90f) }
                            val rotatedBitmap = Bitmap.createBitmap(
                                bitmap,
                                0,
                                0,
                                bitmap.width,
                                bitmap.height,
                                matrix,
                                true
                            )
                            val outputStream = ByteArrayOutputStream()
                            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
                            val compressedBytes = outputStream.toByteArray()
                            val compressedBitmap = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)
                            BakingViewModel.setCapturedImage(compressedBitmap, navigationController,context)
                            navigationController.navigate("loading")
                            image.close()
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Log.e(
                                "CameraCapture",
                                "Capture failed: ${exception.message}",
                                exception
                            )
                        }
                    })
            },
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.TopCenter)
                .padding(top = 12.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.imagebluecircle),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
        }
    }
}

