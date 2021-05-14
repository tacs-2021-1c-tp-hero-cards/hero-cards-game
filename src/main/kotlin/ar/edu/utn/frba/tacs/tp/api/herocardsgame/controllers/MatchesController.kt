package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateMatchRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.NextDuelRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.MatchService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class MatchesController(private val matchService: MatchService) {

    @PostMapping("/users/matches")
    fun createMatch(@RequestBody createMatchRequest: CreateMatchRequest): ResponseEntity<Match> {
        val match =
            matchService.createMatch(createMatchRequest.userIds, createMatchRequest.deckId)
        return ResponseEntity.status(HttpStatus.CREATED).body(match)
    }

    @PostMapping("/users/matches/{match-id}/nextDuel")
    fun nextDuel(
        @PathVariable("match-id") matchId: String,
        @RequestBody nextDuelRequest: NextDuelRequest
    ): ResponseEntity<Match> {
        val match = matchService.nextDuel(matchId, nextDuelRequest.token, nextDuelRequest.duelType)
        return ResponseEntity.status(HttpStatus.OK).body(match)
    }

    @GetMapping("/users/matches/{match-id}")
    fun getMatch(@PathVariable("match-id") matchId: String): ResponseEntity<Match> =
        ResponseEntity.status(HttpStatus.OK).body(matchService.searchMatchById(matchId))

    @PostMapping("/users/matches/{match-id}/abortMatch")
    fun abortMatch(
        @PathVariable("match-id") matchId: String,
        @RequestBody tokenMap: Map<String, String>
    ): ResponseEntity<Match> =
        ResponseEntity.status(HttpStatus.OK).body(matchService.abortMatch(matchId, tokenMap["token"]!!))

}