package ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import java.lang.RuntimeException

data class InvalidDuelTypeException(val duelType: DuelType) : RuntimeException() {
    override val message: String = "$duelType is not a valid type of duel"
}