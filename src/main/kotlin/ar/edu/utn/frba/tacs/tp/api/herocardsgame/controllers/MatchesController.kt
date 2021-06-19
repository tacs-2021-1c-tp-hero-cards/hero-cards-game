package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidMatchException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidTurnException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateMatchRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.NextDuelRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.MatchService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@CrossOrigin(origins =["http://localhost:3000"], allowedHeaders = ["*"])
class MatchesController(private val matchService: MatchService) {

    @PostMapping("/users/matches")
    fun createMatch(@RequestBody createMatchRequest: CreateMatchRequest): ResponseEntity<Match> =
        try {
            ResponseEntity.status(HttpStatus.CREATED)
                .body(matchService.createMatch(createMatchRequest.userIds, createMatchRequest.deckId))
        } catch (e: ElementNotFoundException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }

    @PostMapping("/users/matches/{match-id}/nextDuel")
    fun nextDuel(
        @PathVariable("match-id") matchId: String,
        @RequestBody nextDuelRequest: NextDuelRequest
    ): ResponseEntity<Match> = try {
        ResponseEntity.status(HttpStatus.OK)
            .body(matchService.nextDuel(matchId, nextDuelRequest.token, nextDuelRequest.duelType))
    } catch (e: Exception) {
        when (e) {
            is InvalidMatchException, is InvalidTurnException -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
            else -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/users/matches/{match-id}")
    fun getMatch(@PathVariable("match-id") matchId: String): ResponseEntity<Match> =
        try {
            ResponseEntity.status(HttpStatus.OK).body(matchService.searchMatchById(matchId))
        } catch (e: ElementNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    @PostMapping("/users/matches/{match-id}/abortMatch")
    fun abortMatch(
        @PathVariable("match-id") matchId: String,
        @RequestBody tokenMap: Map<String, String>
    ): ResponseEntity<Match> = try {
        ResponseEntity.status(HttpStatus.OK).body(matchService.abortMatch(matchId, tokenMap["token"]!!))
    } catch (e: Exception) {
        when (e) {
            is InvalidMatchException, is InvalidTurnException -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
            else -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }
}