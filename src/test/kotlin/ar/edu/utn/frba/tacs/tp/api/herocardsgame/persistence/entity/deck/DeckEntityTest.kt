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
    private val cardsId: List<Long> = listOf(69L, 2L)
    private val deckHistoryList = listOf(DeckHistory(id, version, name, cards))

    @Nested
    inner class ToEntity {

        @Test
        fun `Build entity without id with model with id`() {
            val model = Deck(id, version, name, cards)

            val entity = DeckEntity(deck = model)
            assertEquals(id, entity.id)
            assertEquals(version, entity.version)
            assertEquals(name, entity.name)
            assertEquals(cardsId, entity.cardIds)
            assertTrue(entity.deckHistoryIds.isEmpty())
        }

        @Test
        fun `Build entity with id`() {
            val model = Deck(null, null, name, cards)

            val entity = DeckEntity(id = id, version = version, deck = model)
            assertEquals(id, entity.id)
            assertEquals(version, entity.version)
            assertEquals(name, entity.name)
            assertEquals(cardsId, entity.cardIds)
            assertTrue(entity.deckHistoryIds.isEmpty())
        }

        @Test
        fun `Build entity without version with model with version`() {
            val model = Deck(id, version, name, cards)

            val entity = DeckEntity(deck = model)
            assertEquals(id, entity.id)
            assertEquals(version, entity.version)
            assertEquals(name, entity.name)
            assertEquals(cardsId, entity.cardIds)
            assertTrue(entity.deckHistoryIds.isEmpty())
        }

        @Test
        fun `Build entity with version`() {
            val model = Deck(null, null, name, cards)

            val entity = DeckEntity(id = id, version = version, deck = model)
            assertEquals(id, entity.id)
            assertEquals(version, entity.version)
            assertEquals(name, entity.name)
            assertEquals(cardsId, entity.cardIds)
            assertTrue(entity.deckHistoryIds.isEmpty())
        }

        @Test
        fun `Build entity with deck historty`() {
            val model = Deck(id, 1L, "newName", cards, deckHistoryList)

            val entity = DeckEntity(deck = model)
            assertEquals(id, entity.id)
            assertEquals(1L, entity.version)
            assertEquals("newName", entity.name)
            assertEquals(cardsId, entity.cardIds)

            val deckHistoryIds = entity.deckHistoryIds
            assertTrue(deckHistoryIds.contains(version))
        }

    }

    @Test
    fun toModel() {
        val entity = DeckEntity(deck = Deck(0L, 1L, "newName", cards, deckHistoryList))

        val model = entity.toModel(listOf(batman, flash), deckHistoryList)
        assertEquals(id, model.id)
        assertEquals(1L, model.version)
        assertEquals("newName", model.name)
        assertTrue(model.cards.contains(batman))
        assertTrue(model.cards.contains(flash))

        val deckHistory = model.deckHistoryList.first()
        assertEquals(id, deckHistory.id)
        assertEquals(version, deckHistory.version)
        assertEquals(name, deckHistory.name)
        assertTrue(deckHistory.cards.contains(batman))
        assertTrue(deckHistory.cards.contains(flash))
    }
}