package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DeckTest {

    private val deckName = "deckName"
    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    @Nested
    inner class UpdateDeck {

        @Test
        fun `Update deck name only`() {
            val deck = Deck(id = 0L, name = deckName, listOf(batman))

            val newDeck = deck.updateDeck("newDeckName", emptyList())
            assertNull(newDeck.id)
            assertEquals("newDeckName", newDeck.name)
            assertTrue(newDeck.cards.contains(batman))
        }

        @Test
        fun `Update only cards in the deck`() {
            val deck = Deck(id = 0L, name = deckName, listOf(batman))

            val newDeck = deck.updateDeck(null, listOf(flash))
            assertNull(newDeck.id)
            assertEquals(deckName, newDeck.name)
            assertTrue(newDeck.cards.contains(flash))
        }

        @Test
        fun `Update deck name and cards`() {
            val deck = Deck(id = 0L, name = deckName, listOf(batman))

            val newDeck = deck.updateDeck("newDeckName", listOf(flash))
            assertNull(newDeck.id)
            assertEquals("newDeckName", newDeck.name)
            assertTrue(newDeck.cards.contains(flash))
        }

    }

}