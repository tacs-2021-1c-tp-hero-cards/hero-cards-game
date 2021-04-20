package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game;

public class Card {

  private Long id;
  private String name;
  private Powerstats powerstats;

  public Card(Long id, String name, Powerstats powerstats) {
    this.id = id;
    this.name = name;
    this.powerstats = powerstats;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Powerstats getPowerstats() {
    return powerstats;
  }
}
