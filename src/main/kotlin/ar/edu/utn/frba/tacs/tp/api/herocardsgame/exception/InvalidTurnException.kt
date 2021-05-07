package ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception

import java.lang.RuntimeException

data class InvalidTurnException(val token: String) : RuntimeException() {
    override val message: String = "It is not the turn of the user with token: $token"
}