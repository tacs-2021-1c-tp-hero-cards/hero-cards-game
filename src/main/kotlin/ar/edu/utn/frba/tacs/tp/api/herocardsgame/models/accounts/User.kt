package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts

data class User(
    val id: Long? = null,
    val userName: String,
    val fullName: String,
    val password: String,
    val token: String? = null
)