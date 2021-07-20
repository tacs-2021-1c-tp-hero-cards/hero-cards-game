package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DeckEntityTest {

    private val id: Long = 0L
    private val version: Long = 0L
    private val name: String = "deckName"
    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()
    private val cards: List<Card> = listOf(batman, flash)
    private val cardsId: String = "69,2"
    private val deckHistory = DeckHistory(id, version, name, cards)

    @Nested
    inner class ToEntity {

        @Test
        fun `Build entity with id`() {
            val model = Deck(id, name, cards)

            val entity = DeckEntity(deck = model)
            assertEquals(id, entity.id)
            assertEquals(name, entity.name)
            assertEquals(cardsId, entity.cardIds)
            assertTrue(entity.deckHistory.isEmpty())
        }

        @Test
        fun `Build entity without id`() {
            val model = Deck(null, name, cards)

            val entity = DeckEntity(deck = model)
            assertNull(entity.id)
            assertEquals(name, entity.name)
            assertEquals(cardsId, entity.cardIds)
            assertTrue(entity.deckHistory.isEmpty())
        }

        @Test
        fun `Build entity with deck historty`() {
            val model = Deck(id,  "newName", cards, listOf(deckHistory))

            val entity = DeckEntity(deck = model)
            assertEquals(id, entity.id)
            assertEquals("newName", entity.name)
            assertEquals(cardsId, entity.cardIds)

            val deckHistoryIds = entity.deckHistory
            assertTrue(deckHistoryIds.contains(DeckHistoryEntity(deckHistory)))
        }

    }

    @Test
    fun toModel() {
        val entity = DeckEntity(deck = Deck(0L, "newName", cards, listOf(deckHistory)))

        val model = entity.toModel(listOf(batman, flash))
        assertEquals(id, model.id)
        assertEquals("newName", model.name)
        assertTrue(model.cards.contains(batman))
        assertTrue(model.cards.contains(flash))

        val deckHistory = model.deckHistoryList.first()
        assertEquals(id, deckHistory.deckId)
        assertEquals(name, deckHistory.name)
        assertTrue(deckHistory.cards.contains(batman))
        assertTrue(deckHistory.cards.contains(flash))
    }
}