package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game;

data class Player(
        val username: String,
        val availableCards: List<Card>,
        val prizeCards: List<Card> = emptyList()
)