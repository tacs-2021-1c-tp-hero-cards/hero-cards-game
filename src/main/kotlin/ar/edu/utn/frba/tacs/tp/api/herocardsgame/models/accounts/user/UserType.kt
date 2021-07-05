package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user

enum class UserType {
    HUMAN, IA;

    companion object {
        fun existUserType(userType: String): Boolean = values().any { it.name == userType }
    }
}