package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers.users;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserDecksController {

  @GetMapping("/decks")
  public void getDecks() {

  }

}
