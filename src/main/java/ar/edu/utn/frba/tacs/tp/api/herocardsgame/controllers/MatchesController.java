package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MatchesController {


  @GetMapping("/users/matches")
  @RequestMapping(method = RequestMethod.GET, value = {"/users/matches/{matchId}"})
  public void getMatch(@PathVariable("matchId") String matchId) {
    System.out.println(matchId);
  }

  @DeleteMapping("/users/matches")
  @RequestMapping(method = RequestMethod.DELETE, value = {"/users/matches/{matchId}"})
  public void deleteMatch(@PathVariable("matchId") String matchId) {
    System.out.println(matchId);
  }

  @PostMapping("/users/matches")
  @RequestMapping(method = RequestMethod.POST, value = {"/users/matches/{matchId}"})
  public void continueMatch(@PathVariable("matchId") String matchId) {
    System.out.println(matchId);
  }

  @PostMapping("/users/matches")
  public void createMatch() {
    System.out.println("create a new match");
  }

}
