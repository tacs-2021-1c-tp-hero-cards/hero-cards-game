package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck.DeckEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck.DeckHistoryEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository.DeckRepository
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class DeckIntegrationTest {

    private val cardIntegrationMock = mock(CardIntegration::class.java)
    private val repositoryMock = mock(DeckRepository::class.java)
    private val instance = DeckIntegration(cardIntegrationMock, repositoryMock)

    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    private val id = 0L
    private val name = "deckName"

    private val deckHistory = DeckHistory(deckId = 0L, name = name, cards = listOf(batman))
    private val deckHistoryEntity = DeckHistoryEntity(deckHistory)

    private val deck = Deck(0L, name, listOf(batman, flash))
    private val deckEntity = DeckEntity(deck)

    @Nested
    inner class GetDeckById {

        @Test
        fun `Get deck by id`() {
            `when`(repositoryMock.getById(0L)).thenReturn(deckEntity)

            `when`(cardIntegrationMock.getCardById("69")).thenReturn(batman)
            `when`(cardIntegrationMock.getCardById("2")).thenReturn(flash)

            val deckById = instance.getDeckById(id)
            assertEquals(deck, deckById)
        }

        @Test
        fun `Get deck by id but not exist`() {
            `when`(repositoryMock.getById(0L)).thenReturn(deckEntity)

            Assertions.assertThrows(ElementNotFoundException::class.java) {
                instance.getDeckById(1L)
            }
        }

    }

    @Nested
    inner class GetDeckByIdOrName {

        @Test
        fun `Get deck by name and non find`() {
            `when`(repositoryMock.findDeckByIdAndName(name = "deckName2")).thenReturn(emptyList())

            val decks = instance.getDeckByIdOrName(name = "deckName2")
            assertTrue(decks.isEmpty())
        }

        @Test
        fun `Get deck by name and non exist`() {
            val decks = instance.getDeckByIdOrName(name = name)
            assertTrue(decks.isEmpty())
        }

        @Test
        fun `Get deck by name and find`() {
            `when`(repositoryMock.findDeckByIdAndName(name = name)).thenReturn(listOf(deckEntity))
            `when`(cardIntegrationMock.getCardById(batman.id.toString())).thenReturn(batman)
            `when`(cardIntegrationMock.getCardById(flash.id.toString())).thenReturn(flash)

            val decks = instance.getDeckByIdOrName(name = name)
            assertEquals(1, decks.size)
            assertTrue(decks.contains(deck))
        }

        @Test
        fun `Deck by name and id and find`() {
            `when`(repositoryMock.findDeckByIdAndName(id.toString(), name)).thenReturn(listOf(deckEntity))

            `when`(cardIntegrationMock.getCardById(batman.id.toString())).thenReturn(batman)
            `when`(cardIntegrationMock.getCardById(flash.id.toString())).thenReturn(flash)

            val decks = instance.getDeckByIdOrName(id = id.toString(), name = name)
            assertEquals(1, decks.size)
            assertTrue(decks.contains(deck))
        }

    }

    @Test
    fun saveDeck() {
        `when`(cardIntegrationMock.getCardById(batman.id.toString())).thenReturn(batman)
        `when`(cardIntegrationMock.saveCard(batman)).thenReturn(batman)
        `when`(cardIntegrationMock.getCardById(flash.id.toString())).thenReturn(flash)
        `when`(cardIntegrationMock.saveCard(flash)).thenReturn(flash)
        `when`(repositoryMock.save(deckEntity)).thenReturn(deckEntity)

        instance.saveDeck(deck)
        verify(repositoryMock, times(1)).save(deckEntity)
    }

    @Nested
    inner class DeleteDeck {

        @Test
        fun `Delete deck when non exist`() {
            `when`(repositoryMock.getById(id)).thenReturn(null)

            Assertions.assertThrows(ElementNotFoundException::class.java) {
                instance.deleteDeck(id)
            }
        }

        @Test
        fun `Delete deck when exists`() {
            `when`(repositoryMock.getById(id)).thenReturn(deckEntity)

            instance.deleteDeck(id)
            verify(repositoryMock, times(1)).deleteById(id)
        }

    }
}
