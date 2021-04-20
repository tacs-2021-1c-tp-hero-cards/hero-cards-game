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
public class MatchesController {


  @GetMapping("/users/matches/{matchId}")
  public ResponseEntity<Match> getMatch(@PathVariable("matchId") String matchId) {
    Match match = FileConstructorUtils.createFromFile("src/test/resources/json/match/MatchExample1.json", Match.class);
    return ResponseEntity.status(HttpStatus.OK).body(match);
  }

  @DeleteMapping("/users/matches/{matchId}")
  public ResponseEntity deleteMatch(@PathVariable("matchId") String matchId) {
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PostMapping("/users/matches/{matchId}/nextDuel")
  public ResponseEntity continueMatch(@PathVariable("matchId") String matchId) {
    Match match = FileConstructorUtils.createFromFile("src/test/resources/json/match/MatchExample2.json", Match.class);
    return ResponseEntity.status(HttpStatus.OK).body(match);
  }

  @PostMapping("/users/matches")
  public ResponseEntity<Match> createMatch() {
    Match match = FileConstructorUtils.createFromFile("src/test/resources/json/match/MatchExample1.json", Match.class);
    return ResponseEntity.status(HttpStatus.CREATED).body(match);
  }

}
