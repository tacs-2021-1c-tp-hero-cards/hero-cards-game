package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class DeckIntegrationTest {

    private val deckMapMock: HashMap<Long, Deck> = hashMapOf()
    private val instance = DeckIntegration(deckMapMock)

    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    @Test
    fun getAllDeck() {
        val deck = Deck(0L, "deckTest", listOf(batman, flash))
        deckMapMock[0L] = deck

        val allDeck = instance.getAllDeck()
        assertEquals(1, allDeck.size)
        assertEquals(deck, allDeck.first())
    }

    @Test
    fun saveDeck() {
        instance.saveDeck(
            deck = Deck(
                name = "deckTest",
                cards = listOf(batman, flash)
            )
        )

        val allDeck = deckMapMock.values.toList()
        assertEquals(1, allDeck.size)

        val foundDeck = allDeck.first()
        assertEquals("deckTest", foundDeck.name)
        assertEquals(0L, foundDeck.id)

        val cards = foundDeck.cards
        assertEquals(2, cards.size)
        assertTrue(cards.contains(batman))
        assertTrue(cards.contains(flash))
    }

    @Test
    fun deleteDeck() {
        deckMapMock[0L] = Deck(0L, "deckTest", listOf(batman, flash))
        assertTrue(deckMapMock.values.toList().isNotEmpty())

        instance.deleteDeck(0L)
        assertTrue(deckMapMock.values.toList().isEmpty())
    }
}
