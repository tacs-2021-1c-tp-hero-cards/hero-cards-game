package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.DeckIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.MatchIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.SuperHeroIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.SuperHeroClient
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.CardMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.ImageMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.PowerstatsMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateDeckRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateMatchRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.NextDuelRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.DeckService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.HashService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.MatchService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.UserService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.context.annotation.Bean
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

internal class MatchesControllerTest {

    lateinit var superHeroClientMock: SuperHeroClient
    lateinit var userIntegrationMock: UserIntegration
    lateinit var deckIntegrationMock: DeckIntegration
    lateinit var instance: MatchesController

    @BeforeEach
    fun init() {
        val context = AnnotationConfigWebApplicationContext()
        context.register(MatchesControllerTest::class.java)
        context.register(MatchesController::class.java)
        context.register(MatchService::class.java)
        context.register(MatchIntegration::class.java)

        context.register(DeckService::class.java)
        context.register(SuperHeroIntegration::class.java)
        context.register(CardMapper::class.java)
        context.register(PowerstatsMapper::class.java)
        context.register(ImageMapper::class.java)
        context.register(SuperHeroIntegration::class.java)
        context.register(UserService::class.java)

        context.refresh()
        context.start()

        superHeroClientMock = context.getBean(SuperHeroClient::class.java)
        userIntegrationMock = context.getBean(UserIntegration::class.java)
        deckIntegrationMock = context.getBean(DeckIntegration::class.java)
        instance = context.getBean(MatchesController::class.java)
    }

    @Bean
    fun getSuperHeroClientBean(): SuperHeroClient = mock(SuperHeroClient::class.java)

    @Bean
    fun getUserIntegrationBean(): UserIntegration = mock(UserIntegration::class.java)

    @Bean
    fun getDeckIntegrationBean(): DeckIntegration = mock(DeckIntegration::class.java)

    @Nested
    inner class CreateMatch {

        @Test
        fun `Create match with 2 users and 3 cards`() {
            `when`(userIntegrationMock.getAllUser()).thenReturn(listOf(buildUser(), buildOtherUser()))
            `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(buildDeck()))

            val response = instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))
            assertEquals(201, response.statusCodeValue)
            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(buildDeck(), match.deck)
            assertEquals(MatchStatus.IN_PROGRESS, match.status)

            val players = match.players
            assertEquals(2, players.size)
            assertTrue(players.any { it.userName == "userName" && it.id == 0L })
            assertTrue(players.any { it.userName == "userName2" && it.id == 1L })
            assertTrue(players.all { it.availableCards.isNotEmpty() })
            assertTrue(players.all { it.prizeCards.isEmpty() })
        }

        @Test
        fun `Not create match by empty deck`() {
            `when`(deckIntegrationMock.getAllDeck()).thenReturn(emptyList())

            val response = instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not create match by invalid deck id`() {
            `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(buildDeck()))

            val response = instance.createMatch(CreateMatchRequest(listOf("0", "1"), "1"))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not create match by empty users`() {
            `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(buildDeck()))
            `when`(userIntegrationMock.getAllUser()).thenReturn(emptyList())

            val response = instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not create match by invalid user id`() {
            `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(buildDeck()))
            `when`(userIntegrationMock.getAllUser()).thenReturn(listOf(buildUser(), buildOtherUser()))

            val response = instance.createMatch(CreateMatchRequest(listOf("0", "2"), "0"))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }
    }

    @Nested
    inner class NextDuel {

        @Test
        fun `Play next duel with type COMBAT`(){
            `when`(userIntegrationMock.getAllUser()).thenReturn(listOf(buildUser(), buildOtherUser()))
            `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(buildDeck()))
            instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))

            val response = instance.nextDuel("0", NextDuelRequest("token2", DuelType.COMBAT))
            assertEquals(200, response.statusCodeValue)

            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(buildDeck(), match.deck)
            assertEquals(MatchStatus.FINALIZED, match.status)

            val players = match.players
            assertEquals(2, players.size)
            assertTrue(players.any { it.userName == "userName" && it.id == 0L })
            assertTrue(players.any { it.userName == "userName2" && it.id == 1L })
            assertTrue(players.any { it.prizeCards.isEmpty() })
            assertTrue(players.any { it.prizeCards.isNotEmpty() })
        }

        @Test
        fun `Not play next duel by empty match`() {
            val response = instance.nextDuel("0", NextDuelRequest("token", DuelType.COMBAT))
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not play next duel by invalid match id`() {
            `when`(userIntegrationMock.getAllUser()).thenReturn(listOf(buildUser(), buildOtherUser()))
            `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(buildDeck()))
            instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))

            val response = instance.nextDuel("1", NextDuelRequest("token", DuelType.COMBAT))
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not play next duel by match is CANCELLED`() {
            `when`(userIntegrationMock.getAllUser()).thenReturn(listOf(buildUser(), buildOtherUser()))
            `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(buildDeck()))
            instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))
            instance.abortMatch("0", hashMapOf("token" to "token2"))

            val response = instance.nextDuel("0", NextDuelRequest("token", DuelType.COMBAT))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not play next duel by not is user turn`() {
            `when`(userIntegrationMock.getAllUser()).thenReturn(listOf(buildUser(), buildOtherUser()))
            `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(buildDeck()))
            instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))

            val response = instance.nextDuel("0", NextDuelRequest("token", DuelType.COMBAT))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

    }

    @Nested
    inner class GetMatch {

        @Test
        fun `Search match by valid match id`() {
            `when`(userIntegrationMock.getAllUser()).thenReturn(listOf(buildUser(), buildOtherUser()))
            `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(buildDeck()))
            instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))

            val response = instance.getMatch("0")
            assertEquals(200, response.statusCodeValue)
            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(buildDeck(), match.deck)
            assertEquals(MatchStatus.IN_PROGRESS, match.status)

            val players = match.players
            assertEquals(2, players.size)
            assertTrue(players.any { it.userName == "userName" && it.id == 0L })
            assertTrue(players.any { it.userName == "userName2" && it.id == 1L })
            assertTrue(players.all { it.availableCards.isNotEmpty() })
            assertTrue(players.all { it.prizeCards.isEmpty() })
        }

        @Test
        fun `Not search match by invalid match id`() {
            `when`(userIntegrationMock.getAllUser()).thenReturn(listOf(buildUser(), buildOtherUser()))
            `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(buildDeck()))
            instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))

            val response = instance.getMatch("1")

            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not search match by empty match list`() {
            val response = instance.getMatch("0")

            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }
    }

    @Nested
    inner class AbortMatch {

        @Test
        fun `Abort match by match id`() {
            `when`(userIntegrationMock.getAllUser()).thenReturn(listOf(buildUser(), buildOtherUser()))
            `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(buildDeck()))
            instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))

            val response = instance.abortMatch("0", hashMapOf("token" to "token2"))
            assertEquals(200, response.statusCodeValue)
            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(buildDeck(), match.deck)
            assertEquals(MatchStatus.CANCELLED, match.status)

            val players = match.players
            assertEquals(2, players.size)
            assertTrue(players.any { it.userName == "userName" && it.id == 0L })
            assertTrue(players.any { it.userName == "userName2" && it.id == 1L })
            assertTrue(players.all { it.availableCards.isNotEmpty() })
            assertTrue(players.all { it.prizeCards.isEmpty() })
        }

        @Test
        fun `Not abort match by empty match`() {
            val response = instance.abortMatch("0", hashMapOf("token" to "token2"))
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not abort match by invalid match id`() {
            `when`(userIntegrationMock.getAllUser()).thenReturn(listOf(buildUser(), buildOtherUser()))
            `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(buildDeck()))
            instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))

            val response = instance.abortMatch("1", hashMapOf("token" to "token2"))
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not abort match by match is CANCELLED`() {
            `when`(userIntegrationMock.getAllUser()).thenReturn(listOf(buildUser(), buildOtherUser()))
            `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(buildDeck()))
            instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))
            instance.abortMatch("0", hashMapOf("token" to "token2"))

            val response = instance.abortMatch("0", hashMapOf("token" to "token2"))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not abort match by not is user turn`() {
            `when`(userIntegrationMock.getAllUser()).thenReturn(listOf(buildUser(), buildOtherUser()))
            `when`(deckIntegrationMock.getAllDeck()).thenReturn(listOf(buildDeck()))
            instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))

            val response = instance.abortMatch("0", hashMapOf("token" to "token"))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

    }

    private fun buildDeck() =
        Deck(
            0L,
            "deckNameTest",
            listOf(
                BuilderContextUtils.buildBatman(),
                BuilderContextUtils.buildBatmanII(),
                BuilderContextUtils.buildBatmanIII()
            )
        )

    private fun buildUser() =
        User(0L, "userName", "fullName", HashService.calculatePasswordHash("userName", "password"), "token")

    private fun buildOtherUser() =
        User(1L, "userName2", "fullName2", HashService.calculatePasswordHash("userName2", "password2"), "token2")

}