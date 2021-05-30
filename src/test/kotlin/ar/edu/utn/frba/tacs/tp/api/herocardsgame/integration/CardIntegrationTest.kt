package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class CardIntegrationTest {

    lateinit var dao: Dao
    lateinit var instance: CardIntegration

    private val superHeroIntegrationMock: SuperHeroIntegration = mock(SuperHeroIntegration::class.java)

    val batman = BuilderContextUtils.buildBatman()
    val flash = BuilderContextUtils.buildFlash()
    val batmanII = BuilderContextUtils.buildBatmanII()

    @BeforeEach
    fun init() {
        dao = Dao()
        instance = CardIntegration(dao, superHeroIntegrationMock)
    }

    @Nested
    inner class SaveCard {

        @Test
        fun `Saved card when the card already exists`() {
            val lastUse = dao.saveCard(batman).lastUse

            instance.saveCard(batman)

            val allCard = dao.getAllCard().map { it.toModel() }
            assertEquals(1, allCard.size)
            assertTrue(allCard.contains(batman))

            assertNotEquals(lastUse, dao.getAllCard().first().lastUse)
        }

        @Test
        fun `Saved card when card does not exist and did not exceed limit`() {
            dao.saveCard(batman)

            instance.saveCard(flash)

            val allCard = dao.getAllCard().map { it.toModel() }
            assertEquals(2, allCard.size)
            assertTrue(allCard.contains(batman))
            assertTrue(allCard.contains(flash))
        }

        @Test
        fun `Saved card when card does not exist and exceeded the limit`() {
            dao.saveCard(batman)
            dao.saveCard(batmanII)

            instance.limitCard = 2

            instance.saveCard(flash)

            val allCard = dao.getAllCard().map { it.toModel() }
            assertEquals(2, allCard.size)
            assertTrue(allCard.contains(batmanII))
            assertTrue(allCard.contains(flash))
        }

    }

    @Nested
    inner class GetCardById {

        @Test
        fun `Card exists in database`() {
            dao.saveCard(batman)

            val found = instance.getCardById(batman.id.toString())
            assertEquals(batman, found)

            verify(superHeroIntegrationMock, times(0)).getCard(batman.id.toString())
        }

        @Test
        fun `Card not exist in database and exists in external api`() {
            `when`(superHeroIntegrationMock.getCard(batman.id.toString())).thenReturn(batman)

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
    inner class SearchCardByName{

        @Test
        fun `Search cards by name and non find any`(){
            `when`(superHeroIntegrationMock.searchCardByName("characterName")).thenReturn(emptyList())

            val cards = instance.searchCardByName("characterName")
            assertTrue(cards.isEmpty())
        }

        @Test
        fun `Search cards by name and find 3`(){
            `when`(superHeroIntegrationMock.searchCardByName("characterName")).thenReturn(listOf(batman, batmanII, flash))

            val cards = instance.searchCardByName("characterName")

            assertEquals(3, cards.size)
            assertTrue(cards.contains(batman))
            assertTrue(cards.contains(batmanII))
            assertTrue(cards.contains(flash))
        }

    }

    @Nested
    inner class GetSavedCards{

        @Test
        fun `Exist saved cards in database`(){
            dao.saveCard(batman)
            dao.saveCard(batmanII)
            dao.saveCard(flash)

            val savedCards = instance.getSavedCards()

            assertEquals(3, savedCards.size)
            assertTrue(savedCards.contains(batman))
            assertTrue(savedCards.contains(batmanII))
            assertTrue(savedCards.contains(flash))
        }

        @Test
        fun `No saved cards and generate  list of random cards`(){
            `when`(superHeroIntegrationMock.getRandomCards(3)).thenReturn(listOf(batman, batmanII, flash))

            instance.limitCard = 3

            val savedCards = instance.getSavedCards()

            assertEquals(3, savedCards.size)
            assertTrue(savedCards.contains(batman))
            assertTrue(savedCards.contains(batmanII))
            assertTrue(savedCards.contains(flash))
        }

    }
}