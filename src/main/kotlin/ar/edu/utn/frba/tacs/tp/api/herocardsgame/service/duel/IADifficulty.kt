package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel

enum class IADifficulty {
    HARD, EASY, RANDOM, HALF;

    companion object {
        fun existDifficulty(difficulty: String): Boolean = values().any { it.name == difficulty }
    }
}