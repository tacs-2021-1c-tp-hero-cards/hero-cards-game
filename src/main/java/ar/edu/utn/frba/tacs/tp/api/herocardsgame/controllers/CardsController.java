package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card;
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.FileConstructorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
public class CardsController {
    
  @GetMapping("/users/cards")
  public ResponseEntity<List<Card>> getCards() {
    Card batman = FileConstructorUtils.createFromFile("src/test/resources/json/card/Batman.json", Card.class);
    Card flash = FileConstructorUtils.createFromFile("src/test/resources/json/card/Flash.json", Card.class);
    return ResponseEntity.status(HttpStatus.OK).body(Arrays.asList(batman, flash));
  }

  @PostMapping("/admin/cards")
  public ResponseEntity<Card> createCard(@RequestBody Card card) {
    return ResponseEntity.status(HttpStatus.CREATED).body(card);
  }

  @PutMapping("/admin/cards/{cardId}")
  public ResponseEntity updateCard(@PathVariable("cardId") String cardId, @RequestBody Card card) {
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @DeleteMapping("/admin/cards/{cardId}")
  public ResponseEntity deleteCard(@PathVariable("cardId") String cardId) {
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
