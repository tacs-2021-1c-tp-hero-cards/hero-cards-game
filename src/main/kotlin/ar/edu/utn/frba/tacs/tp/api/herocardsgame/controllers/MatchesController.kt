package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidMatchException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidTurnException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateMatchRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.NextDuelRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.MatchService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@CrossOrigin(origins = ["http://localhost:3000"], allowedHeaders = ["*"])
class MatchesController(private val matchService: MatchService) :
    AbstractController<MatchesController>(MatchesController::class.java) {

    @PostMapping("/users/matches")
    fun createMatch(@RequestBody createMatchRequest: CreateMatchRequest): ResponseEntity<Match> =
        try {
            reportRequest(
                method = RequestMethod.POST,
                path = "/users/matches",
                body = createMatchRequest
            )
            val response = matchService.createMatch(createMatchRequest.humanUserIds, createMatchRequest.iaUserIds, createMatchRequest.deckId)
            reportResponse(HttpStatus.CREATED, response)
        } catch (e: ElementNotFoundException) {
            reportError(e, HttpStatus.BAD_REQUEST)
        }

    @PostMapping("/users/matches/{match-id}/nextDuel")
    fun nextDuel(
        @PathVariable("match-id") matchId: String,
        @RequestBody nextDuelRequest: NextDuelRequest
    ): ResponseEntity<Match> = try {
        reportRequest(
            method = RequestMethod.POST,
            path = "/users/matches/{match-id}/nextDuel",
            pathVariables = hashMapOf("match-id" to matchId),
            body = nextDuelRequest
        )
        val response = matchService.nextDuel(matchId, nextDuelRequest.token, nextDuelRequest.duelType)
        reportResponse(HttpStatus.OK, response)
    } catch (e: InvalidMatchException) {
        reportError(e, HttpStatus.BAD_REQUEST)
    } catch (e: InvalidTurnException) {
        reportError(e, HttpStatus.BAD_REQUEST)
    } catch (e: ElementNotFoundException) {
        reportError(e, HttpStatus.NOT_FOUND)
    }

    @GetMapping("/users/matches/{match-id}")
    fun getMatch(@PathVariable("match-id") matchId: String): ResponseEntity<Match> =
        try {
            reportRequest(
                method = RequestMethod.GET,
                path = "/users/matches/{match-id}",
                pathVariables = hashMapOf("match-id" to matchId),
                body = null
            )
            val response = matchService.searchMatchById(matchId)
            reportResponse(HttpStatus.OK, response)
        } catch (e: ElementNotFoundException) {
            reportError(e, HttpStatus.NOT_FOUND)
        }

    @PostMapping("/users/matches/{match-id}/abortMatch")
    fun abortMatch(
        @PathVariable("match-id") matchId: String,
        @RequestBody tokenMap: Map<String, String>
    ): ResponseEntity<Match> = try {
        reportRequest(
            method = RequestMethod.POST,
            path = "/users/matches/{match-id}/abortMatch",
            pathVariables = hashMapOf("match-id" to matchId),
            body = tokenMap
        )
        val response = matchService.abortMatch(matchId, tokenMap["token"]!!)
        reportResponse(HttpStatus.OK, response)
    } catch (e: InvalidMatchException) {
        reportError(e, HttpStatus.BAD_REQUEST)
    } catch (e: InvalidTurnException) {
        reportError(e, HttpStatus.BAD_REQUEST)
    } catch (e: ElementNotFoundException) {
        reportError(e, HttpStatus.NOT_FOUND)
    }
}