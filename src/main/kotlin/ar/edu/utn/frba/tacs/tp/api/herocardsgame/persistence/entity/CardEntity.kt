package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Powerstats
import org.springframework.data.redis.core.RedisHash
import java.io.Serializable
import javax.persistence.Id

@RedisHash("Card", timeToLive = 3600)
data class CardEntity(
    @Id
    val id: String,
    val name: String,
    val imageUrl: String,
    val height: Int,
    val weight: Int,
    val intelligence: Int,
    val speed: Int,
    val power: Int,
    val combat: Int,
    val strength: Int,
) : Serializable {
    constructor(card: Card) : this(
        id = card.id.toString(),
        name = card.name,
        imageUrl = card.imageUrl,
        height = card.powerstats.height,
        weight = card.powerstats.weight,
        intelligence = card.powerstats.intelligence,
        speed = card.powerstats.speed,
        power = card.powerstats.power,
        combat = card.powerstats.combat,
        strength = card.powerstats.strength
    )

    fun toModel(): Card =
        Card(id.toLong(), name, Powerstats(height, weight, intelligence, speed, power, combat, strength), imageUrl)
}