package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.api

import com.google.gson.Gson

data class CharactersSearchApi(
    val response: String,
    val error: String ?,
    val resultsFor: String,
    val results: List<CharacterApi>
){
    override fun toString(): String {
        return Gson().toJson(this)
    }
}