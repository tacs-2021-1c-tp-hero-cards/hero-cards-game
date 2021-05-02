package ar.edu.utn.frba.tacs.tp.api.herocardsgame.request

data class UpdateDeckRequest(
    val deckName: String?,
    val deckCards: List<String> = emptyList()
)