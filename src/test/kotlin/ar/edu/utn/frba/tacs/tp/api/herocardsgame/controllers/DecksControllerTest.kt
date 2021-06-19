package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.CardIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.DeckIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.SuperHeroIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.SuperHeroClient
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.CardMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.ImageMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.PowerstatsMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Powerstats
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateDeckRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.UpdateDeckRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.DeckService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.context.annotation.Bean
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

internal class DecksControllerTest {

    lateinit var superHeroClientMock: SuperHeroClient
    lateinit var instance: DecksController

    @BeforeEach
    fun init() {
        val context = AnnotationConfigWebApplicationContext()
        context.register(DecksControllerTest::class.java)
        context.register(DecksController::class.java)
        context.register(DeckService::class.java)
        context.register(SuperHeroIntegration::class.java)
        context.register(DeckIntegration::class.java)
        context.register(CardMapper::class.java)
        context.register(PowerstatsMapper::class.java)
        context.register(ImageMapper::class.java)
        context.register(SuperHeroIntegration::class.java)
        context.register(CardIntegration::class.java)
        context.register(Dao::class.java)

        context.refresh()
        context.start()

        superHeroClientMock = context.getBean(SuperHeroClient::class.java)
        instance = context.getBean(DecksController::class.java)
    }

    @Bean
    fun getSuperHeroClientBean(): SuperHeroClient = mock(SuperHeroClient::class.java)

    @Nested
    inner class GetDecks {

        @Test
        fun `Search all decks`() {
            `when`(superHeroClientMock.getCharacter("70")).thenReturn(BuilderContextUtils.buildCharacterApi())
            instance.createDeck(CreateDeckRequest("deckNameTest", listOf("70")))

            val response = instance.getDecks()
            assertEquals(200, response.statusCodeValue)
            assertEquals(Deck(0L, "deckNameTest", listOf(BuilderContextUtils.buildBatmanII())), response.body!!.first())
        }

        @Test
        fun `Search all decks but there are none`() {
            val response = instance.getDecks()

            assertEquals(200, response.statusCodeValue)
            assertTrue(response.body!!.isEmpty())
        }
    }

    @Nested
    inner class GetDeckByIdOrName {

        @Test
        fun `Search by deck id`() {
            `when`(superHeroClientMock.getCharacter("70")).thenReturn(BuilderContextUtils.buildCharacterApi())
            instance.createDeck(CreateDeckRequest("deckNameTest", listOf("70")))

            val response = instance.getDeckByIdOrName("0", null)
            assertEquals(200, response.statusCodeValue)
            assertEquals(Deck(0L, "deckNameTest", listOf(BuilderContextUtils.buildBatmanII())), response.body!!.first())
        }

        @Test
        fun `Search by deck id but there are none`() {
            `when`(superHeroClientMock.getCharacter("70")).thenReturn(BuilderContextUtils.buildCharacterApi())
            instance.createDeck(CreateDeckRequest("deckNameTest", listOf("70")))

            val response = instance.getDeckByIdOrName("1", null)
            assertEquals(200, response.statusCodeValue)
            assertTrue(response.body!!.isEmpty())
        }

        @Test
        fun `Search by deck name`() {
            `when`(superHeroClientMock.getCharacter("70")).thenReturn(BuilderContextUtils.buildCharacterApi())
            instance.createDeck(CreateDeckRequest("deckNameTest", listOf("70")))

            val response = instance.getDeckByIdOrName(null, "deckNameTest")
            assertEquals(200, response.statusCodeValue)
            assertEquals(Deck(0L, "deckNameTest", listOf(BuilderContextUtils.buildBatmanII())), response.body!!.first())
        }

        @Test
        fun `Search by deck name but there are none`() {
            `when`(superHeroClientMock.getCharacter("70")).thenReturn(BuilderContextUtils.buildCharacterApi())
            instance.createDeck(CreateDeckRequest("deckNameTest", listOf("70")))

            val response = instance.getDeckByIdOrName(null, "deckNameTest2")
            assertEquals(200, response.statusCodeValue)
            assertTrue(response.body!!.isEmpty())
        }

        @Test
        fun `Search by deck name and id`() {
            `when`(superHeroClientMock.getCharacter("70")).thenReturn(BuilderContextUtils.buildCharacterApi())
            instance.createDeck(CreateDeckRequest("deckNameTest", listOf("70")))

            val response = instance.getDeckByIdOrName("0", "deckNameTest")
            assertEquals(200, response.statusCodeValue)
            assertEquals(Deck(0L, "deckNameTest", listOf(BuilderContextUtils.buildBatmanII())), response.body!!.first())
        }

        @Test
        fun `Search by deck name and id but deck name is different`() {
            `when`(superHeroClientMock.getCharacter("70")).thenReturn(BuilderContextUtils.buildCharacterApi())
            instance.createDeck(CreateDeckRequest("deckNameTest", listOf("70")))

            val response = instance.getDeckByIdOrName("0", "deckNameTest2")
            assertEquals(200, response.statusCodeValue)
            assertTrue(response.body!!.isEmpty())
        }

        @Test
        fun `Search by deck name and id but deck id is different`() {
            `when`(superHeroClientMock.getCharacter("70")).thenReturn(BuilderContextUtils.buildCharacterApi())
            instance.createDeck(CreateDeckRequest("deckNameTest", listOf("70")))

            val response = instance.getDeckByIdOrName("1", "deckNameTest")
            assertEquals(200, response.statusCodeValue)
            assertTrue(response.body!!.isEmpty())
        }

        @Test
        fun `Search by deck name and id but there are none`() {
            `when`(superHeroClientMock.getCharacter("70")).thenReturn(BuilderContextUtils.buildCharacterApi())
            instance.createDeck(CreateDeckRequest("deckNameTest", listOf("70")))

            val response = instance.getDeckByIdOrName("1", "deckNameTest2")
            assertEquals(200, response.statusCodeValue)
            assertTrue(response.body!!.isEmpty())
        }
    }

    @Nested
    inner class CreateDeck {

        @Test
        fun `Create deck`() {
            `when`(superHeroClientMock.getCharacter("70")).thenReturn(BuilderContextUtils.buildCharacterApi())

            val response = instance.createDeck(CreateDeckRequest("deckNameTest", listOf("70")))
            assertEquals(201, response.statusCodeValue)
            assertEquals(Deck(0L, "deckNameTest", listOf(BuilderContextUtils.buildBatmanII())), response.body!!)
        }

        @Test
        fun `Not create deck by invalid card id`() {
            `when`(superHeroClientMock.getCharacter("70")).thenThrow(ElementNotFoundException("character", "70"))

            val response = instance.createDeck(CreateDeckRequest("deckNameTest", listOf("70")))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not create deck by invalid powerstats card`() {
            `when`(superHeroClientMock.getCharacter("124")).thenReturn(BuilderContextUtils.buildCharacterApiWithInvalidPowerstats())

            val response = instance.createDeck(CreateDeckRequest("deckNameTest", listOf("124")))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }
    }

    @Nested
    inner class UpdateDeck {

        @Test
        fun `Update deck name`() {
            `when`(superHeroClientMock.getCharacter("70")).thenReturn(BuilderContextUtils.buildCharacterApi())
            instance.createDeck(CreateDeckRequest("deckNameTest", listOf("70")))

            val response = instance.updateDeck("0", UpdateDeckRequest("deckNameTest2", null))
            assertEquals(200, response.statusCodeValue)
            assertEquals(Deck(1L, "deckNameTest2", listOf(BuilderContextUtils.buildBatmanII())), response.body!!)
        }

        @Test
        fun `Update deck card`() {
            `when`(superHeroClientMock.getCharacter("70")).thenReturn(BuilderContextUtils.buildCharacterApi())
            `when`(superHeroClientMock.getCharacter("71")).thenReturn(BuilderContextUtils.buildCharacterApi())
            instance.createDeck(CreateDeckRequest("deckNameTest", listOf("70")))

            val response = instance.updateDeck("0", UpdateDeckRequest(null, listOf("71")))
            assertEquals(200, response.statusCodeValue)
            assertEquals(Deck(1L, "deckNameTest", listOf(BuilderContextUtils.buildBatmanII())), response.body!!)
        }

        @Test
        fun `Update deck card and deck name`() {
            `when`(superHeroClientMock.getCharacter("70")).thenReturn(BuilderContextUtils.buildCharacterApi())
            `when`(superHeroClientMock.getCharacter("71")).thenReturn(BuilderContextUtils.buildCharacterApi())
            instance.createDeck(CreateDeckRequest("deckNameTest", listOf("70")))

            val response = instance.updateDeck("0", UpdateDeckRequest("deckNameTest2", listOf("71")))
            assertEquals(200, response.statusCodeValue)
            assertEquals(Deck(1L, "deckNameTest2", listOf(BuilderContextUtils.buildBatmanII())), response.body!!)
        }

        @Test
        fun `Not update deck name by invalid deck id`() {
            `when`(superHeroClientMock.getCharacter("70")).thenReturn(BuilderContextUtils.buildCharacterApi())
            instance.createDeck(CreateDeckRequest("deckNameTest", listOf("70")))

            val response = instance.updateDeck("1", UpdateDeckRequest("deckNameTest2", null))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not update deck cards by invalid powerstats card`() {
            `when`(superHeroClientMock.getCharacter("70")).thenReturn(BuilderContextUtils.buildCharacterApi())
            `when`(superHeroClientMock.getCharacter("71")).thenReturn(BuilderContextUtils.buildCharacterApi())
            `when`(superHeroClientMock.getCharacter("124")).thenReturn(BuilderContextUtils.buildCharacterApiWithInvalidPowerstats())
            instance.createDeck(CreateDeckRequest("deckNameTest", listOf("70")))

            val response = instance.updateDeck("1", UpdateDeckRequest(null, listOf("124")))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not update deck cards by invalid deck id`() {
            `when`(superHeroClientMock.getCharacter("70")).thenReturn(BuilderContextUtils.buildCharacterApi())
            `when`(superHeroClientMock.getCharacter("71")).thenReturn(BuilderContextUtils.buildCharacterApi())
            instance.createDeck(CreateDeckRequest("deckNameTest", listOf("70")))

            val response = instance.updateDeck("1", UpdateDeckRequest(null, listOf("71")))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not update deck cards and deck name by invalid deck id`() {
            `when`(superHeroClientMock.getCharacter("70")).thenReturn(BuilderContextUtils.buildCharacterApi())
            `when`(superHeroClientMock.getCharacter("71")).thenReturn(BuilderContextUtils.buildCharacterApi())
            instance.createDeck(CreateDeckRequest("deckNameTest", listOf("70")))

            val response = instance.updateDeck("1", UpdateDeckRequest("deckNameTest2", listOf("71")))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not update deck cards by invalid character id`() {
            `when`(superHeroClientMock.getCharacter("70")).thenReturn(BuilderContextUtils.buildCharacterApi())
            `when`(superHeroClientMock.getCharacter("0")).thenThrow(ElementNotFoundException("character", "0"))
            instance.createDeck(CreateDeckRequest("deckNameTest", listOf("70")))

            val response = instance.updateDeck("1", UpdateDeckRequest(null, listOf("0")))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }
    }

    @Nested
    inner class DeleteDeck {

        @Test
        fun `Delete deck`() {
            instance.createDeck(CreateDeckRequest("deckNameTest", emptyList()))

            val response = instance.deleteDeck("0")
            assertEquals(204, response.statusCodeValue)
            assertNull(response.body)
            assertTrue(instance.getDecks().body!!.isEmpty())
        }

        @Test
        fun `Not delete deck by invalid deck id`() {
            val response = instance.deleteDeck("0")
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }
    }

}