package ar.edu.utn.frba.tacs.tp.api.herocardsgame.request;

data class CreateMatchRequest(
        val userId: String,
        val userType: String,
        val deckId: String
)