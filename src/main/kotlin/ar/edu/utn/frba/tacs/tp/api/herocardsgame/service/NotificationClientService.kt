package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.DuelHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
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
                this.template.convertAndSend("/topic/user/$token", match)
            }
        }
    }

    fun notifyResultDuel(match: Match) {
        val players = match.players
        val duelResult = match.duelHistoryList.last()

        notifyResultDuel(players.last(), duelResult)
        notifyResultDuel(
            players.first(), duelResult.copy(
                player = duelResult.opponent,
                opponent = duelResult.player,
                duelResult = duelResult.duelResult.calculateOppositeResult()
            )
        )
    }

    private fun notifyResultDuel(player: Player, duelHistory: DuelHistory) {
        if (player.user.userType == UserType.HUMAN) {
            val token =
                userIntegration.searchHumanUserByIdUserNameFullNameOrToken(player.user.id!!.toString()).first().token

            this.template.convertAndSend("/topic/user/$token", duelHistory)
        }
    }

}