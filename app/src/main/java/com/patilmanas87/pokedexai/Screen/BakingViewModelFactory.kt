package com.patilmanas87.pokedexai.Screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.patilmanas87.pokedexai.database.PokemonDao

class BakingViewModelFactory(private val dao: PokemonDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BakingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BakingViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

