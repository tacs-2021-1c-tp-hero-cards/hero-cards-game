package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DeckTest {

    private val deckId = 0L
    private val deckName = "decktest"
    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    @Test
    fun rename() {
        val deck = Deck(name = deckName).rename("newDecktest")
        assertEquals("newDecktest", deck.name)
    }

    @Test
    fun rename_empty() {
        val deck = Deck(name = deckName).rename(null)
        assertEquals(deckName, deck.name)
    }

    @Test
    fun addCard() {
        val deck = Deck(name = deckName, cards = listOf(batman)).addCard(flash)
        assertEquals(2, deck.cards.size)
    }

    @Test
    fun removeCard() {
        val deck = Deck(deckId, deckName, listOf(batman, flash)).removeCard(flash.id)
        assertEquals(1, deck.cards.size)
    }

    @Test
    fun replaceCards() {
        val deck = Deck(deckId, deckName, listOf(batman)).replaceCards(listOf(flash))
        assertEquals(1, deck.cards.size)
        assertTrue(deck.cards.contains(flash))
    }

    @Test
    fun replaceCards_empty() {
        val deck = Deck(deckId, deckName, listOf(batman, flash)).replaceCards(emptyList())
        assertEquals(2, deck.cards.size)
        assertTrue(deck.cards.contains(flash))
        assertTrue(deck.cards.contains(batman))
    }

}