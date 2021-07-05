package ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception

import java.lang.RuntimeException

data class InvalidUserTypeException(val userType: String) : RuntimeException() {
    override val message: String = "Not exist userType with name: $userType"
}