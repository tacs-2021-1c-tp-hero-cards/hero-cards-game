package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Stats
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty

data class IA(
    override val id: Long? = null,
    override val userName: String,
    override val stats: Stats = Stats(),
    val difficulty: IADifficulty,
    override val userType: UserType = UserType.IA
) : User {
    override fun winMatch(): IA = copy(stats = stats.addWinMatch())
    override fun tieMatch(): IA = copy(stats = stats.addTieMatch())
    override fun loseMatch(): IA = copy(stats = stats.addLoseMatch())
    override fun startMatch(): IA = copy(stats = stats.addInProgressMatch())
    override fun endMatch(): IA = copy(stats = stats.decInProgressMatch())
}