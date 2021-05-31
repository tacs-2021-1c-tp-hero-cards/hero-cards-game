package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DeckEntityTest {

    private val id: Long = 0L
    private val name: String = "name"
    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()
    private val cards: List<Card> = listOf(batman, flash)
    private val cardsId: List<Long> = listOf(69L, 2L)

    @Test
    fun toEntityWithId() {
        val model = Deck(id, name, cards, false)

        val entity = DeckEntity(deck = model)
        assertEquals(id, entity.id)
        assertEquals(name, entity.name)
        assertEquals(cardsId, entity.cardIds)
        assertFalse(entity.usable)
    }

    @Test
    fun toEntityWithOutId() {
        val model = Deck(null, name, cards, true)

        val entity = DeckEntity(1L, model)
        assertEquals(1L, entity.id)
        assertEquals(name, entity.name)
        assertEquals(cardsId, entity.cardIds)
        assertTrue(entity.usable)
    }

    @Test
    fun toModel() {
        val entity =
            DeckEntity(
                id,
                Deck(null, name, cards, true)
            )

        val model = entity.toModel(listOf(batman, flash))
        assertEquals(id, model.id)
        assertEquals(name, model.name)
        assertTrue(model.cards.contains(batman))
        assertTrue(model.cards.contains(flash))
        assertTrue(model.usable)
    }
}