package ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception

import java.lang.RuntimeException

data class InvalidPowerstatsException(val cardId: Long) : RuntimeException() {
    override val message: String = "Card with id: $cardId has invalid powerstats"
}