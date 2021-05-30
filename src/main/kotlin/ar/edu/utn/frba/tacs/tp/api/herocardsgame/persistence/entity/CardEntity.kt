package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Powerstats
import java.time.LocalDateTime

class CardEntity(card: Card){
    val id: Long = card.id
    val name: String = card.name
    val imageUrl: String = card.imageUrl
    val height: Int = card.powerstats.height
    val weight: Int = card.powerstats.weight
    val intelligence: Int = card.powerstats.intelligence
    val speed: Int = card.powerstats.speed
    val power: Int = card.powerstats.power
    val combat: Int = card.powerstats.combat
    val strength: Int = card.powerstats.strength
    val lastUse: LocalDateTime = LocalDateTime.now()

    fun toModel(): Card =
        Card(id, name, Powerstats(height, weight, intelligence, speed, power, combat, strength), imageUrl)
}