package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Match;
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.FileConstructorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
class MatchesController {

    @GetMapping("/users/matches/{matchId}")
    fun getMatch(@PathVariable("matchId") matchId: String): ResponseEntity<Match> {
        val match = FileConstructorUtils.createFromFile("src/main/resources/json/match/MatchExample1.json", Match::class.java);
        return ResponseEntity.status(HttpStatus.OK).body(match);
    }

    @DeleteMapping("/users/matches/{matchId}")
    fun deleteMatch(@PathVariable("matchId") matchId: String): ResponseEntity<Void> {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/users/matches/{matchId}/nextDuel")
    fun continueMatch(@PathVariable("matchId") matchId: String): ResponseEntity<Match> {
        val match = FileConstructorUtils.createFromFile("src/main/resources/json/match/MatchExample2.json", Match::class.java);
        return ResponseEntity.status(HttpStatus.OK).body(match);
    }

    @PostMapping("/users/matches")
    fun createMatch(): ResponseEntity<Match> {
        val match = FileConstructorUtils.createFromFile("src/main/resources/json/match/MatchExample1.json", Match::class.java);
        return ResponseEntity.status(HttpStatus.CREATED).body(match);
    }

}
