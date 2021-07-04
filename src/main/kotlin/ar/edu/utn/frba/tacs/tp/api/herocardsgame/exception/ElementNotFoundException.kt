package ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception

import java.lang.RuntimeException

data class ElementNotFoundException(val resource: String, val field: String, val value: String) : RuntimeException() {
    override val message: String = "No $resource found with $field: $value"
}