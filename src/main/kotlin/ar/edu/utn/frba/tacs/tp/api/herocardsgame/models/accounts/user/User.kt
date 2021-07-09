package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Stats

interface User {
    val id: Long?
    val userName: String
    val stats: Stats
    val userType: UserType

    fun winMatch(): User
    fun tieMatch(): User
    fun loseMatch(): User
    fun startMatch(): User
    fun endMatch(): User
}