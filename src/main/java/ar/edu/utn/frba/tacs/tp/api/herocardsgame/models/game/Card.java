package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game;

public class Card {

  private Long id;
  private String name;
  private Integer height;
  private Integer weight;
  private Integer intelligence;
  private Integer speed;
  private Integer power;
  private Integer combat;
  private Integer strength;

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Integer getHeight() {
    return height;
  }

  public Integer getWeight() {
    return weight;
  }

  public Integer getIntelligence() {
    return intelligence;
  }

  public Integer getSpeed() {
    return speed;
  }

  public Integer getPower() {
    return power;
  }

  public Integer getCombat() {
    return combat;
  }

  public Integer getStrength() {
    return strength;
  }
}
