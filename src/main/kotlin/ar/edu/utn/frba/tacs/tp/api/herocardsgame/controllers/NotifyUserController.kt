package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.socket.server.Greeting
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller


@Controller
class NotifyUserController(val template: SimpMessagingTemplate) {

    @MessageMapping("/user/{token}")
    fun notifyCreateMatch(@DestinationVariable token: String, message: String) {
        val greeting = Greeting("Conectate a:, $message!")
        this.template.convertAndSend("/topic/user/$token", greeting)
    }
}