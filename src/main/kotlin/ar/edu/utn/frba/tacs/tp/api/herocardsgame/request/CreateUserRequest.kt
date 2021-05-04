package ar.edu.utn.frba.tacs.tp.api.herocardsgame.request

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.HashService

data class CreateUserRequest(
    val userName: String,
    val fullName: String,
    val password: String
) {
    fun buildPasswordHash() = HashService.calculatePasswordHash(userName, password)
}