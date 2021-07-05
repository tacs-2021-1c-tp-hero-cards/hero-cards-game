package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Stats
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human

class HumanEntity(id: Long? = null, human: Human) {
    val id: Long = id ?: human.id!!
    val userName: String = human.userName
    val fullName: String = human.fullName
    val password: String = human.password
    val token: String? = human.token
    val winCount: Int = human.stats.winCount
    val tieCount: Int = human.stats.tieCount
    val loseCount: Int = human.stats.loseCount
    val inProgressCount: Int = human.stats.inProgressCount

    fun toModel(): Human =
        Human(id, userName, fullName, password, token, Stats(winCount, tieCount, loseCount, inProgressCount))
}