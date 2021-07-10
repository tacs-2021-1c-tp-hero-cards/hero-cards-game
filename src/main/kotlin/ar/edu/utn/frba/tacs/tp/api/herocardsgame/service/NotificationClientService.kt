package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class NotificationClientService(val userIntegration: UserIntegration, val template: SimpMessagingTemplate) {

    fun notifyCreateMatch(userId: String, match: Match) {
        val token = userIntegration.searchHumanUserByIdUserNameFullNameOrToken(userId).first().token
        if (token != null) {
            this.template.convertAndSend("/topic/user/$token", match)
        }
    }

}