package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidPowerstatsException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateDeckRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.UpdateDeckRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.DeckService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

/**
 * El administrador tiene la capacidad de manejar los mazos (crear, eliminar, modificar)
 * y ponerles un nombre
 */
@Controller
@CrossOrigin(origins = ["http://localhost:3000"], allowedHeaders = ["*"])
class DecksController(
    private val deckService: DeckService
) {

    private val log: Logger = LoggerFactory.getLogger(DecksController::class.java)

    /**
     * @return list of deck
     */
    @GetMapping("/decks")
    fun getDecks(): ResponseEntity<List<Deck>> {
        log.info("Get /decks")
        return ResponseEntity.status(HttpStatus.OK).body(deckService.searchDeck())
    }


    /**
     * @param deckId, deckName
     * @return list of deck
     */
    @GetMapping("/decks/search")
    fun getDeckByIdOrName(
        @RequestParam(value = "deck-id") deckId: String?,
        @RequestParam(value = "deck-name") deckName: String?
    ): ResponseEntity<List<Deck>> {
        log.info("Get /decks/search requestParam: [deck-id=$deckId | deck-name=$deckName]")
        return ResponseEntity.status(HttpStatus.OK).body(deckService.searchDeck(deckId, deckName))
    }


    /**
     *  TODO we should validate all the cards added has attributes needed for the game.
     * @param createDeckRequest
     * @return deck
     */
    @PostMapping("/admin/decks")
    fun createDeck(@RequestBody createDeckRequest: CreateDeckRequest): ResponseEntity<Deck> =
        try {
            log.info("Post /admin/decks requestBody: [$createDeckRequest]")

            ResponseEntity
                .status(HttpStatus.CREATED)
                .body(deckService.createDeck(createDeckRequest.deckName, createDeckRequest.cardIds))
        } catch (e: ElementNotFoundException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }catch (e: InvalidPowerstatsException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }

    /**
     * TODO use the same validation to create the deck
     * @param deckId, updateDeckRequest
     * @return
     */
    @PutMapping("/admin/decks/{deck-id}")
    fun updateDeck(
        @PathVariable("deck-id") deckId: String, @RequestBody updateDeckRequest: UpdateDeckRequest
    ): ResponseEntity<Deck> =
        try {
            log.info("Put /admin/decks/$deckId requestBody: [$updateDeckRequest]")

            ResponseEntity.status(HttpStatus.OK).body(
                deckService.updateDeck(
                    deckId,
                    updateDeckRequest.deckName,
                    updateDeckRequest.deckCards ?: emptyList()
                )
            )
        } catch (e: ElementNotFoundException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }catch (e: InvalidPowerstatsException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }

    /**
     * @param deckId
     * @return
     */
    @DeleteMapping("/admin/decks/{deck-id}")
    fun deleteDeck(@PathVariable("deck-id") deckId: String): ResponseEntity<Void> =
        try {
            log.info("Delete /admin/decks/$deckId")

            deckService.deleteDeck(deckId)
            ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        } catch (e: ElementNotFoundException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }

}