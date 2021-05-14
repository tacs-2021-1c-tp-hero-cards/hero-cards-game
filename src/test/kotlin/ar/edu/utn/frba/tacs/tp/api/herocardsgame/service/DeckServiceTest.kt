package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.DeckIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.SuperHeroIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class DeckServiceTest {

    private val superHeroIntegrationMock = mock(SuperHeroIntegration::class.java)
    private val deckIntegrationMock = mock(DeckIntegration::class.java)
    private val instance = DeckService(superHeroIntegrationMock, deckIntegrationMock)

    private val deckId = 0L
    private val deckName = "testDeck"
    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    @Test
    fun addDeck() {
        `when`(superHeroIntegrationMock.getCard("1")).thenReturn(batman)
        `when`(superHeroIntegrationMock.getCard("2")).thenReturn(flash)
        `when`(deckIntegrationMock.calculateId()).thenReturn(deckId)

        instance.addDeck(deckName, listOf("1", "2"))

        verify(deckIntegrationMock, times(1))
            .saveDeck(deck = Deck(name = deckName, cards = listOf(batman, flash)))
    }

    @Test
    fun addDeckWithId() {
        `when`(superHeroIntegrationMock.getCard("1")).thenReturn(batman)
        `when`(superHeroIntegrationMock.getCard("2")).thenReturn(flash)

        instance.addDeck(deckName, listOf("1", "2"))

        verify(deckIntegrationMock, times(1))
            .saveDeck(Deck(name = deckName, cards = listOf(batman, flash)))
    }

    @Test
    fun deleteDeck() {
        `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(Deck(deckId, deckName, listOf(batman))))

        instance.deleteDeck(deckId.toString())
        verify(deckIntegrationMock, times(1)).deleteDeck(deckId)
    }

    @Test
    fun deleteEmptyDeck() {
        `when`(deckIntegrationMock.getAllDeck()).thenReturn(emptyList())
        assertThrows(ElementNotFoundException::class.java) {
            instance.deleteDeck(deckId.toString())
        }
    }

    @Test
    fun buildDeck() {
        `when`(superHeroIntegrationMock.getCard("1")).thenReturn(batman)
        `when`(superHeroIntegrationMock.getCard("2")).thenReturn(flash)

        val deck = instance.buildDeck(deckName, listOf("1", "2"))
        assertNull(deck.id)
        assertEquals(deckName, deck.name)
        assertEquals(2, deck.cards.size)
    }

    @Test
    fun addCardInDeckNotFoundDeck() {
        `when`(superHeroIntegrationMock.getCard("1")).thenReturn(batman)
        assertThrows(ElementNotFoundException::class.java) {
            instance.addCardInDeck("deckId", "1")
        }
    }

    @Test
    fun addCardInDeckNotFoundCard() {
        `when`(superHeroIntegrationMock.getCard("1")).thenThrow(ElementNotFoundException::class.java)
        assertThrows(ElementNotFoundException::class.java) {
            instance.addCardInDeck("deckId", "1")
        }
    }

    @Test
    fun addCardInDeck() {
        val deck = Deck(deckId, deckName, listOf(batman))
        `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(deck))
        `when`(superHeroIntegrationMock.getCard("2")).thenReturn(flash)

        instance.addCardInDeck(deckId.toString(), "2")

        verify(deckIntegrationMock, times(1))
            .saveDeck(deck.copy(cards = listOf(batman, flash)))
    }

    @Test
    fun deleteCardInDeckNotFoundDeck() {
        `when`(superHeroIntegrationMock.getCard("1")).thenReturn(batman)
        assertThrows(ElementNotFoundException::class.java) {
            instance.deleteCardInDeck("deckId", "1")
        }
    }

    @Test
    fun deleteCardInDeckNotFoundCard() {
        `when`(superHeroIntegrationMock.getCard("1")).thenThrow(ElementNotFoundException::class.java)
        assertThrows(ElementNotFoundException::class.java) {
            instance.deleteCardInDeck("deckId", "1")
        }
    }

    @Test
    fun deleteCardInDeck() {
        val deck = Deck(deckId, deckName, listOf(batman, flash))
        `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(deck))

        instance.deleteCardInDeck(deckId.toString(), "2")

        verify(deckIntegrationMock, times(1)).saveDeck(deck.copy(cards = listOf(batman)))
    }

    @Test
    fun searchDeck() {
        val deck = Deck(deckId, deckName, listOf(batman, flash))
        val deck2 = Deck(1L, deckName, listOf(batman, flash))
        val deck3 = Deck(deckId, "testDeck2", listOf(batman, flash))
        `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(deck, deck2, deck3))

        val foundDecks = instance.searchDeck(deckId.toString(), deckName)
        assertEquals(1, foundDecks.size)

        val foundDeck = foundDecks.first()
        assertEquals(deck, foundDeck)
    }

    @Test
    fun searchDeckByName() {
        val deck = Deck(deckId, deckName, listOf(batman, flash))
        val deck2 = Deck(deckId, "testDeck2", listOf(batman, flash))
        `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(deck, deck2))

        val foundDecks = instance.searchDeck(deckId.toString(), deckName)
        assertEquals(1, foundDecks.size)

        val foundDeck = foundDecks.first()
        assertEquals(deck, foundDeck)
    }

    @Test
    fun searchDeckById() {
        val deck = Deck(deckId, deckName, listOf(batman, flash))
        val deck2 = Deck(1L, deckName, listOf(batman, flash))
        `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(deck, deck2))

        val foundDecks = instance.searchDeck(deckId.toString(), deckName)
        assertEquals(1, foundDecks.size)

        val foundDeck = foundDecks.first()
        assertEquals(deck, foundDeck)
    }

    @Test
    fun updateDeckByName() {
        val deck = Deck(deckId, deckName, listOf(batman))
        `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(deck))

        instance.updateDeck(deckId.toString(), "deckName2", emptyList())

        verify(deckIntegrationMock, times(1)).saveDeck(deck = deck.copy(name = "deckName2"))
    }

    @Test
    fun updateDeckByCards() {
        val deck = Deck(deckId, deckName, listOf(batman))
        `when`(superHeroIntegrationMock.getCard("2")).thenReturn(flash)
        `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(deck))

        instance.updateDeck(deckId.toString(), null, listOf("2"))

        verify(deckIntegrationMock, times(1)).saveDeck(deck = deck.copy(cards = listOf(flash)))
    }
}