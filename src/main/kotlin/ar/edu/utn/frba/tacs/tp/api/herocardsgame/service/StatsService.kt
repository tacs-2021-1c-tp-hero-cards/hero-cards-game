package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.UserStatsResponse
import org.springframework.stereotype.Service

@Service
class StatsService(private val userIntegration: UserIntegration) {

    fun buildUserStats(userId: String): UserStatsResponse =
        UserStatsResponse(userId, userIntegration.getUserById(userId.toLong()).stats)

    fun buildAllUserStats(): List<UserStatsResponse> =
        userIntegration.getAllUser().map { UserStatsResponse(it.id.toString(), it.stats) }
            .sortedByDescending { it.totalPoint }

}