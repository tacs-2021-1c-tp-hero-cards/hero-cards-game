package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String username;
    private List<Card> availableCards;
    private List<Card> prizeCards;

    public Player(String username, List<Card> availableCards) {
        this.username = username;
        this.availableCards = availableCards;
        this.prizeCards = new ArrayList(); // Starts empty
    }

    public String getUsername() {
        return username;
    }

    public List<Card> getAvailableCards() {
        return availableCards;
    }

    public List<Card> getPrizeCards() {
        return prizeCards;
    }
}
