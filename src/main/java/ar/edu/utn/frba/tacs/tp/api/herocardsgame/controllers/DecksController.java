package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card;
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck;
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.AddCardToDeckRequest;
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.UpdateNameDeckRequest;
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.FileConstructorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * El administrador tiene la capacidad de manejar los mazos (crear, eliminar, modificar)
 * y ponerles un nombre
 */
@Controller
public class DecksController {

  @GetMapping("/users/decks")
  public ResponseEntity<List<Deck>> getDecks() {
    Card batman = FileConstructorUtils.createFromFile("src/main/resources/json/card/Batman.json", Card.class);
    Card flash = FileConstructorUtils.createFromFile("src/main/resources/json/card/Flash.json", Card.class);

    Deck batmanDeck = new Deck(1L, "Batman deck", Collections.singletonList(batman));
    Deck flashDeck = new Deck(2L, "Justice League deck", Arrays.asList(batman, flash));

    return ResponseEntity.status(HttpStatus.OK).body(Arrays.asList(batmanDeck, flashDeck));
  }

  /**
   *  TODO we should validate all the cards added has attributes needed for the game.
   * @param deck
   * @return
   */
  @PostMapping("/admin/decks")
  public ResponseEntity<Deck> createDeck(@RequestBody Deck deck) {
    return ResponseEntity.status(HttpStatus.CREATED).body(deck);
  }

  /**
   * TODO use the same validation to create the deck
   * @param deckId
   * @return
   */
  @PutMapping("/admin/decks/{deckId}")
  public ResponseEntity updateDeck(@PathVariable("deckId") String deckId, @RequestBody Deck deck) {
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PatchMapping("/admin/decks/{deckId}/name")
  public ResponseEntity updateNameDeck(@PathVariable("deckId") String deckId,
                                       @RequestBody UpdateNameDeckRequest updateNameDeckRequest){
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  //TODO: Tengo dudas con respecto a si es put o patch
  //TODO: Tengo dudas con respecto a si conviene agregar la carta por id o por objeto
  @PatchMapping("/admin/decks/{deckId}/card")
  public ResponseEntity addCardToDeck(@PathVariable("deckId") String deckId,
                                      @RequestBody AddCardToDeckRequest addCardToDeckRequest){
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @DeleteMapping("/admin/decks/{deckId}/card/{cardId}")
  public ResponseEntity deleteCardToDeck(@PathVariable("deckId") String deckId, @PathVariable("cardId") String cardId){
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @DeleteMapping("/admin/decks/{deckId}")
  public ResponseEntity deleteDeck(@PathVariable("deckId") String deckId) {
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

}
