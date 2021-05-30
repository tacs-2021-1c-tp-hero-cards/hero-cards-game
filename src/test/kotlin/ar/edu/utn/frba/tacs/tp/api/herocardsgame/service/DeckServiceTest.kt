package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.CardIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.DeckIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class DeckServiceTest {

    private val cardIntegrationMock = mock(CardIntegration::class.java)
    private val deckIntegrationMock = mock(DeckIntegration::class.java)
    private val instance = DeckService(cardIntegrationMock, deckIntegrationMock)

    private val deckId = 0L
    private val deckName = "testDeck"
    private val deck = Deck(deckId, deckName)
    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    @Test
    fun saveDeck() {
        `when`(cardIntegrationMock.getCardById("1")).thenReturn(batman)
        `when`(cardIntegrationMock.getCardById("2")).thenReturn(flash)

        instance.saveDeck(deckName, listOf("1", "2"))

        verify(deckIntegrationMock, times(1))
            .saveDeck(deck = Deck(name = deckName, cards = listOf(batman, flash)))
    }

    @Test
    fun deleteDeck() {
        instance.deleteDeck(deckId.toString())

        verify(deckIntegrationMock, times(1)).deleteDeck(deckId)
    }

    @Nested
    inner class SearchDeck {

        @Test
        fun `Search deck by name and id`() {
            instance.searchDeck(deckId.toString(), deckName)

            verify(deckIntegrationMock, times(1)).getDeckByIdOrName(deckId, deckName)
        }

        @Test
        fun `Search deck by name`() {
            instance.searchDeck(deckName = deckName)

            verify(deckIntegrationMock, times(1)).getDeckByIdOrName(null, deckName)
        }

        @Test
        fun `Search deck by id`() {
            instance.searchDeck(deckId = deckId.toString())

            verify(deckIntegrationMock, times(1)).getDeckByIdOrName(deckId, null)
        }

    }

    @Nested
    inner class UpdateDeck {

        @Test
        fun `Update deck name`() {
            `when`(deckIntegrationMock.getDeckById(deckId)).thenReturn(deck)

            instance.updateDeck(deckId.toString(), "deckName2", emptyList())

            verify(deckIntegrationMock, times(1)).saveDeck(deck = deck.copy(name = "deckName2"))
        }

        @Test
        fun `Update cards in the deck`() {
            `when`(deckIntegrationMock.getDeckById(deckId)).thenReturn(deck.copy(cards = listOf(batman)))
            `when`(cardIntegrationMock.getCardById("2")).thenReturn(flash)

            instance.updateDeck(deckId.toString(), null, listOf("2"))

            verify(deckIntegrationMock, times(1)).saveDeck(deck = deck.copy(cards = listOf(flash)))
        }

        @Test
        fun `Update deck name and cards`() {
            `when`(deckIntegrationMock.getDeckById(deckId)).thenReturn(deck.copy(cards = listOf(batman)))
            `when`(cardIntegrationMock.getCardById("2")).thenReturn(flash)

            instance.updateDeck(deckId.toString(), "deckName2", listOf("2"))

            verify(deckIntegrationMock, times(1))
                .saveDeck(deck = deck.copy(name = "deckName2", cards = listOf(flash)))
        }

    }

}