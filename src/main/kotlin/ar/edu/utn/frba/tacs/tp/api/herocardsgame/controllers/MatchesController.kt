package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidMatchException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidTurnException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateMatchRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.MatchConfirmationRequest
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

    @GetMapping("/matches/{match-id}")
    fun getMatch(@PathVariable("match-id") matchId: String): ResponseEntity<Match> =
        try {
            reportRequest(
                method = RequestMethod.GET,
                path = "/matches/{match-id}",
                pathVariables = hashMapOf("match-id" to matchId),
                body = null
            )
            val response = matchService.searchMatchById(matchId)
            reportResponse(HttpStatus.OK, response)
        } catch (e: ElementNotFoundException) {
            reportError(e, HttpStatus.NOT_FOUND)
        }

    @PostMapping("/matches")
    fun createMatch(
        @RequestBody createMatchRequest: CreateMatchRequest,
        @RequestHeader(value = "x-user-token") token: String
    ): ResponseEntity<Match> =
        try {
            reportRequest(
                method = RequestMethod.POST,
                path = "/matches",
                requestHeader = hashMapOf("x-user-token" to token),
                body = createMatchRequest
            )
            val response = matchService.createMatch(
                token,
                createMatchRequest.userId,
                UserType.getUserType(createMatchRequest.userType),
                createMatchRequest.deckId
            )
            reportResponse(HttpStatus.CREATED, response)
        } catch (e: ElementNotFoundException) {
            reportError(e, HttpStatus.BAD_REQUEST)
        }

    @PatchMapping("/matches/{match-id}/confirmation")
    fun confirmMatch(
        @PathVariable("match-id") matchId: String,
        @RequestBody matchConfirmationRequest: MatchConfirmationRequest,
        @RequestHeader(value = "x-user-token") token: String
    ): ResponseEntity<Match> =
        try {
            reportRequest(
                method = RequestMethod.PATCH,
                path = "/matches/{match-id}/confirmation",
                pathVariables = hashMapOf("match-id" to matchId),
                requestHeader = hashMapOf("x-user-token" to token),
                body = null
            )
            val response = matchService.matchConfirmation(matchId, matchConfirmationRequest.confirm, token)
            reportResponse(HttpStatus.OK, response)
        } catch (e: ElementNotFoundException) {
            reportError(e, HttpStatus.BAD_REQUEST)
        } catch (e: InvalidMatchException) {
            reportError(e, HttpStatus.BAD_REQUEST)
        }

    @PatchMapping("/matches/{match-id}/nextDuel")
    fun nextDuel(
        @PathVariable("match-id") matchId: String,
        @RequestBody nextDuelRequest: NextDuelRequest,
        @RequestHeader(value = "x-user-token") token: String
    ): ResponseEntity<Match> = try {
        reportRequest(
            method = RequestMethod.PATCH,
            path = "/matches/{match-id}/nextDuel",
            pathVariables = hashMapOf("match-id" to matchId),
            requestHeader = hashMapOf("x-user-token" to token),
            body = nextDuelRequest
        )
        val response = matchService.nextDuel(matchId, token, nextDuelRequest.duelType)
        reportResponse(HttpStatus.OK, response)
    } catch (e: InvalidMatchException) {
        reportError(e, HttpStatus.BAD_REQUEST)
    } catch (e: InvalidTurnException) {
        reportError(e, HttpStatus.BAD_REQUEST)
    } catch (e: ElementNotFoundException) {
        reportError(e, HttpStatus.NOT_FOUND)
    }

    @PatchMapping("/matches/{match-id}/abortMatch")
    fun abortMatch(
        @PathVariable("match-id") matchId: String,
        @RequestHeader(value = "x-user-token") token: String
    ): ResponseEntity<Match> = try {
        reportRequest(
            method = RequestMethod.POST,
            path = "/users/matches/{match-id}/abortMatch",
            pathVariables = hashMapOf("match-id" to matchId),
            requestHeader = hashMapOf("x-user-token" to token),
            body = null
        )
        val response = matchService.abortMatch(matchId, token)
        reportResponse(HttpStatus.OK, response)
    } catch (e: InvalidMatchException) {
        reportError(e, HttpStatus.BAD_REQUEST)
    } catch (e: InvalidTurnException) {
        reportError(e, HttpStatus.BAD_REQUEST)
    } catch (e: ElementNotFoundException) {
        reportError(e, HttpStatus.NOT_FOUND)
    }
}