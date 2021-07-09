package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidUserTypeException

enum class UserType {
    HUMAN, IA;

    companion object {
        fun getUserType(userType: String): UserType =
            try {
                valueOf(userType)
            } catch (e: IllegalArgumentException) {
                throw InvalidUserTypeException(userType)
            }
    }
}