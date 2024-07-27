package com.patilmanas87.pokedexai.Screen

sealed class Screens(
     val route: String
){
    object camera:Screens("camera")
    object record:Screens("record")
    object pokemonDetails:Screens("pokemonDetails")
    object user:Screens("user")
    object history:Screens("history")
}