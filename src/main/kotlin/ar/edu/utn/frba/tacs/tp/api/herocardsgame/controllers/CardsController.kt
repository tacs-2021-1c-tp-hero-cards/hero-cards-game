package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.CardIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class CardsController(val cardIntegration: CardIntegration) {

    /**
     * @param cardId
     * @return card
     */
    @GetMapping("/cards/{card-id}")
    fun getCard(@PathVariable("card-id") cardId: String): ResponseEntity<Card> =
        try {
            ResponseEntity.status(HttpStatus.OK).body(cardIntegration.getCardById(cardId))
        } catch (e: ElementNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    /**
     * @param cardName
     * @return list of card
     */
    @GetMapping("/cards/search/{card-name}")
    fun getCardByName(@PathVariable("card-name") cardName: String): ResponseEntity<List<Card>> =
        try {
            ResponseEntity.status(HttpStatus.OK).body(cardIntegration.searchCardByName(cardName))
        } catch (e: ElementNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    /**
     * @return list of saved cards
     */
    @GetMapping("/cards")
    fun getSavedCards(): ResponseEntity<List<Card>> =
        ResponseEntity.status(HttpStatus.OK).body(cardIntegration.getSavedCards())

}