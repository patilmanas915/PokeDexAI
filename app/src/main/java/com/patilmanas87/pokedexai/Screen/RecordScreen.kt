package com.patilmanas87.pokedexai.Screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.patilmanas87.pokedexai.model.Pokemon
import kotlinx.serialization.json.Json

@Composable
fun RecordScreen(viewModel: BakingViewModel, navHostController: NavHostController) {
    val pokemons by viewModel.pokemonList.collectAsState()
    val context = LocalContext.current.applicationContext
    viewModel.loadAllPokemon(navHostController)

    // Use Material3 Theme
    MaterialTheme {
        Surface(color = Color.Transparent) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Add spacing between items
            ) {
                items(pokemons.size) { index ->
                    val pokemon = pokemons[index]
                    val obj = fromPokemonJson(pokemon.pokemon)
                    PokemonCard(pokemon = obj, onClick = {
                        viewModel.openPokemon(pokemon, context, navHostController)
                    }, index+1)
                }
            }

        }
    }
}

@Composable
fun PokemonCard(pokemon: Pokemon, onClick: () -> Unit, no: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium // Use Material 3 shapes
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Space between number and name
        ) {
            Text(
                text = no.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Box(
                modifier = Modifier
                    .weight(1f) // Make name take remaining space
                    .padding(start = 16.dp) // Optional padding for better spacing
            ) {
                Text(
                    text = pokemon.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Center) // Center the text within the Box
                )
            }
        }


    }
}


fun fromPokemonJson(pokemonJson: String): Pokemon {
    return Json.decodeFromString(pokemonJson)
}

fun fromByteArray(byteArray: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}