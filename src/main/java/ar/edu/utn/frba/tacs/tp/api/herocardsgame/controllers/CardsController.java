package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CardsController {

  @GetMapping("/cards")
  @RequestMapping(method = RequestMethod.GET, value = {"/users/cards"})
  public void getCards(@RequestParam("search") String search) {
    System.out.println(search);
  }

  @PostMapping("/admin/cards")
  @RequestMapping(method = RequestMethod.POST, value = {"/admin/cards/{cardId}"})
  public void createCard(@PathVariable("cardId") String cardId) {
    System.out.println(cardId);
  }

  @PutMapping("/admin/cards")
  @RequestMapping(method = RequestMethod.PUT, value = {"/admin/cards/{cardId}"})
  public void updateCard(@PathVariable("cardId") String cardId) {
    System.out.println(cardId);
  }

  @DeleteMapping("/admin/cards")
  @RequestMapping(method = RequestMethod.DELETE, value = {"/admin/cards/{cardId}"})
  public void deleteCard(@PathVariable("cardId") String cardId) {
    System.out.println(cardId);
  }

  /**
   * Should present the card with the Image and attributes
   * @param cardId
   */

  @GetMapping("/users/cards")
  @RequestMapping(method = RequestMethod.HEAD, value = {"/users/cards/{cardId}"})
  public void presentCard(@PathVariable("cardId") String cardId) {
    System.out.println(cardId);
  }


}
