package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.SuperHeroIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.SuperHeroClient
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.CardMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.ImageMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.PowerstatsMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.context.annotation.Bean
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

internal class CardsControllerTest {

    lateinit var superHeroClientMock: SuperHeroClient
    lateinit var instance: CardsController

    @BeforeEach
    fun init() {
        val context = AnnotationConfigWebApplicationContext()
        context.register(CardsControllerTest::class.java)
        context.register(CardsController::class.java)
        context.register(SuperHeroIntegration::class.java)
        context.register(CardMapper::class.java)
        context.register(PowerstatsMapper::class.java)
        context.register(ImageMapper::class.java)
        context.refresh()
        context.start()

        superHeroClientMock = context.getBean(SuperHeroClient::class.java)
        instance = context.getBean(CardsController::class.java)
    }

    @Bean
    fun getSuperHeroClientBean(): SuperHeroClient = mock(SuperHeroClient::class.java)

    @Nested
    inner class GetCard {

        @Test
        fun `Search by character id, returns all information of the character`() {
            `when`(superHeroClientMock.getCharacter("70")).thenReturn(BuilderContextUtils.buildCharacterApi())

            val response = instance.getCard("70")
            assertEquals(200, response.statusCodeValue)
            assertEquals(response.body!!, BuilderContextUtils.buildBatmanII())
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
            assertTrue(body.contains(BuilderContextUtils.buildBatman()))
            assertTrue(body.contains(BuilderContextUtils.buildBatmanII()))
            assertTrue(body.contains(BuilderContextUtils.buildBatmanIII()))
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
}