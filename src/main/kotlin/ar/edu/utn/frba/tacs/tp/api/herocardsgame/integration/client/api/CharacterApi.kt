package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.api

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
)