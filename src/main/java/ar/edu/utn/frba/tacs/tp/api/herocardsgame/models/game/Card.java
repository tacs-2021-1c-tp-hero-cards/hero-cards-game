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

  public Card(Long id, String name, Integer height, Integer weight, Integer intelligence, Integer speed, Integer power,
              Integer combat, Integer strength) {
    this.id = id;
    this.name = name;
    this.height = height;
    this.weight = weight;
    this.intelligence = intelligence;
    this.speed = speed;
    this.power = power;
    this.combat = combat;
    this.strength = strength;
  }

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
