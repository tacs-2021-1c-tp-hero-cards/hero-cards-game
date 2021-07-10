package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.*
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.CardMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.CharacterMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.ImageMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.PowerstatsMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateMatchRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.NextDuelRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.DeckService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.HashService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.MatchService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.NotificationClientService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.context.annotation.Bean
import org.springframework.http.ResponseEntity
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

internal class MatchesControllerTest {

    private lateinit var dao: Dao
    private lateinit var superHeroIntegrationMock: SuperHeroIntegration
    private lateinit var instance: MatchesController

    private val batman = BuilderContextUtils.buildBatman()
    private val batmanII = BuilderContextUtils.buildBatmanII()

    private val deck =
        Deck(id = 0L, version = 0L, name = "name", cards = listOf(batman, batmanII))

    private val deckHistory = DeckHistory(deck)

    private val user =
        Human(0L, "userName", "fullName", HashService.calculatePasswordHash("userName", "password"), "token")

    private val humanOpponent =
        Human(1L, "userName2", "fullName2", HashService.calculatePasswordHash("userName2", "password2"), "token2")

    private val iaOpponent = IA(2L, "userName3", difficulty = IADifficulty.HARD)

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
        context.register(CharacterMapper::class.java)
        context.register(PowerstatsMapper::class.java)
        context.register(ImageMapper::class.java)
        context.register(DeckService::class.java)
        context.register(DeckIntegration::class.java)
        context.register(UserIntegration::class.java)
        context.register(Dao::class.java)
        context.register(NotificationClientService::class.java)

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

    @Bean
    fun getSimpMessagingTemplate(): SimpMessagingTemplate{
        return mock(SimpMessagingTemplate::class.java)
    }

    @Nested
    inner class CreateMatch {

        @Test
        fun `Create match with 2 humans and 2 cards`() {
            dao.saveHuman(user)
            dao.saveHuman(humanOpponent)
            dao.saveDeck(deck)

            val response = instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")
            assertEquals(201, response.statusCodeValue)
            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(DeckHistory(deck), match.deck)
            assertEquals(MatchStatus.PENDING, match.status)

            val players = match.players
            assertEquals(2, players.size)
            assertTrue(players.any { it.user.userName == "userName" && it.user.id == 0L })
            assertTrue(players.any { it.user.userName == "userName2" && it.user.id == 1L })
            assertTrue(players.all { it.availableCards.isNotEmpty() })
            assertTrue(players.all { it.prizeCards.isEmpty() })
        }

        @Test
        fun `Create match with human and ia and 2 cards`() {
            dao.saveHuman(user)
            dao.saveIA(iaOpponent)
            dao.saveDeck(deck)

            val response = instance.createMatch(CreateMatchRequest("2", "IA", "0"), "token")
            assertEquals(201, response.statusCodeValue)
            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(DeckHistory(deck), match.deck)
            assertEquals(MatchStatus.PENDING, match.status)

            val players = match.players
            assertEquals(2, players.size)
            assertTrue(players.any { it.user.userName == "userName" && it.user.id == 0L })
            assertTrue(players.any { it.user.userName == "userName3" && it.user.id == 2L })
            assertTrue(players.all { it.availableCards.isNotEmpty() })
            assertTrue(players.all { it.prizeCards.isEmpty() })
        }

        @Test
        fun `Not create match by empty deck`() {
            val response = instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not create match by invalid deck id`() {
            dao.saveHuman(user)

            val response = instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not create match by empty users`() {
            dao.saveDeck(deck)

            val response = instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not create match by invalid user id`() {
            dao.saveHuman(user)
            dao.saveHuman(humanOpponent)
            dao.saveDeck(deck)

            val response = instance.createMatch(CreateMatchRequest("2", "IA", "0"), "token")
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }
    }

    @Nested
    inner class NextDuel {

        @Test
        fun `Play next duel with type COMBAT`() {
            dao.saveHuman(user)
            dao.saveHuman(humanOpponent)
            dao.saveDeck(deck)

            val matchResult = instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")

            val response = instance.nextDuel("0", NextDuelRequest(getUserTurn(matchResult).token!!, DuelType.COMBAT))
            assertEquals(200, response.statusCodeValue)

            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(DeckHistory(deck), match.deck)
            assertEquals(MatchStatus.FINALIZED, match.status)

            val players = match.players
            assertEquals(2, players.size)
            assertTrue(players.any { it.prizeCards.isEmpty() })
            assertTrue(players.any { it.prizeCards.isNotEmpty() })

            val duelHistory = match.duelHistoryList.first()
            assertEquals(0L, duelHistory.id)
            assertEquals(DuelType.COMBAT, duelHistory.duelType)
        }

        @Test
        fun `Play next duel when userType is IA`() {
            dao.saveHuman(user)
            dao.saveIA(iaOpponent)
            dao.saveDeck(deck)

            val matchResult = instance.createMatch(CreateMatchRequest("2", "IA", "0"), "token").body!!

            val iaPlayer = matchResult.players.first { it.user.userType == UserType.IA }

            val newPlayers = listOf(
                iaPlayer,
                matchResult.players.first { it.user.userType == UserType.HUMAN })

            dao.saveMatch(matchResult.copy(players = newPlayers))

            val response = instance.nextDuel("0", NextDuelRequest(null, null))
            assertEquals(200, response.statusCodeValue)

            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(DeckHistory(deck), match.deck)
            assertEquals(MatchStatus.FINALIZED, match.status)

            val players = match.players
            assertEquals(2, players.size)
            assertTrue(players.any { it.prizeCards.isEmpty() })
            assertTrue(players.any { it.prizeCards.isNotEmpty() })

            val duelHistory = match.duelHistoryList.first()
            assertEquals(0L, duelHistory.id)
            assertEquals(
                iaPlayer.availableCards.first().calculateDuelTypeAccordingDifficulty(iaOpponent.difficulty),
                duelHistory.duelType
            )
        }

        @Test
        fun `Not play next duel by empty match`() {
            val response = instance.nextDuel("0", NextDuelRequest("token", DuelType.COMBAT))
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not play next duel by invalid match id`() {
            dao.saveHuman(user)
            dao.saveHuman(humanOpponent)
            dao.saveDeck(deck)

            instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")

            val response = instance.nextDuel("1", NextDuelRequest("token", DuelType.COMBAT))
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not play next duel by match is CANCELLED`() {
            dao.saveHuman(user)
            dao.saveHuman(humanOpponent)
            dao.saveDeck(deck)

            instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")
            instance.abortMatch("0", hashMapOf("token" to "token"))

            val response = instance.nextDuel("0", NextDuelRequest("token", DuelType.COMBAT))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not play next duel by not is user turn`() {
            dao.saveHuman(user)
            dao.saveHuman(humanOpponent)
            dao.saveDeck(deck)

            val matchResult = instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")

            val response = instance.nextDuel("0", NextDuelRequest(getUserNotTurn(matchResult).token!!, DuelType.COMBAT))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

    }

    @Nested
    inner class GetMatch {

        @Test
        fun `Search match by valid match id`() {
            dao.saveHuman(user)
            dao.saveHuman(humanOpponent)
            dao.saveDeck(deck)

            instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")

            val response = instance.getMatch("0")
            assertEquals(200, response.statusCodeValue)
            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(deckHistory, match.deck)
            assertEquals(MatchStatus.PENDING, match.status)

            val players = match.players
            assertEquals(2, players.size)
            assertTrue(players.any { it.user.userName == "userName" && it.user.id == 0L })
            assertTrue(players.any { it.user.userName == "userName2" && it.user.id == 1L })
            assertTrue(players.all { it.availableCards.isNotEmpty() })
            assertTrue(players.all { it.prizeCards.isEmpty() })
        }

        @Test
        fun `Not search match by invalid match id`() {
            dao.saveHuman(user)
            dao.saveHuman(humanOpponent)
            dao.saveDeck(deck)

            instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")

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
            dao.saveHuman(user)
            dao.saveHuman(humanOpponent)
            dao.saveDeck(deck)

            val matchResponse = instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")

            val response = instance.abortMatch("0", hashMapOf("token" to getUserTurn(matchResponse).token!!))
            assertEquals(200, response.statusCodeValue)
            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(deckHistory, match.deck)
            assertEquals(MatchStatus.CANCELLED, match.status)

            val players = match.players
            assertEquals(2, players.size)

            val userTurn = getUserTurn(response)
            assertTrue(players.any { it.user.userName == userTurn.userName && it.user.id == userTurn.id && it.user.stats.loseCount == 1 })
            val userNotTurn = getUserNotTurn(response)
            assertTrue(players.any { it.user.userName == userNotTurn.userName && it.user.id == userNotTurn.id && it.user.stats.winCount == 1 })
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
            dao.saveHuman(user)
            dao.saveHuman(humanOpponent)
            dao.saveDeck(deck)

            instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")

            val response = instance.abortMatch("1", hashMapOf("token" to "token2"))
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not abort match by match is CANCELLED`() {
            dao.saveHuman(user)
            dao.saveHuman(humanOpponent)
            dao.saveDeck(deck)

            instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")
            instance.abortMatch("0", hashMapOf("token" to "token2"))

            val response = instance.abortMatch("0", hashMapOf("token" to "token2"))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not abort match by not is user turn`() {
            dao.saveHuman(user)
            dao.saveHuman(humanOpponent)
            dao.saveDeck(deck)

            val matchResponse = instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")

            val response =
                instance.abortMatch("0", hashMapOf("token" to getUserNotTurn(matchResponse).token!!))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

    }

    private fun getUserTurn(matchResponse: ResponseEntity<Match>) =
        matchResponse.body!!.players.first().user as Human

    private fun getUserNotTurn(matchResponse: ResponseEntity<Match>) =
        matchResponse.body!!.players.last().user as Human

}