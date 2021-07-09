package ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception

import java.lang.RuntimeException

data class InvalidIAUserException(val userName: String, val difficulty: String) : RuntimeException() {
    override val message: String = "IA user already exists with this username: $userName and difficulty: $difficulty"
}