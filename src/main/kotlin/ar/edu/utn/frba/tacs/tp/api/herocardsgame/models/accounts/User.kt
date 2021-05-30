package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts

data class User(
    val id: Long? = null,
    val userName: String,
    val fullName: String,
    val password: String,
    val token: String? = null,
    val stats: Stats = Stats()
) {
    fun winMatch(): User = copy(stats = stats.addWinMatch())

    fun tieMatch(): User = copy(stats = stats.addTieMatch())

    fun loseMatch(): User = copy(stats = stats.addLoseMatch())

    fun startMatch(): User = copy(stats = stats.addInProgressMatch())
}