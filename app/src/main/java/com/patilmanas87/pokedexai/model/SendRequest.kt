package com.patilmanas87.pokedexai.model

data class SendRequest(
    val inference_job_token: String,
    val inference_job_token_type: String,
    val success: Boolean
)