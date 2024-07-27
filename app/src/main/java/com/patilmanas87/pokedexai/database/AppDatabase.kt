package com.patilmanas87.pokedexai.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PokemonDB::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
}
