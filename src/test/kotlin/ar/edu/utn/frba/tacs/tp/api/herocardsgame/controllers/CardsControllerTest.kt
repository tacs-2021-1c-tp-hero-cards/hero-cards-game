package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.CardIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.SuperHeroIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.SuperHeroClient
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.CardMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.ImageMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.PowerstatsMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.context.annotation.Bean
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

internal class CardsControllerTest {

    lateinit var superHeroClientMock: SuperHeroClient
    lateinit var dao: Dao
    lateinit var cardIntegration: CardIntegration
    lateinit var superHeroIntegration: SuperHeroIntegration
    lateinit var instance: CardsController

    private val batman = BuilderContextUtils.buildBatman()
    private val batmanII = BuilderContextUtils.buildBatmanII()
    private val batmanIII = BuilderContextUtils.buildBatmanIII()

    @BeforeEach
    fun init() {
        val context = AnnotationConfigWebApplicationContext()
        context.register(CardsControllerTest::class.java)
        context.register(CardsController::class.java)
        context.register(CardIntegration::class.java)
        context.register(Dao::class.java)
        context.register(SuperHeroIntegration::class.java)
        context.register(CardMapper::class.java)
        context.register(PowerstatsMapper::class.java)
        context.register(ImageMapper::class.java)
        context.refresh()
        context.start()


        superHeroClientMock = context.getBean(SuperHeroClient::class.java)
        superHeroIntegration = context.getBean(SuperHeroIntegration::class.java)
        dao = context.getBean(Dao::class.java)
        cardIntegration = context.getBean(CardIntegration::class.java)
        instance = context.getBean(CardsController::class.java)
    }

    @Bean
    fun getSuperHeroClientBean(): SuperHeroClient = mock(SuperHeroClient::class.java)

    @Nested
    inner class GetCard {

        @Test
        fun `Search by character id, returns all information of the character in database`() {
            dao.saveCard(batmanII)

            val response = instance.getCard("70")
            assertEquals(200, response.statusCodeValue)
            assertEquals(response.body!!, batmanII)
            verify(superHeroClientMock, times(0)).getCharacter("70")
        }

        @Test
        fun `Search by character id, returns all information of the character`() {
            `when`(superHeroClientMock.getCharacter("70")).thenReturn(BuilderContextUtils.buildCharacterApi())

            val response = instance.getCard("70")
            assertEquals(200, response.statusCodeValue)
            assertEquals(response.body!!, batmanII)
        }

        @Test
        fun `Search by invalid character id, returns NOT_FOUND`() {
            `when`(superHeroClientMock.getCharacter("0")).thenThrow(ElementNotFoundException("character", "0"))

            val response = instance.getCard("0")
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }
    }

    @Nested
    inner class GetCardByName {

        @Test
        fun `Search character by name, returns all the information of the characters`() {
            `when`(superHeroClientMock.getCharacterByName("batman"))
                .thenReturn(BuilderContextUtils.buildCharactersSearchApi())

            val response = instance.getCardByName("batman")
            assertEquals(200, response.statusCodeValue)

            val body = response.body!!
            assertTrue(body.contains(batman))
            assertTrue(body.contains(batmanII))
            assertTrue(body.contains(batmanIII))
        }

        @Test
        fun `Search character by invalid name, returns NOT_FOUND`() {
            `when`(superHeroClientMock.getCharacterByName("zzz"))
                .thenThrow(ElementNotFoundException("character", "zzz"))

            val response = instance.getCardByName("zzz")
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }
    }

    @Nested
    inner class GetSavedCards{

        @Test
        fun `Exist saved cards in database`(){
            dao.saveCard(batman)
            dao.saveCard(batmanII)
            dao.saveCard(batmanIII)

            val response = instance.getSavedCards()
            assertEquals(200, response.statusCodeValue)

            val body = response.body!!
            assertEquals(3, body.size)
            assertTrue(body.contains(batman))
            assertTrue(body.contains(batmanII))
            assertTrue(body.contains(batmanIII))
        }

        @Test
        fun `No saved cards and generate list of random cards`(){
            `when`(superHeroClientMock.getCharacter(anyString())).thenReturn(BuilderContextUtils.buildCharacterApi())

            cardIntegration.limitCard = 3
            superHeroIntegration.totalCard = 3

            val response = instance.getSavedCards()
            assertEquals(200, response.statusCodeValue)

            val body = response.body!!
            assertEquals(3, body.size)
            assertTrue(body.contains(batmanII))

            verify(superHeroClientMock, times(1)).getCharacter("1")
            verify(superHeroClientMock, times(1)).getCharacter("2")
            verify(superHeroClientMock, times(1)).getCharacter("3")
        }

    }

}