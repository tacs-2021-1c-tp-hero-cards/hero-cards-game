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
public class AdminDecksController {


  @GetMapping("/decks")
  public void getDecks() {

  }

  @PostMapping("/decks")
  @RequestMapping(method = RequestMethod.POST, value = {"/decks/{deckId}"})
  public void createCards(@PathVariable("deckId") String deckId) {
    System.out.println(deckId);
  }

  @PutMapping("/decks")
  @RequestMapping(method = RequestMethod.PUT, value = {"/decks/{deckId}"})
  public void updateCards(@PathVariable("deckId") String deckId) {
    System.out.println(deckId);
  }

  @DeleteMapping("/decks")
  @RequestMapping(method = RequestMethod.DELETE, value = {"/decks/{deckId}"})
  public void deleteCard(@PathVariable("deckId") String deckId) {
    System.out.println(deckId);
  }


}
