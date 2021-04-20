package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game;

import java.util.List;

public class Deck {

    private Long id;
    private String name;
    private List<Card> cards;

    public Deck(Long id, String name, List<Card> cards) {
        this.id = id;
        this.name = name;
        this.cards = cards;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Card> getCards() {
        return cards;
    }
}

