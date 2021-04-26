package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game;

data class Card(
        val id: Long,
        val name: String,
        val powerstats: Powerstats
)