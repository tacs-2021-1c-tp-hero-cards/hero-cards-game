package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidPowerstatsException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateDeckRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.UpdateDeckRequest
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
@CrossOrigin(origins = ["http://localhost:5000"], allowedHeaders = ["*"])
class DecksController(private val deckService: DeckService) :
    AbstractController<DecksController>(DecksController::class.java) {

    /**
     * @return list of deck
     */
    @GetMapping("/decks")
    fun getDecks(): ResponseEntity<List<Deck>> {
        reportRequest(
            method = RequestMethod.GET,
            path = "/decks",
            body = null
        )
        val response = deckService.searchDeck()
        return reportResponse(HttpStatus.OK, response)
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
        reportRequest(
            method = RequestMethod.GET,
            path = "/decks/search",
            body = null,
            requestParams = hashMapOf("deck-id" to deckId, "deck-name" to deckName)
        )
        val response = deckService.searchDeck(deckId, deckName)
        return reportResponse(HttpStatus.OK, response)
    }

    /**
     * @param createDeckRequest
     * @return deck
     */
    @PostMapping("/admin/decks")
    fun createDeck(@RequestBody createDeckRequest: CreateDeckRequest): ResponseEntity<Deck> =
        try {
            reportRequest(
                method = RequestMethod.POST,
                path = "/admin/decks",
                body = createDeckRequest
            )
            val response = deckService.createDeck(createDeckRequest.deckName, createDeckRequest.cardIds)
            reportResponse(HttpStatus.CREATED, response)
        } catch (e: ElementNotFoundException) {
            reportError(e, HttpStatus.BAD_REQUEST)
        }catch (e: InvalidPowerstatsException) {
            reportError(e, HttpStatus.BAD_REQUEST)
        }

    /**
     * @param deckId, updateDeckRequest
     * @return
     */
    @PutMapping("/admin/decks/{deck-id}")
    fun updateDeck(
        @PathVariable("deck-id") deckId: String, @RequestBody updateDeckRequest: UpdateDeckRequest
    ): ResponseEntity<Deck> =
        try {
            reportRequest(
                method = RequestMethod.PUT,
                path = "/admin/decks/{deck-id}",
                pathVariables = hashMapOf("deck-id" to deckId),
                body = updateDeckRequest
            )
            val response = deckService.updateDeck(
                deckId,
                updateDeckRequest.deckName,
                updateDeckRequest.deckCards ?: emptyList()
            )
            reportResponse(HttpStatus.OK, response)
        } catch (e: ElementNotFoundException) {
            reportError(e, HttpStatus.BAD_REQUEST)
        }catch (e: InvalidPowerstatsException) {
            reportError(e, HttpStatus.BAD_REQUEST)
        }

    /**
     * @param deckId
     * @return
     */
    @DeleteMapping("/admin/decks/{deck-id}")
    fun deleteDeck(@PathVariable("deck-id") deckId: String): ResponseEntity<Void> =
        try {
            reportRequest(
                method = RequestMethod.DELETE,
                path = "/admin/decks/{deck-id}",
                pathVariables = hashMapOf("deck-id" to deckId),
                body = null
            )
            deckService.deleteDeck(deckId)
            reportResponse(HttpStatus.NO_CONTENT)
        } catch (e: ElementNotFoundException) {
            reportError(e, HttpStatus.BAD_REQUEST)
        }

}