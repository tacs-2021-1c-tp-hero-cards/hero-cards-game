package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * El administrador tiene la capacidad de manejar los mazos (crear, eliminar, modificar)
 * y ponerles un nombre
 */
@Controller
public class DecksController {


  @GetMapping("/decks")
  public void getDecks() { }

  /**
   *  TODO we should validate all the cards added has attributes needed for the game.
   * @param deckId
   */
  @PostMapping("/admin/decks")
  @RequestMapping(method = RequestMethod.POST, value = {"/admin/decks/{deckId}"})
  public void createDeck(@PathVariable("deckId") String deckId) {
    System.out.println(deckId);
  }

  /**
   * TODO use the same validation to create the deck
   * @param deckId
   */
  @PutMapping("/admin/decks")
  @RequestMapping(method = RequestMethod.PUT, value = {"/admin/decks/{deckId}"})
  public void updateDeck(@PathVariable("deckId") String deckId) {
    System.out.println(deckId);
  }

  @DeleteMapping("/admin/decks")
  @RequestMapping(method = RequestMethod.DELETE, value = {"/admin/decks/{deckId}"})
  public void deleteDeck(@PathVariable("deckId") String deckId) {
    System.out.println(deckId);
  }


}
