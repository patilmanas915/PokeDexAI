package com.patilmanas87.pokedexai.model

import kotlinx.serialization.Serializable


@Serializable
data class Pokemon(
    val name: String,
    val description: String,
    val type: String,
    val attack: String,
    val speed: String,
    val deffence: String,
    val special_attack: String,
    val special_deffence: String
) {
}
