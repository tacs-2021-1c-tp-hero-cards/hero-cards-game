package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.SuperHeroIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.FileConstructorUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class DeckServiceTest {

    private val superHeroIntegrationMock = mock(SuperHeroIntegration::class.java)
    private val instance = DeckService(superHeroIntegrationMock)

    private val idDeck = 0L
    private val nameDeck = "testDeck"

    @Test
    fun buildDeck_ok() {
        `when`(superHeroIntegrationMock.getCard("1")).thenReturn(buildBatman())
        `when`(superHeroIntegrationMock.getCard("2")).thenReturn(buildFlash())

        val deck = instance.addDeck(nameDeck, listOf("1", "2"))
        assertEquals(idDeck, deck.id)
        assertEquals(nameDeck, deck.name)
        assertEquals(2, deck.cards.size)
        assertEquals(1, instance.getAllDeck().size)
    }

    @Test
    fun searchDeck() {
        `when`(superHeroIntegrationMock.getCard("1")).thenReturn(buildBatman())
        `when`(superHeroIntegrationMock.getCard("2")).thenReturn(buildFlash())

        val batmanDeck = instance.addDeck("batmanDeck", listOf("1"))
        val flashDeck = instance.addDeck("flashDeck", listOf("2"))

        val searchDecks = instance.searchDeck(flashDeck.id.toString(), batmanDeck.name)
        assertEquals(0, searchDecks.size)
    }

    @Test
    fun searchDeckByName() {
        `when`(superHeroIntegrationMock.getCard("1")).thenReturn(buildBatman())
        `when`(superHeroIntegrationMock.getCard("2")).thenReturn(buildFlash())

        val batmanDeck = instance.addDeck("batmanDeck", listOf("1"))
        instance.addDeck("flashDeck", listOf("2"))

        val searchDecks = instance.searchDeck(null, "batmanDeck")
        assertEquals(1, searchDecks.size)

        val searchDeck = searchDecks.first()
        assertEquals(batmanDeck.name, searchDeck.name)
        assertEquals(batmanDeck.id, searchDeck.id)
        assertEquals(batmanDeck.cards, searchDeck.cards)
    }

    @Test
    fun searchDeckById() {
        `when`(superHeroIntegrationMock.getCard("1")).thenReturn(buildBatman())
        `when`(superHeroIntegrationMock.getCard("2")).thenReturn(buildFlash())

        instance.addDeck("batmanDeck", listOf("1"))
        val flashDeck = instance.addDeck("flashDeck", listOf("2"))

        val searchDecks = instance.searchDeck(flashDeck.id.toString(), null)
        assertEquals(1, searchDecks.size)

        val searchDeck = searchDecks.first()
        assertEquals(flashDeck.name, searchDeck.name)
        assertEquals(flashDeck.id, searchDeck.id)
        assertEquals(flashDeck.cards, searchDeck.cards)
    }

    @Test
    fun addCardInDeckNotFoundDeck() {
        `when`(superHeroIntegrationMock.getCard("1")).thenReturn(buildBatman())
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
        `when`(superHeroIntegrationMock.getCard("1")).thenReturn(buildBatman())
        `when`(superHeroIntegrationMock.getCard("2")).thenReturn(buildFlash())

        val deck = instance.addDeck("batmanDeck", listOf("1"))

        instance.addCardInDeck(deck.id.toString(), "2")

        val foundDeck = instance.getAllDeck().first()
        assertEquals(deck.name, foundDeck.name)
        assertEquals(deck.id, foundDeck.id)
        assertEquals(2, foundDeck.cards.size)
    }

    @Test
    fun deleteCardInDeckNotFoundDeck() {
        `when`(superHeroIntegrationMock.getCard("1")).thenReturn(buildBatman())
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
        `when`(superHeroIntegrationMock.getCard("1")).thenReturn(buildBatman())
        `when`(superHeroIntegrationMock.getCard("2")).thenReturn(buildFlash())

        val deck = instance.addDeck("batmanDeck", listOf("1","2"))

        instance.deleteCardInDeck(deck.id.toString(), "2")

        val foundDeck = instance.getAllDeck().first()
        assertEquals(deck.name, foundDeck.name)
        assertEquals(deck.id, foundDeck.id)
        assertEquals(1, foundDeck.cards.size)
    }

    @Test
    fun deleteDeck(){
        `when`(superHeroIntegrationMock.getCard("1")).thenReturn(buildBatman())
        `when`(superHeroIntegrationMock.getCard("2")).thenReturn(buildFlash())

        val deck = instance.addDeck("batmanDeck", listOf("1", "2"))
        instance.deleteDeck(deck.id.toString())

        assertTrue(instance.getAllDeck().isEmpty())
    }

    private fun buildBatman() = FileConstructorUtils.createFromFile(
        "src/test/kotlin/ar/edu/utn/frba/tacs/tp/api/herocardsgame/json/card/Batman.json",
        Card::class.java
    )

    private fun buildFlash() = FileConstructorUtils.createFromFile(
        "src/test/kotlin/ar/edu/utn/frba/tacs/tp/api/herocardsgame/json/card/Flash.json",
        Card::class.java
    )
}