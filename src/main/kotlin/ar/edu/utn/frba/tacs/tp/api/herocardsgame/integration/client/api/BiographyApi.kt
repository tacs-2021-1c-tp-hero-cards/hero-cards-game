package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.api

data class BiographyApi(
    val response: String,
    val error: String,
    val fullName: String,
    val alterEgos: String,
    val aliases: List<String>,
    val placeOfBirth: String,
    val firstAppearance: String,
    val publisher: String,
    val alignment: String
)