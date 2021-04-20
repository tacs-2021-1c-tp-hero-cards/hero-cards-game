package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game;

public class Powerstats {

    private Integer height;
    private Integer weight;
    private Integer intelligence;
    private Integer speed;
    private Integer power;
    private Integer combat;
    private Integer strength;

    public Powerstats(Integer height, Integer weight, Integer intelligence, Integer speed, Integer power, Integer combat,
                      Integer strength) {
        this.height = height;
        this.weight = weight;
        this.intelligence = intelligence;
        this.speed = speed;
        this.power = power;
        this.combat = combat;
        this.strength = strength;
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
