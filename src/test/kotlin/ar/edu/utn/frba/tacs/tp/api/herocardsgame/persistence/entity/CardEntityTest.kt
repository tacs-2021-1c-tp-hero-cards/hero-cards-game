package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Powerstats
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class CardEntityTest {

    private val id = 69L
    private val name = "Batman"
    private val height = 178
    private val weight = 77
    private val intelligence = 81
    private val speed = 29
    private val power = 63
    private val combat = 90
    private val strength = 40
    private val imageUrl = "https://www.superherodb.com/pictures2/portraits/10/100/10441.jpg"

    @Test
    fun toEntity() {
        val model = Card(id, name, Powerstats(height, weight, intelligence, speed, power, combat, strength), imageUrl)

        val entity = CardEntity(model)
        assertEquals(id, entity.id)
        assertEquals(name, entity.name)
        assertEquals(height, entity.height)
        assertEquals(weight, entity.weight)
        assertEquals(intelligence, entity.intelligence)
        assertEquals(speed, entity.speed)
        assertEquals(power, entity.power)
        assertEquals(combat, entity.combat)
        assertEquals(strength, entity.strength)
        assertEquals(imageUrl, entity.imageUrl)
    }

    @Test
    fun toModel() {
        val entity = CardEntity(
            Card(
                id,
                name,
                Powerstats(height, weight, intelligence, speed, power, combat, strength),
                imageUrl
            )
        )

        val model = entity.toModel()
        assertEquals(id, model.id)
        assertEquals(name, model.name)
        assertEquals(height, model.powerstats.height)
        assertEquals(weight, model.powerstats.weight)
        assertEquals(intelligence, model.powerstats.intelligence)
        assertEquals(speed, model.powerstats.speed)
        assertEquals(power, model.powerstats.power)
        assertEquals(combat, model.powerstats.combat)
        assertEquals(strength, model.powerstats.strength)
        assertEquals(imageUrl, model.imageUrl)
    }
}