package ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception

import java.lang.RuntimeException

data class InvalidUserException(val userName: String, val fullName: String) : RuntimeException() {
    override val message: String = "User already exists with this username: $userName and fullname: $fullName"
}