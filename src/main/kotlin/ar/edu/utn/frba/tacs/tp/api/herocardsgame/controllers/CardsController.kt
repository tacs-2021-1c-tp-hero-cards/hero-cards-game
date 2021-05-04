package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.SuperHeroIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
class CardsController(val superHeroIntegration: SuperHeroIntegration) {

    @GetMapping("/cards/{card-id}")
    fun getCard(@PathVariable("card-id") cardId: String): ResponseEntity<Card> =
        try {
            val card = superHeroIntegration.getCard(cardId)
            ResponseEntity.status(HttpStatus.OK).body(card)
        } catch (e: ElementNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    @GetMapping("/cards/search/{card-name}")
    fun getCardByName(@PathVariable("card-name") cardName: String): ResponseEntity<List<Card>> =
        try {
            val card = superHeroIntegration.searchCardByName(cardName)
            ResponseEntity.status(HttpStatus.OK).body(card)
        } catch (e: ElementNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
}