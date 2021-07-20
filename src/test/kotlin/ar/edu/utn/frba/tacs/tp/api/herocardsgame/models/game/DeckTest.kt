package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidDeckVersionException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DeckTest {

    private val deckName = "deckName"
    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    val deck = Deck(0L, deckName, listOf(batman))

    @Nested
    inner class UpdateDeck {

        @Test
        fun `Update deck name only`() {
            val newDeck = deck.updateDeck("newDeckName", emptyList())

            assertEquals(0L, newDeck.id)
            assertEquals("newDeckName", newDeck.name)
            assertTrue(newDeck.cards.contains(batman))

            val deckHistory = newDeck.deckHistoryList.first()
            assertEquals(0L, deckHistory.deckId)
            assertNull(deckHistory.deckVersion)
            assertEquals(deckName, deckHistory.name)
            assertTrue(deckHistory.cards.contains(batman))

        }

        @Test
        fun `Update only cards in the deck`() {
            val newDeck = deck.updateDeck(null, listOf(flash))

            assertEquals(0L, newDeck.id)
            assertEquals(deckName, newDeck.name)
            assertTrue(newDeck.cards.contains(flash))

            val deckHistory = newDeck.deckHistoryList.first()
            assertEquals(0L, deckHistory.deckId)
            assertNull(deckHistory.deckVersion)
            assertEquals(deckName, deckHistory.name)
            assertTrue(deckHistory.cards.contains(batman))
        }

        @Test
        fun `Update deck name and cards`() {
            val newDeck = deck.updateDeck("newDeckName", listOf(flash))

            assertEquals(0L, newDeck.id)
            assertEquals("newDeckName", newDeck.name)
            assertTrue(newDeck.cards.contains(flash))

            val deckHistory = newDeck.deckHistoryList.first()
            assertEquals(0L, deckHistory.deckId)
            assertNull(deckHistory.deckVersion)
            assertEquals(deckName, deckHistory.name)
            assertTrue(deckHistory.cards.contains(batman))
        }

    }

    @Nested
    inner class SearchDeckVersion {

        @Test
        fun `Search deck version that are in the history`() {
            val newDeck =
                Deck(0L, "newDeckName", listOf(flash), listOf(DeckHistory(deck).copy(deckVersion= 0L)))

            val foundDeckVersion = newDeck.searchDeckVersion(0L)

            assertEquals(0L, foundDeckVersion.deckId)
            assertEquals(0L, foundDeckVersion.deckVersion)
            assertEquals(deckName, foundDeckVersion.name)
            assertEquals(listOf(batman), foundDeckVersion.cards)
        }

        @Test
        fun `Search deck version that is not in the history`() {
            assertThrows(InvalidDeckVersionException::class.java) {
                deck.searchDeckVersion(1L)
            }
        }

    }

}