package com.patilmanas87.pokedexai.navigation



import androidx.camera.view.LifecycleCameraController
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.patilmanas87.pokedexai.Screen.BakingScreen
import com.patilmanas87.pokedexai.Screen.BakingViewModel
import com.patilmanas87.pokedexai.Screen.CameraScreen
import com.patilmanas87.pokedexai.Screen.LoadingScreen
import com.patilmanas87.pokedexai.Screen.PokemonDetailsScreen
import com.patilmanas87.pokedexai.Screen.RecordScreen
import com.patilmanas87.pokedexai.Screen.Screens
import com.patilmanas87.pokedexai.Screen.UserScreen
import com.patilmanas87.pokedexai.model.Pokemon
import kotlinx.coroutines.time.delay
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.time.Duration


@Composable
fun EnterAnimation(content: @Composable () -> Unit) {
    val visibleState = remember { MutableTransitionState(false) }

    LaunchedEffect(Unit) {
        delay(Duration.ofSeconds(1)) // Add your desired delay here in milliseconds
        visibleState.targetState = true
    }

    AnimatedVisibility(
        visibleState = visibleState,
        modifier = Modifier
    ) {
        content()
    }
}

@Composable
fun navGraph(navHostController: NavHostController,controller: LifecycleCameraController,viewModel: BakingViewModel,modifier: Modifier=Modifier.fillMaxSize().padding(top=32.dp)) {
    NavHost(navHostController, Screens.record.route) {
        composable(Screens.camera.route){

                CameraScreen(controller, modifier, viewModel)
        }

        composable(Screens.record.route){
                RecordScreen(viewModel,navHostController)
        }

        composable(Screens.pokemonDetails.route){

                PokemonDetailsScreen(viewModel)

        }
        composable(Screens.user.route)
        {
                BakingScreen(viewModel)
        }
        composable(Screens.history.route)
        {
            UserScreen()
        }
        composable("loading"){
            LoadingScreen()
        }

    }


}


