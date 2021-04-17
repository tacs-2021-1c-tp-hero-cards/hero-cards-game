package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin")
public class AdminStatsController {

  @GetMapping("/scoreboards")
  public void getScoreBoards() { }

  @GetMapping("/stats")
  public void getStats() {
    System.out.println("GET STATS");
  }

}
