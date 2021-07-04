package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.CardIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.SuperHeroIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.character.Character
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMethod

@Controller
@CrossOrigin(origins = ["http://localhost:3000"], allowedHeaders = ["*"])
class CharacterController(val superHeroIntegration: SuperHeroIntegration) :
    AbstractController<CharacterController>(CharacterController::class.java) {

    /**
     * @param characterId
     * @return character
     */
    @GetMapping("/character/{character-id}")
    fun getCharacter(@PathVariable("character-id") characterId: String): ResponseEntity<Character> =
        try {
            reportRequest(
                method = RequestMethod.GET,
                path = "/character/{character-id}",
                body = null,
                pathVariables = hashMapOf("character-id" to characterId)
            )
            val response = superHeroIntegration.getCharacter(characterId)
            reportResponse(HttpStatus.OK, response)
        } catch (e: ElementNotFoundException) {
            reportError(e, HttpStatus.NOT_FOUND)
        }
}