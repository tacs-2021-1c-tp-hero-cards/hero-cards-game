package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.FileConstructorUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DeckTest {

    private val deckId = 0L
    private val deckName = "decktest"
    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    @Test
    fun addCard() {
        val deck = Deck(deckId, deckName, listOf(batman))
        deck.addCard(flash)
        assertEquals(2, deck.cards.size)
    }

    @Test
    fun removeCard() {
        val deck = Deck(deckId, deckName, listOf(batman, flash))
        deck.removeCard(flash.id)
        assertEquals(1, deck.cards.size)
    }

    @Test
    fun removeAllCard() {
        val deck = Deck(deckId, deckName, listOf(batman, flash))
        deck.removeAllCard()
        assertTrue(deck.cards.isEmpty())
    }

}