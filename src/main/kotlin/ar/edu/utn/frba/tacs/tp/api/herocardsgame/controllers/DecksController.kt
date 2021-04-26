package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.AddCardToDeckRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.UpdateNameDeckRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.FileConstructorUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

/**
 * El administrador tiene la capacidad de manejar los mazos (crear, eliminar, modificar)
 * y ponerles un nombre
 */
@Controller
class DecksController {

    @GetMapping("/decks")
    fun getDecks(): ResponseEntity<List<Deck>> {
        val batman = FileConstructorUtils.createFromFile("src/main/resources/json/card/Batman.json", Card::class.java);
        val flash = FileConstructorUtils.createFromFile("src/main/resources/json/card/Flash.json", Card::class.java);

        val batmanDeck = Deck(1L, "Batman deck", listOf(batman));
        val flashDeck = Deck(2L, "Justice League deck", listOf(batman, flash));

        return ResponseEntity.status(HttpStatus.OK).body(listOf(batmanDeck, flashDeck));
    }

    /**
     *  TODO we should validate all the cards added has attributes needed for the game.
     * @param deck
     * @return
     */
    @PostMapping("/admin/decks")
    fun createDeck(@RequestBody deck: Deck): ResponseEntity<Deck> {
        return ResponseEntity.status(HttpStatus.CREATED).body(deck);
    }

    /**
     * TODO use the same validation to create the deck
     * @param deckId
     * @return
     */
    @PutMapping("/admin/decks/{deckId}")
    fun updateDeck(@PathVariable("deckId") deckId: String, @RequestBody deck: Deck): ResponseEntity<Void> {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/admin/decks/{deckId}/name")
    fun updateNameDeck(@PathVariable("deckId") deckId: String,
                       @RequestBody updateNameDeckRequest: UpdateNameDeckRequest): ResponseEntity<Void> {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //TODO: Tengo dudas con respecto a si es put o patch
    //TODO: Tengo dudas con respecto a si conviene agregar la carta por id o por objeto
    @PatchMapping("/admin/decks/{deckId}/card")
    fun addCardToDeck(@PathVariable("deckId") deckId: String,
                      @RequestBody addCardToDeckRequest: AddCardToDeckRequest): ResponseEntity<Void> {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/admin/decks/{deckId}/card/{cardId}")
    fun deleteCardToDeck(@PathVariable("deckId") deckId: String,
                         @PathVariable("cardId") cardId: String): ResponseEntity<Void> {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/admin/decks/{deckId}")
    fun deleteDeck(@PathVariable("deckId") deckId: String): ResponseEntity<Void> {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
