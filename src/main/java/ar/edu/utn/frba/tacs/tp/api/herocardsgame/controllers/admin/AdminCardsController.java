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
public class AdminCardsController {


  @GetMapping("/cards")
  public void getCards() {

  }

  @PostMapping("/cards")
  @RequestMapping(method = RequestMethod.POST, value = {"/cards/{cardId}"})
  public void createCards(@PathVariable("cardId") String cardId) {
    System.out.println(cardId);
  }

  @PutMapping("/cards")
  @RequestMapping(method = RequestMethod.PUT, value = {"/cards/{cardId}"})
  public void updateCards(@PathVariable("cardId") String cardId) {
    System.out.println(cardId);
  }

  @DeleteMapping("/cards")
  @RequestMapping(method = RequestMethod.DELETE, value = {"/cards/{cardId}"})
  public void deleteCard(@PathVariable("cardId") String cardId) {
    System.out.println(cardId);
  }


}
