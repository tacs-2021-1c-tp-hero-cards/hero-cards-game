package ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception

import java.lang.RuntimeException

data class InvalidDeckVersionException(val deckId: Long, val deckVersion: Long) : RuntimeException() {
    override val message: String = "Not exist deck with id: $deckId and version: $deckVersion"
}