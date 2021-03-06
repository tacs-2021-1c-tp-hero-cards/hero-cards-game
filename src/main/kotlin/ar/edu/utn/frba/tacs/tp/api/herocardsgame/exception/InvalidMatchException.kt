package ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception

import java.lang.RuntimeException

data class InvalidMatchException(val matchId: Long, val status: String) : RuntimeException() {
    override val message: String = "Match with the id: $matchId is $status"
}