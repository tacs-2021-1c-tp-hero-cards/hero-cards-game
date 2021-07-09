package ar.edu.utn.frba.tacs.tp.api.herocardsgame.socket.server

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.NextDuelRequest
import com.google.gson.Gson
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import org.springframework.web.util.HtmlUtils

@Controller
class GreetingController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    fun example(message: String): Greeting {

        val nextDuelRequest = Gson().fromJson(message, NextDuelRequest::class.java)

        return Greeting("Hello, $nextDuelRequest!")
    }
}