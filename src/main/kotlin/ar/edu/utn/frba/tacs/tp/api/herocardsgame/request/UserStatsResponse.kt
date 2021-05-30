package ar.edu.utn.frba.tacs.tp.api.herocardsgame.request

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Stats

class UserStatsResponse(userId: String, stats: Stats) {
    val id = userId
    val winCount = stats.winCount
    val loseCount = stats.loseCount
    val tieCount = stats.tieCount
    val inProgressCount = stats.inProgressCount
    val totalPoint = stats.calculateTotalPoint()
}