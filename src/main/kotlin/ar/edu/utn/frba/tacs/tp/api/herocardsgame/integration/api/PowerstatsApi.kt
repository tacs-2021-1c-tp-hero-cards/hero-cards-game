package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.api

data class PowerstatsApi(
    val response: String,
    val error: String,
    val intelligence: String,
    val strength: String,
    val speed: String,
    val durability: String,
    val power: String,
    val combat: String
)