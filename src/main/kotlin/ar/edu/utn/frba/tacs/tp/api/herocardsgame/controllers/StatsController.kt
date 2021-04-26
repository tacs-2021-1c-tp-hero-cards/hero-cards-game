package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin")
class StatsController {

  @GetMapping("/scoreboards")
  fun getScoreBoards() {
    println("GET SCORE BOARDS");
  }

  @GetMapping("/stats")
  fun getStats() {
    println("GET STATS");
  }

}
