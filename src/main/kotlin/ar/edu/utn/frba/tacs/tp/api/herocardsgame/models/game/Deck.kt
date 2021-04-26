package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game;

data class Deck(
        val id: Long,
        val name: String,
        val cards: List<Card>
)