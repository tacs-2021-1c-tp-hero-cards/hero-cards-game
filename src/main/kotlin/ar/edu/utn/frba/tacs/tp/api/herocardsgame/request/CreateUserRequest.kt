package ar.edu.utn.frba.tacs.tp.api.herocardsgame.request

data class CreateUserRequest(
    val userName: String,
    val fullName: String,
    val password: String
)