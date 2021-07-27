package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.CardIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMethod

@Controller
@CrossOrigin(origins = ["http://localhost:5000"], allowedHeaders = ["*"])
class CardsController(val cardIntegration: CardIntegration) :
    AbstractController<CardsController>(CardsController::class.java) {

    /**
     * @param cardId
     * @return card
     */
    @GetMapping("/cards/{card-id}")
    fun getCard(@PathVariable("card-id") cardId: String): ResponseEntity<Card> =
        try {
            reportRequest(
                method = RequestMethod.GET,
                path = "/cards/{card-id}",
                body = null,
                pathVariables = hashMapOf("card-id" to cardId)
            )
            val response = cardIntegration.getCardById(cardId)
            reportResponse(HttpStatus.OK, response)
        } catch (e: ElementNotFoundException) {
            reportError(e, HttpStatus.NOT_FOUND)
        }

    /**
     * @param cardName
     * @return list of card
     */
    @GetMapping("/cards/search/{card-name}")
    fun getCardByName(@PathVariable("card-name") cardName: String): ResponseEntity<List<Card>> =
        try {
            reportRequest(
                method = RequestMethod.GET,
                path = "/cards/search/{card-name}",
                body = null,
                pathVariables = hashMapOf("card-name" to cardName)
            )
            val response = cardIntegration.searchCardByName(cardName)
            reportResponse(HttpStatus.OK, response)
        } catch (e: ElementNotFoundException) {
            reportError(e, HttpStatus.NOT_FOUND)
        }

    /**
     * @return list of saved cards
     */
    @GetMapping("/cards")
    fun getSavedCards(): ResponseEntity<List<Card>> {
        reportRequest(
            method = RequestMethod.GET,
            path = "/cards",
            body = null
        )
        val response = cardIntegration.getSavedCards()
        return reportResponse(HttpStatus.OK, response)
    }

}