package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.NotifyResponse
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class NotificationClientService(val userIntegration: UserIntegration, val template: SimpMessagingTemplate) {

    fun notifyCreateMatch(userId: String, userType: UserType, match: Match) {
        if (userType == UserType.HUMAN) {
            val user = userIntegration.searchHumanUserByIdUserNameFullNameOrToken(userId).firstOrNull()
                ?: throw ElementNotFoundException("human user", "id", userId)
            val token = user.token

            if (token != null) {
                this.template.convertAndSend("/topic/user/$token/notifications", NotifyResponse(match.id!!, user))
            }
        }
    }

    fun notifyConfirmMatch(token: String, match: Match) {
        match.players
            .filter { it.user.userType == UserType.HUMAN }
            .map { userIntegration.searchHumanUserByIdUserNameFullNameOrToken(it.user.id!!.toString()).first() }
            .filter { it.token != null && it.token != token }
            .map {
                val destination =
                    if (match.status == MatchStatus.IN_PROGRESS) {
                        "confirmations"
                    } else {
                        "rejections"
                    }

                this.template.convertAndSend("/topic/user/$token/$destination", NotifyResponse(match.id!!, it))
            }
    }

    fun notifyResultDuel(match: Match) {
        val user = match.players.first().user

        if (user.userType == UserType.HUMAN) {
            val token =
                userIntegration.searchHumanUserByIdUserNameFullNameOrToken(user.id!!.toString()).first().token

            val duelResult = match.duelHistoryList.last()
            val opponentDuelResult = duelResult.copy(
                player = duelResult.opponent,
                opponent = duelResult.player,
                duelResult = duelResult.duelResult.calculateOppositeResult()
            )

            this.template.convertAndSend("/topic/user/$token/nextDuel", opponentDuelResult)
        }
    }

    fun notifyAbort(token: String, match: Match) {
        match.players
            .filter { it.user.userType == UserType.HUMAN }
            .map { userIntegration.searchHumanUserByIdUserNameFullNameOrToken(it.user.id!!.toString()).first() }
            .filter { it.token != null && it.token != token }
            .map {
                this.template.convertAndSend("/topic/user/$token/abortions", NotifyResponse(match.id!!, it))
            }
    }
}