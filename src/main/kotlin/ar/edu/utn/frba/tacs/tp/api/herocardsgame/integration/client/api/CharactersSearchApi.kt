package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.api

data class CharactersSearchApi(
    val response: String,
    val error: String ?,
    val resultsFor: String,
    val results: List<CharacterApi>
)