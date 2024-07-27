package com.patilmanas87.pokedexai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnimatedNavigate(
    navController: NavController,
    targetRoute: String,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    Column {
        AnimatedVisibility(
            visible = isVisible,
            exit = fadeOut()
        ) {
            content()
        }

        Button(onClick = {
            isVisible = false
            scope.launch {
                delay(500) // Wait for the fade-out animation to complete
                navController.navigate(targetRoute)
            }
        }) {
            Text("Navigate")
        }
    }
}

