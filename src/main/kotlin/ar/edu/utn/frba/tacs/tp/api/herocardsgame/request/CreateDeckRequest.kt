package ar.edu.utn.frba.tacs.tp.api.herocardsgame.request

data class CreateDeckRequest(
    val deckName: String,
    val cardIds: List<String>
)