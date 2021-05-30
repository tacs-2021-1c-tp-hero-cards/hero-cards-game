package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Stats
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User

class UserEntity(id: Long? = null, user: User) {
    val id: Long = id ?: user.id!!
    val userName: String = user.userName
    val fullName: String = user.fullName
    val password: String = user.password
    val token: String? = user.token
    val winCount: Int = user.stats.winCount
    val tieCount: Int = user.stats.tieCount
    val loseCount: Int = user.stats.loseCount
    val inProgressCount: Int = user.stats.inProgressCount

    fun toModel(): User =
        User(id, userName, fullName, password, token, Stats(winCount, tieCount, loseCount, inProgressCount))
}