package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Stats
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty

class IAEntity(id: Long? = null, ia: IA) {
    val id: Long = id ?: ia.id!!
    val userName: String = ia.userName
    val winCount: Int = ia.stats.winCount
    val tieCount: Int = ia.stats.tieCount
    val loseCount: Int = ia.stats.loseCount
    val inProgressCount: Int = ia.stats.inProgressCount
    val duelDifficulty = ia.difficulty.name

    fun toModel(): IA =
        IA(id, userName, Stats(winCount, tieCount, loseCount, inProgressCount), IADifficulty.valueOf(duelDifficulty))
}