package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.Mockito.*

internal class DeckIntegrationTest {

    lateinit var dao: Dao
    lateinit var instance: DeckIntegration

    private val cardIntegrationMock: CardIntegration = mock(CardIntegration::class.java)

    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    private val id = 0L
    private val version = 0L
    private val name = "deckName"

    private val deck = Deck(id, version, name, listOf(batman, flash))
    private val deckHistory = DeckHistory(deck)

    @BeforeEach
    fun init() {
        dao = Dao()
        instance = DeckIntegration(dao, cardIntegrationMock)
    }

    @Nested
    inner class GetDeckById {

        @Test
        fun `Get deck by id`() {
            dao.saveDeck(deck)

            `when`(cardIntegrationMock.getCardById("69")).thenReturn(batman)
            `when`(cardIntegrationMock.getCardById("2")).thenReturn(flash)

            val deckById = instance.getDeckById(id)
            assertEquals(deck, deckById)
        }

        @Test
        fun `Get deck by id but not exist`() {
            dao.saveDeck(deck.copy(id = 1L))

            Assertions.assertThrows(ElementNotFoundException::class.java) {
                instance.getDeckById(id)
            }
        }

        @Test
        fun `Get deck by id but no deck exists in the system`() {
            Assertions.assertThrows(ElementNotFoundException::class.java) {
                instance.getDeckById(id)
            }
        }

    }

    @Nested
    inner class GetDeckByIdOrName {

        @Test
        fun `Get deck by name and non find`() {
            dao.saveDeck(deck)

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
            dao.saveDeck(deck)

            `when`(cardIntegrationMock.getCardById(batman.id.toString())).thenReturn(batman)
            `when`(cardIntegrationMock.getCardById(flash.id.toString())).thenReturn(flash)

            val decks = instance.getDeckByIdOrName(name = name)
            assertEquals(1, decks.size)
            assertTrue(decks.contains(deck))
        }

        @Test
        fun `Deck by name and id and non find`() {
            dao.saveDeck(deck)

            val decks = instance.getDeckByIdOrName(id = id, name = "deckName2")
            assertTrue(decks.isEmpty())
        }

        @Test
        fun `Deck by name and id and non exist`() {
            val decks = instance.getDeckByIdOrName(id = id, name = "deckName2")
            assertTrue(decks.isEmpty())
        }

        @Test
        fun `Deck by name and id and find`() {
            dao.saveDeck(deck)

            `when`(cardIntegrationMock.getCardById(batman.id.toString())).thenReturn(batman)
            `when`(cardIntegrationMock.getCardById(flash.id.toString())).thenReturn(flash)

            val decks = instance.getDeckByIdOrName(id = id, name = name)
            assertEquals(1, decks.size)
            assertTrue(decks.contains(deck))
        }

    }

    @Nested
    inner class GetDeckHistoryById {

        @Test
        fun `Get deck history by id`() {
            dao.saveDeckHistory(deckHistory)
            val otherDeckHistory = DeckHistory(id, 1L, name, listOf(batman))
            dao.saveDeckHistory(otherDeckHistory)

            `when`(cardIntegrationMock.getCardById("69")).thenReturn(batman)
            `when`(cardIntegrationMock.getCardById("2")).thenReturn(flash)

            val deckHistoryList = instance.getDeckHistoryById(id)

            assertEquals(2, deckHistoryList.size)
            assertTrue(deckHistoryList.contains(otherDeckHistory))
            assertTrue(deckHistoryList.contains(otherDeckHistory))
        }

        @Test
        fun `Get deck history by id but not exist`() {
            dao.saveDeckHistory(deckHistory.copy(id = 1L))
            assertTrue(instance.getDeckHistoryById(id).isEmpty())
        }

        @Test
        fun `Get deck history by id but no deck history exists in the system`() {
            assertTrue(instance.getDeckHistoryById(id).isEmpty())
        }

    }

    @Test
    fun saveDeck() {
        `when`(cardIntegrationMock.getCardById(batman.id.toString())).thenReturn(batman)
        `when`(cardIntegrationMock.saveCard(batman)).thenReturn(batman)
        `when`(cardIntegrationMock.getCardById(flash.id.toString())).thenReturn(flash)
        `when`(cardIntegrationMock.saveCard(flash)).thenReturn(flash)

        instance.saveDeck(Deck(id, 1L, name, listOf(batman, flash), listOf(deckHistory)))

        val allDeck = dao.getAllDeck()
        assertEquals(1, allDeck.size)

        val foundDeck = allDeck.first().toModel(listOf(batman, flash), listOf(deckHistory))
        assertEquals(id, foundDeck.id)
        assertEquals(1L, foundDeck.version)
        assertEquals(name, foundDeck.name)
        assertTrue(foundDeck.cards.contains(batman))
        assertTrue(foundDeck.cards.contains(flash))

        val deckHistory = foundDeck.deckHistoryList.first()
        assertEquals(id, deckHistory.id)
        assertEquals(0L, deckHistory.version)
        assertEquals(name, deckHistory.name)
        assertTrue(deckHistory.cards.contains(batman))
        assertTrue(deckHistory.cards.contains(flash))

    }

    @Nested
    inner class DeleteDeck {

        @Test
        fun `Delete deck when non exist`() {
            Assertions.assertThrows(ElementNotFoundException::class.java) {
                instance.deleteDeck(id)
            }
        }

        @Test
        fun `Delete deck when exists`() {
            dao.saveDeck(deck)

            `when`(cardIntegrationMock.getCardById(batman.id.toString())).thenReturn(batman)
            `when`(cardIntegrationMock.getCardById(flash.id.toString())).thenReturn(flash)

            instance.deleteDeck(id)

            assertTrue(dao.getAllDeck().isEmpty())
        }

    }
}
