package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts

data class Stats(
    val winCount: Int = 0,
    val tieCount: Int = 0,
    val loseCount: Int = 0,
    val inProgressCount: Int = 0
) {
    fun addWinMatch(): Stats = copy(winCount = winCount + 1)

    fun addTieMatch(): Stats = copy(tieCount = tieCount + 1)

    fun addLoseMatch(): Stats = copy(loseCount = loseCount + 1)

    fun addInProgressMatch(): Stats = copy(inProgressCount = inProgressCount + 1)

    fun calculateTotalPoint(): Int = winCount * 3 + tieCount
}
