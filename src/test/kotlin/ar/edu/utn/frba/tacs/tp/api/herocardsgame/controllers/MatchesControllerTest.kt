package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.*
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.CardMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.CharacterMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.ImageMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.PowerstatsMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user.UserFactory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository.UserRepository
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateMatchRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.MatchConfirmationRequest
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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.context.annotation.Bean
import org.springframework.http.ResponseEntity
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

internal class MatchesControllerTest {

    private lateinit var dao: Dao
    private lateinit var userFactory: UserFactory
    private lateinit var userRepository: UserRepository
    private lateinit var superHeroIntegrationMock: SuperHeroIntegration
    private lateinit var instance: MatchesController

    private val batman = BuilderContextUtils.buildBatman()
    private val batmanII = BuilderContextUtils.buildBatmanII()

    private val deck =
        Deck(id = 0L, name = "name", cards = listOf(batman, batmanII))

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
        context.register(UserRepository::class.java)
        context.register(UserFactory::class.java)

        context.refresh()
        context.start()

        dao = context.getBean(Dao::class.java)
        userFactory = context.getBean(UserFactory::class.java)
        userRepository = context.getBean(UserRepository::class.java)
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
    fun getSimpMessagingTemplate(): SimpMessagingTemplate {
        return mock(SimpMessagingTemplate::class.java)
    }

    @Nested
    inner class CreateMatch {

        @Test
        fun `Create match with 2 humans and 2 cards`() {

            userRepository.save(userFactory.toEntity(user))
            userRepository.save(userFactory.toEntity(humanOpponent))
            //dao.saveDeck(deck)

            val response = instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")
            assertEquals(201, response.statusCodeValue)
            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(DeckHistory(deck), match.deck)
            assertEquals(MatchStatus.PENDING, match.status)
            validatePlayers(user, humanOpponent, match)
        }

        @Test
        fun `Create match with human and ia and 2 cards`() {
            userRepository.save(userFactory.toEntity(user))
            userRepository.save(userFactory.toEntity(iaOpponent))
            //dao.saveDeck(deck)

            val response = instance.createMatch(CreateMatchRequest("2", "IA", "0"), "token")
            assertEquals(201, response.statusCodeValue)
            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(DeckHistory(deck), match.deck)
            assertEquals(MatchStatus.IN_PROGRESS, match.status)
            validatePlayers(user, iaOpponent, match)
        }

        @Test
        fun `Not create match by empty deck`() {
            val response = instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not create match by invalid deck id`() {
            userRepository.save(userFactory.toEntity(user))

            val response = instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not create match by empty users`() {
            //dao.saveDeck(deck)

            val response = instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not create match by invalid user id`() {
            userRepository.save(userFactory.toEntity(user))
            userRepository.save(userFactory.toEntity(humanOpponent))
            //dao.saveDeck(deck)

            val response = instance.createMatch(CreateMatchRequest("2", "IA", "0"), "token")
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

    }

    @Nested
    inner class NextDuel {

        @Test
        fun `Play next duel with type COMBAT`() {
            userRepository.save(userFactory.toEntity(user))
            userRepository.save(userFactory.toEntity(humanOpponent))
            //dao.saveDeck(deck)

            val matchResult = instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")

            val response = instance.nextDuel("0", NextDuelRequest(DuelType.COMBAT), getUserTurn(matchResult).token!!)
            assertEquals(200, response.statusCodeValue)

            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(DeckHistory(deck), match.deck)
            assertEquals(MatchStatus.FINALIZED, match.status)

            val duelHistory = match.duelHistoryList.first()
            assertEquals(0L, duelHistory.id)
            assertEquals(DuelType.COMBAT, duelHistory.duelType)
        }

        @Test
        fun `Play next duel when userType is IA`() {
            userRepository.save(userFactory.toEntity(user))
            userRepository.save(userFactory.toEntity(iaOpponent))
            //dao.saveDeck(deck)

            var matchResult = instance.createMatch(CreateMatchRequest("2", "IA", "0"), "token").body!!

            if (matchResult.player.user.userType != UserType.IA) {
                matchResult = matchResult.copy(player = matchResult.opponent, opponent = matchResult.player)
                dao.saveMatch(matchResult)
            }

            val response = instance.nextDuel("0", NextDuelRequest(null), user.token!!)
            assertEquals(200, response.statusCodeValue)

            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(DeckHistory(deck), match.deck)
            assertEquals(MatchStatus.FINALIZED, match.status)

            val duelHistory = match.duelHistoryList.first()
            assertEquals(0L, duelHistory.id)
            assertEquals(
                matchResult.player.availableCards.first().calculateDuelTypeAccordingDifficulty(iaOpponent.difficulty),
                duelHistory.duelType
            )
        }

        @Test
        fun `Not play next duel by empty match`() {
            val response = instance.nextDuel("0", NextDuelRequest(DuelType.COMBAT), "token")
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not play next duel by invalid match id`() {
            userRepository.save(userFactory.toEntity(user))
            userRepository.save(userFactory.toEntity(humanOpponent))
            //dao.saveDeck(deck)

            instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")

            val response = instance.nextDuel("1", NextDuelRequest(DuelType.COMBAT), "token")
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not play next duel by match is CANCELLED`() {
            userRepository.save(userFactory.toEntity(user))
            userRepository.save(userFactory.toEntity(humanOpponent))
            //dao.saveDeck(deck)

            instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")
            instance.abortMatch("0", "token")

            val response = instance.nextDuel("0", NextDuelRequest(DuelType.COMBAT), "token")
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not play next duel by not is user turn`() {
            userRepository.save(userFactory.toEntity(user))
            userRepository.save(userFactory.toEntity(humanOpponent))
            //dao.saveDeck(deck)

            val matchResult = instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")

            val response = instance.nextDuel("0", NextDuelRequest(DuelType.COMBAT), getUserNotTurn(matchResult).token!!)
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

    }

    @Nested
    inner class GetMatch {

        @Test
        fun `Search match by valid match id`() {
            userRepository.save(userFactory.toEntity(user))
            userRepository.save(userFactory.toEntity(humanOpponent))
            //dao.saveDeck(deck)

            instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")

            val response = instance.getMatch("0")
            assertEquals(200, response.statusCodeValue)
            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(deckHistory, match.deck)
            assertEquals(MatchStatus.PENDING, match.status)
            validatePlayers(user, humanOpponent, match)
        }

        @Test
        fun `Not search match by invalid match id`() {
            userRepository.save(userFactory.toEntity(user))
            userRepository.save(userFactory.toEntity(humanOpponent))
            //dao.saveDeck(deck)

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
            userRepository.save(userFactory.toEntity(user))
            userRepository.save(userFactory.toEntity(humanOpponent))
            //dao.saveDeck(deck)

            val matchResponse = instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")

            val response = instance.abortMatch("0", getUserTurn(matchResponse).token!!)
            assertEquals(200, response.statusCodeValue)
            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(deckHistory, match.deck)
            assertEquals(MatchStatus.CANCELLED, match.status)
            assertTrue(match.player.user.stats.loseCount == 1)
            assertTrue(match.opponent.user.stats.winCount == 1)
        }

        @Test
        fun `Not abort match by empty match`() {
            val response = instance.abortMatch("0", "token2")
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not abort match by invalid match id`() {
            userRepository.save(userFactory.toEntity(user))
            userRepository.save(userFactory.toEntity(humanOpponent))
            //dao.saveDeck(deck)

            instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")

            val response = instance.abortMatch("1", "token2")
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not abort match by match is CANCELLED`() {
            userRepository.save(userFactory.toEntity(user))
            userRepository.save(userFactory.toEntity(humanOpponent))
            //dao.saveDeck(deck)

            instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")
            instance.abortMatch("0", "token2")

            val response = instance.abortMatch("0", "token2")
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not abort match by not is user turn`() {
            userRepository.save(userFactory.toEntity(user))
            userRepository.save(userFactory.toEntity(humanOpponent))
            //dao.saveDeck(deck)

            val matchResponse = instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")

            val response =
                instance.abortMatch("0", getUserNotTurn(matchResponse).token!!)
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

    }

    @Nested
    inner class MatchConfirmation {

        @Test
        fun `Confirm match when the match is pending`() {
            userRepository.save(userFactory.toEntity(user))
            userRepository.save(userFactory.toEntity(humanOpponent))
            //dao.saveDeck(deck)

            val matchResponse = instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")

            val response =
                instance.confirmMatch("0", MatchConfirmationRequest(true), getUserNotTurn(matchResponse).token!!)
            assertEquals(200, response.statusCodeValue)
            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(deckHistory, match.deck)
            assertEquals(MatchStatus.IN_PROGRESS, match.status)

            val playerStats = match.player.user.stats
            assertTrue(playerStats.winCount == 0 && playerStats.tieCount == 0 && playerStats.loseCount == 0 && playerStats.inProgressCount == 1)

            val opponentStats = match.player.user.stats
            assertTrue(opponentStats.winCount == 0 && opponentStats.tieCount == 0 && opponentStats.loseCount == 0 && opponentStats.inProgressCount == 1)
        }

        @Test
        fun `Reject match when the match is pending`() {
            userRepository.save(userFactory.toEntity(user))
            userRepository.save(userFactory.toEntity(humanOpponent))
            //dao.saveDeck(deck)

            instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")

            val response = instance.confirmMatch("0", MatchConfirmationRequest(false), user.token!!)
            assertEquals(200, response.statusCodeValue)
            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(deckHistory, match.deck)
            assertEquals(MatchStatus.CANCELLED, match.status)

            val playerStats = match.player.user.stats
            assertTrue(playerStats.winCount == 0 && playerStats.tieCount == 0 && playerStats.loseCount == 0 && playerStats.inProgressCount == 0)

            val opponentStats = match.player.user.stats
            assertTrue(opponentStats.winCount == 0 && opponentStats.tieCount == 0 && opponentStats.loseCount == 0 && opponentStats.inProgressCount == 0)
        }

        @Test
        fun `Confirm match when the match is in progress`() {
            userRepository.save(userFactory.toEntity(user))
            userRepository.save(userFactory.toEntity(humanOpponent))
            //dao.saveDeck(deck)

            val matchResponse = instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")
            instance.confirmMatch("0", MatchConfirmationRequest(true), getUserTurn(matchResponse).token!!)

            val response =
                instance.confirmMatch("0", MatchConfirmationRequest(false), getUserNotTurn(matchResponse).token!!)
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Confirm match when the match not found`() {
            val response = instance.confirmMatch("0", MatchConfirmationRequest(false), "token")
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

    }

    private fun getUserTurn(matchResponse: ResponseEntity<Match>) =
        matchResponse.body!!.player.user as Human

    private fun getUserNotTurn(matchResponse: ResponseEntity<Match>) =
        matchResponse.body!!.opponent.user as Human

    private fun validatePlayers(user: User, user2: User, match: Match) {
        val player = match.player
        val opponent = match.opponent

        if (user.id == player.user.id) {
            assertEquals(user, player.user)
            assertEquals(user2, opponent.user)
        } else {
            assertEquals(user2, player.user)
            assertEquals(user, opponent.user)
        }

        assertTrue(player.availableCards.isNotEmpty() && player.prizeCards.isEmpty())
        assertTrue(opponent.availableCards.isNotEmpty() && opponent.prizeCards.isEmpty())
    }
}