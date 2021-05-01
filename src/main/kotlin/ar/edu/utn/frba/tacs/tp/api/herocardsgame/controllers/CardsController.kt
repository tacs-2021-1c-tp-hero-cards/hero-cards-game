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

    @GetMapping("/card/{card-id}")
    fun getCard(@PathVariable("card-id") cardId: String): ResponseEntity<Card> {
        return try {
            val card = superHeroIntegration.getCard(cardId)
            ResponseEntity.status(HttpStatus.OK).body(card)
        } catch (e: ElementNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/card/search/{name-card}")
    fun getCardByName(@PathVariable("name-card") nameCard: String): ResponseEntity<List<Card>> {
        return try {
            val card = superHeroIntegration.searchCardByName(nameCard)
            ResponseEntity.status(HttpStatus.OK).body(card)
        } catch (e: ElementNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    //Hace falta definir estos servicios???

    @PostMapping("/admin/cards")
    fun createCard(@RequestBody card: Card): ResponseEntity<Card> {
        return ResponseEntity.status(HttpStatus.CREATED).body(card);
    }

    @PutMapping("/admin/cards/{card-id}")
    fun updateCard(@PathVariable("card-id") cardId: String, @RequestBody card: Card): ResponseEntity<Void> {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/admin/cards/{card-id}")
    fun deleteCard(@PathVariable("card-id") cardId: String): ResponseEntity<Void> {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}