package ar.edu.utn.frba.tacs.tp.api.herocardsgame.request

data class ActivateUserSessionRequest(
    val userName: String,
    val password: String
)