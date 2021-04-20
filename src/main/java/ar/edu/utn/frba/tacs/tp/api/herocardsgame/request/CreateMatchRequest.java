package ar.edu.utn.frba.tacs.tp.api.herocardsgame.request;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User;
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck;

import java.util.List;

public class CreateMatchRequest {

    private List<String> usernames;
    private Long deckId;

    public List<String> getUsernames() {
        return usernames;
    }

    public Long getDeckId() {
        return deckId;
    }
}
