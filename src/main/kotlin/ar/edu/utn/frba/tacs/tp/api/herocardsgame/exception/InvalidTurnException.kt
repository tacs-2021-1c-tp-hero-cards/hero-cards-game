package ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception

import java.lang.RuntimeException

data class InvalidTurnException(val token: String? = null, val userName: String? = null) : RuntimeException() {
    override val message: String =
        if (token != null) {
            "It is not the turn of the user with token: $token"
        } else {
            "It is not the turn of the IA with userName: $userName"
        }
}