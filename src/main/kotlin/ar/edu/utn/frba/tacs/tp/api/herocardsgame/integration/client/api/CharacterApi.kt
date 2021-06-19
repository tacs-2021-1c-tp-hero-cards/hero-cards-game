package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.api

import com.google.gson.Gson

data class CharacterApi(
    val response: String,
    val error: String ?,
    val id: String ?,
    val name: String ?,
    val powerstats: PowerstatsApi ?,
    val biography: BiographyApi ?,
    val appearance: AppearanceApi ?,
    val work: WorkApi ?,
    val connections: ConnectionsApi ?,
    val image: ImageApi ?
){
    override fun toString(): String {
        return Gson().toJson(this)
    }
}