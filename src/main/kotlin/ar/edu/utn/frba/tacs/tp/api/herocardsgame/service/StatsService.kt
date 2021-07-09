package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.UserStatsResponse
import org.springframework.stereotype.Service

@Service
class StatsService(private val userIntegration: UserIntegration) {

    fun buildUserStats(userId: String, userType: String): UserStatsResponse {
        val userById = userIntegration.getUserById(userId.toLong(), UserType.getUserType(userType))
        return UserStatsResponse(userId, userById.userName, userType, userById.stats)
    }

    fun buildAllUserStats(): List<UserStatsResponse> =
        userIntegration.getAllUser()
            .map { UserStatsResponse(it.id.toString(), it.userName, it.userType.name, it.stats) }
            .sortedByDescending { it.totalPoint }

}