package com.patilmanas87.pokedexai.model

data class Body(
    val inference_text: String,
    val tts_model_token: String,
    val uuid_idempotency_token: String
)