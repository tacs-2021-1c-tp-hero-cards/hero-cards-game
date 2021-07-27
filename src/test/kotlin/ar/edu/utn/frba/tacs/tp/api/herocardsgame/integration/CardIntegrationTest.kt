package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.CardEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository.CardRepository
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.util.*

internal class CardIntegrationTest {

    lateinit var instance: CardIntegration

    private val superHeroIntegrationMock: SuperHeroIntegration = mock(SuperHeroIntegration::class.java)
    private val cardRepositoryMock: CardRepository = mock(CardRepository::class.java)

    val batman = BuilderContextUtils.buildBatman()
    val flash = BuilderContextUtils.buildFlash()
    val batmanII = BuilderContextUtils.buildBatmanII()

    @BeforeEach
    fun init() {
        instance = CardIntegration(superHeroIntegrationMock, cardRepositoryMock)
    }

    @Nested
    inner class GetCardById {

        @Test
        fun `Card exists in database`() {
            `when`(cardRepositoryMock.findById(batman.id.toString())).thenReturn(Optional.of(CardEntity(batman)))

            val found = instance.getCardById(batman.id.toString())
            assertEquals(batman, found)

            verify(superHeroIntegrationMock, times(0)).getCard(batman.id.toString())
        }

        @Test
        fun `Card not exist in database and exists in external api`() {
            `when`(superHeroIntegrationMock.getCard(batman.id.toString())).thenReturn(batman)
            `when`(cardRepositoryMock.save(CardEntity(batman))).thenReturn(CardEntity(batman))

            val found = instance.getCardById(batman.id.toString())
            assertEquals(batman, found)

            verify(superHeroIntegrationMock, times(1)).getCard(batman.id.toString())
        }

        @Test
        fun `Card not exist in database and neither in external api`() {
            `when`(superHeroIntegrationMock.getCard(batman.id.toString())).thenThrow(ElementNotFoundException::class.java)

            assertThrows(ElementNotFoundException::class.java) {
                instance.getCardById(batman.id.toString())
            }

            verify(superHeroIntegrationMock, times(1)).getCard(batman.id.toString())
        }

    }

    @Nested
    inner class SearchCardByName {

        @Test
        fun `Search cards by name and non find any`() {
            `when`(superHeroIntegrationMock.searchCardByName("characterName")).thenReturn(emptyList())

            val cards = instance.searchCardByName("characterName")
            assertTrue(cards.isEmpty())
        }

        @Test
        fun `Search cards by name and find 3`() {
            `when`(superHeroIntegrationMock.searchCardByName("characterName"))
                .thenReturn(listOf(batman, batmanII, flash))
            `when`(cardRepositoryMock.save(CardEntity(batman))).thenReturn(CardEntity(batman))
            `when`(cardRepositoryMock.save(CardEntity(batmanII))).thenReturn(CardEntity(batmanII))
            `when`(cardRepositoryMock.save(CardEntity(flash))).thenReturn(CardEntity(flash))

            val cards = instance.searchCardByName("characterName")

            assertEquals(3, cards.size)
            assertTrue(cards.contains(batman))
            assertTrue(cards.contains(batmanII))
            assertTrue(cards.contains(flash))
        }

    }

    @Nested
    inner class GetSavedCards {

        @Test
        fun `Exist saved cards in database`() {
            `when`(cardRepositoryMock.findAll())
                .thenReturn(listOf(CardEntity(batman), CardEntity(batmanII), CardEntity(flash)))

            val savedCards = instance.getSavedCards()

            assertEquals(3, savedCards.size)
            assertTrue(savedCards.contains(batman))
            assertTrue(savedCards.contains(batmanII))
            assertTrue(savedCards.contains(flash))
        }

        @Test
        fun `No saved cards and generate list of random cards`() {
            `when`(cardRepositoryMock.findAll()).thenReturn(emptyList())
            `when`(superHeroIntegrationMock.getRandomCards(3)).thenReturn(listOf(batman, batmanII, flash))
            `when`(cardRepositoryMock.save(CardEntity(batman))).thenReturn(CardEntity(batman))
            `when`(cardRepositoryMock.save(CardEntity(batmanII))).thenReturn(CardEntity(batmanII))
            `when`(cardRepositoryMock.save(CardEntity(flash))).thenReturn(CardEntity(flash))

            instance.limitCard = 3

            val savedCards = instance.getSavedCards()

            assertEquals(3, savedCards.size)
            assertTrue(savedCards.contains(batman))
            assertTrue(savedCards.contains(batmanII))
            assertTrue(savedCards.contains(flash))
        }

    }
}