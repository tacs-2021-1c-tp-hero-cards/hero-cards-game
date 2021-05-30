package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.*
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.CardMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.ImageMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.PowerstatsMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateMatchRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.NextDuelRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.DeckService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.HashService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.MatchService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.context.annotation.Bean
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

internal class MatchesControllerTest {

    private lateinit var dao: Dao
    private lateinit var superHeroIntegrationMock: SuperHeroIntegration
    private lateinit var instance: MatchesController

    private val batman = BuilderContextUtils.buildBatman()
    private val batmanII = BuilderContextUtils.buildBatmanII()

    private val deck =
        Deck(id = 0L, name = "name", cards = listOf(batman, batmanII))

    private val user =
        User(0L, "userName", "fullName", HashService.calculatePasswordHash("userName", "password"), "token")

    private val otherUser =
        User(1L, "userName2", "fullName2", HashService.calculatePasswordHash("userName2", "password2"), "token2")

    @BeforeEach
    fun init() {
        val context = AnnotationConfigWebApplicationContext()
        context.register(MatchesControllerTest::class.java)
        context.register(MatchesController::class.java)
        context.register(MatchService::class.java)
        context.register(MatchIntegration::class.java)
        context.register(PlayerIntegration::class.java)
        context.register(CardIntegration::class.java)
        context.register(CardMapper::class.java)
        context.register(PowerstatsMapper::class.java)
        context.register(ImageMapper::class.java)
        context.register(DeckService::class.java)
        context.register(DeckIntegration::class.java)
        context.register(UserIntegration::class.java)
        context.register(Dao::class.java)

        context.refresh()
        context.start()

        dao = context.getBean(Dao::class.java)
        superHeroIntegrationMock = context.getBean(SuperHeroIntegration::class.java)
        instance = context.getBean(MatchesController::class.java)
    }

    @Bean
    fun getSuperHeroIntegrationBean(): SuperHeroIntegration {
        val superHeroIntegrationMock = mock(SuperHeroIntegration::class.java)

        `when`(superHeroIntegrationMock.getCard("69")).thenReturn(batman)
        `when`(superHeroIntegrationMock.getCard("70")).thenReturn(batmanII)

        return superHeroIntegrationMock
    }

    @Nested
    inner class CreateMatch {

        @Test
        fun `Create match with 2 users and 2 cards`() {
            dao.saveUser(user)
            dao.saveUser(otherUser)
            dao.saveDeck(deck)

            val response = instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))
            assertEquals(201, response.statusCodeValue)
            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(deck, match.deck)
            assertEquals(MatchStatus.IN_PROGRESS, match.status)

            val players = match.players
            assertEquals(2, players.size)
            assertTrue(players.any { it.user.userName == "userName" && it.user.id == 0L })
            assertTrue(players.any { it.user.userName == "userName2" && it.user.id == 1L })
            assertTrue(players.all { it.availableCards.isNotEmpty() })
            assertTrue(players.all { it.prizeCards.isEmpty() })
        }

        @Test
        fun `Not create match by empty deck`() {
            val response = instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not create match by invalid deck id`() {
            dao.saveUser(user)

            val response = instance.createMatch(CreateMatchRequest(listOf("0", "1"), "1"))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not create match by empty users`() {
            dao.saveDeck(deck)

            val response = instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not create match by invalid user id`() {
            dao.saveUser(user)
            dao.saveUser(otherUser)
            dao.saveDeck(deck)

            val response = instance.createMatch(CreateMatchRequest(listOf("0", "2"), "0"))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }
    }

    @Nested
    inner class NextDuel {

        @Test
        fun `Play next duel with type COMBAT`() {
            dao.saveUser(user)
            dao.saveUser(otherUser)
            dao.saveDeck(deck)

            instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))

            val response = instance.nextDuel("0", NextDuelRequest("token", DuelType.COMBAT))
            assertEquals(200, response.statusCodeValue)

            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(deck, match.deck)
            assertEquals(MatchStatus.FINALIZED, match.status)

            val players = match.players
            assertEquals(2, players.size)
            assertTrue(players.any { it.user.userName == "userName" && it.user.id == 0L })
            assertTrue(players.any { it.user.userName == "userName2" && it.user.id == 1L })
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
            dao.saveUser(user)
            dao.saveUser(otherUser)
            dao.saveDeck(deck)

            instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))

            val response = instance.nextDuel("1", NextDuelRequest("token", DuelType.COMBAT))
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not play next duel by match is CANCELLED`() {
            dao.saveUser(user)
            dao.saveUser(otherUser)
            dao.saveDeck(deck)

            instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))
            instance.abortMatch("0", hashMapOf("token" to "token"))

            val response = instance.nextDuel("0", NextDuelRequest("token", DuelType.COMBAT))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not play next duel by not is user turn`() {
            dao.saveUser(user)
            dao.saveUser(otherUser)
            dao.saveDeck(deck)

            instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))

            val response = instance.nextDuel("0", NextDuelRequest("token2", DuelType.COMBAT))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

    }

    @Nested
    inner class GetMatch {

        @Test
        fun `Search match by valid match id`() {
            dao.saveUser(user)
            dao.saveUser(otherUser)
            dao.saveDeck(deck)

            instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))

            val response = instance.getMatch("0")
            assertEquals(200, response.statusCodeValue)
            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(deck, match.deck)
            assertEquals(MatchStatus.IN_PROGRESS, match.status)

            val players = match.players
            assertEquals(2, players.size)
            assertTrue(players.any { it.user.userName == "userName" && it.user.id == 0L })
            assertTrue(players.any { it.user.userName == "userName2" && it.user.id == 1L })
            assertTrue(players.all { it.availableCards.isNotEmpty() })
            assertTrue(players.all { it.prizeCards.isEmpty() })
        }

        @Test
        fun `Not search match by invalid match id`() {
            dao.saveUser(user)
            dao.saveUser(otherUser)
            dao.saveDeck(deck)

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
            dao.saveUser(user)
            dao.saveUser(otherUser)
            dao.saveDeck(deck)

            instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))

            val response = instance.abortMatch("0", hashMapOf("token" to "token"))
            assertEquals(200, response.statusCodeValue)
            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(deck, match.deck)
            assertEquals(MatchStatus.CANCELLED, match.status)

            val players = match.players
            assertEquals(2, players.size)
            assertTrue(players.any { it.user.userName == "userName" && it.user.id == 0L && it.user.stats.loseCount == 1 })
            assertTrue(players.any { it.user.userName == "userName2" && it.user.id == 1L && it.user.stats.winCount == 1 })
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
            dao.saveUser(user)
            dao.saveUser(otherUser)
            dao.saveDeck(deck)

            instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))

            val response = instance.abortMatch("1", hashMapOf("token" to "token2"))
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not abort match by match is CANCELLED`() {
            dao.saveUser(user)
            dao.saveUser(otherUser)
            dao.saveDeck(deck)

            instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))
            instance.abortMatch("0", hashMapOf("token" to "token2"))

            val response = instance.abortMatch("0", hashMapOf("token" to "token2"))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not abort match by not is user turn`() {
            dao.saveUser(user)
            dao.saveUser(otherUser)
            dao.saveDeck(deck)

            instance.createMatch(CreateMatchRequest(listOf("0", "1"), "0"))

            val response = instance.abortMatch("0", hashMapOf("token" to "token2"))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

    }

}