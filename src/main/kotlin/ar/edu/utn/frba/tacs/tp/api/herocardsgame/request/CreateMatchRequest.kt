package ar.edu.utn.frba.tacs.tp.api.herocardsgame.request;

data class CreateMatchRequest(
        val usernames: List<String>,
        val deckId: Long
)