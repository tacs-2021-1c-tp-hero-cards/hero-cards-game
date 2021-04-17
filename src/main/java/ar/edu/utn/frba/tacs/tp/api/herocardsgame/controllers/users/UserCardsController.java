package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers.users;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/users")
public class UserCardsController {


  @GetMapping("/cards")
  @RequestMapping(method = RequestMethod.GET, value = {"/cards"})
  public void getCards(@RequestParam("search") String search) {
    System.out.println(search);
  }

  @PostMapping("/cards")
  @RequestMapping(method = RequestMethod.POST, value = {"/cards/{cardId}"})
  public void createCard(@PathVariable("cardId") String cardId) {
    System.out.println(cardId);
  }

}
