package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel

enum class DuelResult {

    WIN, LOSE, TIE;

    fun calculateOppositeResult(): DuelResult =
        when (this) {
            WIN -> LOSE
            LOSE -> WIN
            else -> TIE
        }

}