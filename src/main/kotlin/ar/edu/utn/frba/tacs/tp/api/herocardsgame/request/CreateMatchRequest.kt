package ar.edu.utn.frba.tacs.tp.api.herocardsgame.request;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType

data class CreateMatchRequest(
        val userId: String,
        val userType: UserType,
        val deckId: String
)