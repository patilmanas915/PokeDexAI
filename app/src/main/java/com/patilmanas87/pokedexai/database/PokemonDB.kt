package com.patilmanas87.pokedexai.database

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patilmanas87.pokedexai.model.Pokemon

@Entity(tableName = "pokemon_table")
data class PokemonDB(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pokemon: String,
    val bitmap: String,
    val audioUrl:String
)
