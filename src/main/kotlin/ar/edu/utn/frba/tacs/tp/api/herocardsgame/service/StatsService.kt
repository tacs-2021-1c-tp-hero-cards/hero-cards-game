package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidUserTypeException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.UserStatsResponse
import org.springframework.stereotype.Service

@Service
class StatsService(private val userIntegration: UserIntegration) {

    fun buildUserStats(userId: String, userType: String): UserStatsResponse {
        if (!UserType.existUserType(userType)) {
            throw InvalidUserTypeException(userType)
        }

        val userById = if (userType == UserType.HUMAN.name) {
            userIntegration.getHumanUserById(userId.toLong())
        } else {
            userIntegration.getIAUserById(userId.toLong())
        }

        return UserStatsResponse(userId, userType, userById.stats)
    }

    fun buildAllUserStats(): List<UserStatsResponse> =
        userIntegration.getAllUser().map { UserStatsResponse(it.id.toString(), it.userType.name, it.stats) }
            .sortedByDescending { it.totalPoint }

}