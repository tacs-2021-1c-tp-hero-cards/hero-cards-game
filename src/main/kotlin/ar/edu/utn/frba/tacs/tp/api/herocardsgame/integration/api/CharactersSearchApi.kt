package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.api

data class CharactersSearchApi(
    val response: String,
    val error: String ?,
    val resultsFor: String,
    val results: List<CharacterApi>
)