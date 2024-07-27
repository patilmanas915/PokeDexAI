package com.patilmanas87.pokedexai.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PokemonDao {
    @Insert
    suspend fun insert(pokemon: PokemonDB)

    @Query("SELECT * FROM pokemon_table WHERE id = :id")
    suspend fun getPokemonById(id: Int): PokemonDB?

    @Query("SELECT * FROM pokemon_table")
    suspend fun getAllPokemon(): List<PokemonDB>

    @Query("SELECT Id FROM pokemon_table ORDER BY id DESC LIMIT 1")
    suspend fun getLastPokemon(): Int


    @Query("DELETE FROM pokemon_table WHERE id = :id")
    suspend fun deletePokemonById(id: Int)
}
