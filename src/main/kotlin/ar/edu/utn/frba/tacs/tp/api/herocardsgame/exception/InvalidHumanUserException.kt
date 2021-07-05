package ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception

import java.lang.RuntimeException

data class InvalidHumanUserException(val userName: String, val fullName: String) : RuntimeException() {
    override val message: String = "Human user already exists with this username: $userName and fullname: $fullName"
}