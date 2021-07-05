package ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception

import java.lang.RuntimeException

data class InvalidDifficultyException(val difficulty: String) : RuntimeException() {
    override val message: String = "Not exist difficulty with name: $difficulty"
}