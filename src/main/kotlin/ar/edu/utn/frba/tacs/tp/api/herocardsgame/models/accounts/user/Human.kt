package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Stats

data class Human(
    override val id: Long? = null,
    override val userName: String,
    val fullName: String,
    val password: String,
    val token: String? = null,
    override val stats: Stats = Stats(),
    override val userType: UserType = UserType.HUMAN
) : User {
    override fun winMatch(): Human = copy(stats = stats.addWinMatch())
    override fun tieMatch(): Human = copy(stats = stats.addTieMatch())
    override fun loseMatch(): Human = copy(stats = stats.addLoseMatch())
    override fun startMatch(): Human = copy(stats = stats.addInProgressMatch())
    override fun endMatch(): Human = copy(stats = stats.decInProgressMatch())
}