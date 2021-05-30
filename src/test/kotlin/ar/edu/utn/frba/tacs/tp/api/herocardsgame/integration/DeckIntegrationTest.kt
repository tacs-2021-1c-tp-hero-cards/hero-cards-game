package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
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
    private val name = "deckName"

    private val deck = Deck(id, name, listOf(batman, flash))

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
        fun `Get deck by id but no user exists in the system`() {
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

    @Test
    fun saveDeck() {
        `when`(cardIntegrationMock.getCardById(batman.id.toString())).thenReturn(batman)
        `when`(cardIntegrationMock.saveCard(batman)).thenReturn(batman)
        `when`(cardIntegrationMock.getCardById(flash.id.toString())).thenReturn(flash)
        `when`(cardIntegrationMock.saveCard(flash)).thenReturn(flash)

        instance.saveDeck(deck)

        val allDeck = dao.getAllDeck()
        assertEquals(1, allDeck.size)

        val foundDeck = allDeck.first().toModel(listOf(batman, flash))
        assertEquals(id, foundDeck.id)
        assertEquals(name, foundDeck.name)
        assertTrue(foundDeck.cards.contains(batman))
        assertTrue(foundDeck.cards.contains(flash))
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
