package com.patilmanas87.pokedexai.Screen
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patilmanas87.pokedexai.R
import com.patilmanas87.pokedexai.model.Pokemon
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailsScreen(viewModel:BakingViewModel= viewModel(),modifier: Modifier=Modifier.fillMaxSize()) {
    val pokemon by viewModel.pokemon.collectAsState()
    val bitmap by viewModel.capturedImage.collectAsState()
    val initialOffsetY = 800f // Initial position of the card below the image
    val topLimit = 0f // The top position limit (adjust as needed)
    val offsetY = remember { mutableStateOf(initialOffsetY) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = "Pokemon Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp),
                contentScale = ContentScale.FillWidth
            )
        }
        Box(
            modifier = Modifier
                .offset { IntOffset(0, offsetY.value.roundToInt()) }
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        val newOffsetY = offsetY.value + delta
                        // Set bounds for the card movement
                        if (newOffsetY in topLimit..initialOffsetY) {
                            offsetY.value = newOffsetY
                        }
                    }
                )
        ) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(30.dp)) {
                Scaffold(
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                            .background(MaterialTheme.colorScheme.background),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Display Pokemon image with rounded corners


                        // Display Pokemon name with modern styling
                        if (pokemon != null) {
                            Text(
                                text = pokemon!!.name,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onBackground
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Display Pokemon description with padding and modern text style
                        if (pokemon != null) {
                            Text(
                                text = pokemon!!.description,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onBackground
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Display Pokemon stats with a card for better aesthetics
                        pokemon?.let { PokemonStats(it) }
                    }
                }
            }
        }
    }

}

@Composable
fun PokemonStats(pokemon: Pokemon) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.large
            )
            .padding(8.dp)
            .clip(MaterialTheme.shapes.large)
            .shadow(elevation = 4.dp, shape = MaterialTheme.shapes.large),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            StatRow(label = "Attack", value = pokemon.attack.toString(), icon = Icons.Default.Star)
            StatRow(label = "Speed", value = pokemon.speed.toString(), icon = Icons.Default.Star)
            StatRow(
                label = "Defense",
                value = pokemon.deffence.toString(),
                icon = Icons.Default.Star
            )
            StatRow(
                label = "Special Attack",
                value = pokemon.special_attack.toString(),
                icon = Icons.Default.Star
            )
            StatRow(
                label = "Special Defense",
                value = pokemon.special_deffence.toString(),
                icon = Icons.Default.Star
            )
        }
    }
}

@Composable
fun StatRow(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            LinearProgressIndicator(
                progress = value.toFloat() / 100,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .height(8.dp)
                    .width(100.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Preview
@Composable
fun PokemonDetailsScreenPreview() {
    PokemonDetailsScreen()
}


