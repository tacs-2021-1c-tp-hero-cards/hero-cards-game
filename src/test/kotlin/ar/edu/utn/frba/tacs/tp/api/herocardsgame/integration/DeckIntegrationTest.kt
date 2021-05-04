package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class DeckIntegrationTest {

    private val deckMapMock: HashMap<Long, Deck> = hashMapOf()
    private val instance = DeckIntegration(deckMapMock)

    @Test
    fun getAllDeck() {
        val deck = Deck(0L, "deckTest", listOf(BuilderContextUtils.buildBatman(), BuilderContextUtils.buildFlash()))
        deckMapMock[0L] = deck

        val allDeck = instance.getAllDeck()
        assertEquals(1, allDeck.size)

        val found = allDeck.first()
        assertEquals(deck, found)
    }

    @Test
    fun saveDeck() {
        instance.saveDeck(
            deck = Deck(
                name = "deckTest",
                cards = listOf(BuilderContextUtils.buildBatman(), BuilderContextUtils.buildFlash())
            )
        )

        val allDeck = deckMapMock.values.toList()
        assertEquals(1, allDeck.size)

        val foundDeck = allDeck.first()
        assertEquals("deckTest", foundDeck.name)
        assertEquals(0L, foundDeck.id)

        val cards = foundDeck.cards
        assertEquals(2, cards.size)
    }

    @Test
    fun deleteDeck() {
        val deck = Deck(0L, "deckTest", listOf(BuilderContextUtils.buildBatman(), BuilderContextUtils.buildFlash()))
        deckMapMock[0L] = deck

        instance.deleteDeck(deck.id!!)
        assertTrue(deckMapMock.values.toList().isEmpty())
    }
}
