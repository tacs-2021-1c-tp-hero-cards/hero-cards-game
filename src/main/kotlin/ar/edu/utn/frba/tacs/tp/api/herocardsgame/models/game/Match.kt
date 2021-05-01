package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

data class Match(
    val id: Long,
    val players: List<Player>,
    val deck: Deck
)