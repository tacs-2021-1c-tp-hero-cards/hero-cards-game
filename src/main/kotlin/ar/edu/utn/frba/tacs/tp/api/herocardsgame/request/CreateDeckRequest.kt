package ar.edu.utn.frba.tacs.tp.api.herocardsgame.request

data class CreateDeckRequest(
    val cardName: String,
    val cardIds: List<String>
)