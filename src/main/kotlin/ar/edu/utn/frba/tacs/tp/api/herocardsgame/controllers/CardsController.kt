package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card;
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.FileConstructorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
class CardsController {

    @GetMapping("/cards")
    fun getCards(): ResponseEntity<List<Card>> {
        val batman = FileConstructorUtils.createFromFile("src/main/resources/json/card/Batman.json", Card::class.java);
        val flash = FileConstructorUtils.createFromFile("src/main/resources/json/card/Flash.json", Card::class.java);

        return ResponseEntity.status(HttpStatus.OK).body(listOf(batman, flash));
    }

    @PostMapping("/admin/cards")
    fun createCard(@RequestBody card: Card): ResponseEntity<Card> {
        return ResponseEntity.status(HttpStatus.CREATED).body(card);
    }


    @PutMapping("/admin/cards/{cardId}")
    fun updateCard(@PathVariable("cardId") cardId: String, @RequestBody card: Card): ResponseEntity<Void> {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/admin/cards/{cardId}")
    fun deleteCard(@PathVariable("cardId") cardId: String): ResponseEntity<Void> {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
