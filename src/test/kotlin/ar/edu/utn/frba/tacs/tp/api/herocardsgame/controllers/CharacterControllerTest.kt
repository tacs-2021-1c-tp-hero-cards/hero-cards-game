package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.SuperHeroIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.SuperHeroClient
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.CardMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.CharacterMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.ImageMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.PowerstatsMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.context.annotation.Bean
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

internal class CharacterControllerTest {

    lateinit var superHeroClientMock: SuperHeroClient
    lateinit var instance: CharacterController

    private val batmanCharacter = BuilderContextUtils.buildBatmanCharacter()

    @BeforeEach
    fun init() {
        val context = AnnotationConfigWebApplicationContext()
        context.register(CharacterControllerTest::class.java)
        context.register(CharacterController::class.java)
        context.register(SuperHeroIntegration::class.java)
        context.register(CardMapper::class.java)
        context.register(CharacterMapper::class.java)
        context.register(PowerstatsMapper::class.java)
        context.register(ImageMapper::class.java)
        context.refresh()
        context.start()

        superHeroClientMock = context.getBean(SuperHeroClient::class.java)
        instance = context.getBean(CharacterController::class.java)
    }

    @Bean
    fun getSuperHeroClientBean(): SuperHeroClient = mock(SuperHeroClient::class.java)

    @Nested
    inner class GetCharacter {

        @Test
        fun `Search by character id, returns all information of the character`() {
            `when`(superHeroClientMock.getCharacter("70")).thenReturn(BuilderContextUtils.buildCharacterApi())

            val response = instance.getCharacter("70")
            assertEquals(200, response.statusCodeValue)
            assertEquals(response.body!!, batmanCharacter)
        }

        @Test
        fun `Search by invalid character id, returns NOT_FOUND`() {
            `when`(superHeroClientMock.getCharacter("0")).thenThrow(ElementNotFoundException("character", "id", "0"))

            val response = instance.getCharacter("0")
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

    }

}