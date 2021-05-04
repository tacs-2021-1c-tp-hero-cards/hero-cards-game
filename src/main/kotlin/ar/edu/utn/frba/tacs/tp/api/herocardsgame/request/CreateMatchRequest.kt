package ar.edu.utn.frba.tacs.tp.api.herocardsgame.request;

data class CreateMatchRequest(
        val userIds: List<String>,
        val deckId: String
)