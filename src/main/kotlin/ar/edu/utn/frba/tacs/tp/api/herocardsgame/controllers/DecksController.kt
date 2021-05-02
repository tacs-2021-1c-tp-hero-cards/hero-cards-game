package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.AddCardToDeckRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateDeckRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.DeckService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
/**
 * El administrador tiene la capacidad de manejar los mazos (crear, eliminar, modificar)
 * y ponerles un nombre
 */
@Controller
class DecksController(
    private val deckService: DeckService
) {

    @GetMapping("/decks")
    fun getDecks(): ResponseEntity<List<Deck>> =
        ResponseEntity.status(HttpStatus.OK).body(deckService.getAllDeck())

    @GetMapping("/decks/search")
    fun getDeckByIdAndName(
        @RequestParam(value = "deck-id") deckId: String,
        @RequestParam(value = "deck-name") deckName: String
    ): ResponseEntity<List<Deck>> =
        ResponseEntity.status(HttpStatus.OK).body(deckService.searchDeck(deckId, deckName))

    /**
     *  TODO we should validate all the cards added has attributes needed for the game.
     * @param deck
     * @return
     */
    @PostMapping("/admin/decks")
    fun createDeck(@RequestBody createDeckRequest: CreateDeckRequest): ResponseEntity<Deck> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(deckService.addDeck(createDeckRequest.cardName, createDeckRequest.cardIds));

    /**
     * TODO use the same validation to create the deck
     * @param deckId
     * @return
     */
    @PutMapping("/admin/decks/{deck-id}")
    fun updateDeck(@PathVariable("deckId") deckId: String, @RequestBody deck: Deck): ResponseEntity<Void> {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/admin/decks/{deck-id}/card")
    fun addCardToDeck(
        @PathVariable("deck-id") deckId: String,
        @RequestBody addCardToDeckRequest: AddCardToDeckRequest
    ): ResponseEntity<Void> {
        deckService.addCardInDeck(deckId, addCardToDeckRequest.cardId)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @DeleteMapping("/admin/decks/{deck-id}/card/{card-id}")
    fun deleteCardToDeck(
        @PathVariable("deck-id") deckId: String,
        @PathVariable("card-id") cardId: String
    ): ResponseEntity<Void> {
        deckService.deleteCardInDeck(deckId, cardId)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @DeleteMapping("/admin/decks/{deck-id}")
    fun deleteDeck(@PathVariable("deck-id") deckId: String): ResponseEntity<Void> {
        deckService.deleteDeck(deckId)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}