package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User;

import java.util.List;

public class Match {

    private Long id;
    private List<Player> players;
    private Deck deck;

    public Match(Long id, List<Player> players, Deck deck) {
        this.id = id;
        this.players = players;
        this.deck = deck;
    }

    public Long getId() {
        return id;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Deck getDeck() {
        return deck;
    }
}
