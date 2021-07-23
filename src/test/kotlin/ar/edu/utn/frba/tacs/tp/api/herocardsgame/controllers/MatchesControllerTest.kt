package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.*
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.CardMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.CharacterMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.ImageMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.PowerstatsMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.DuelHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.PlayerHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck.DeckEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match.MatchFactory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user.UserFactory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository.DeckRepository
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository.MatchRepository
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository.UserRepository
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateMatchRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.MatchConfirmationRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.NextDuelRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.DeckService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.HashService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.MatchService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.NotificationClientService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
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
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

internal class MatchesControllerTest {

    private lateinit var userRepositoryMock: UserRepository
    private lateinit var matchRepositoryMock: MatchRepository
    private lateinit var deckRepositoryMock: DeckRepository
    private lateinit var userFactory: UserFactory
    private lateinit var matchFactory: MatchFactory
    private lateinit var superHeroIntegrationMock: SuperHeroIntegration
    private lateinit var instance: MatchesController

    private val batman = BuilderContextUtils.buildBatman().copy()
    private val batmanII = BuilderContextUtils.buildBatmanII().copy()

    private val deck =
        Deck(id = 0L, name = "name", cards = listOf(batman, batman))

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
        context.register(UserFactory::class.java)
        context.register(MatchFactory::class.java)

        context.refresh()
        context.start()

        userRepositoryMock = context.getBean(UserRepository::class.java)
        matchRepositoryMock = context.getBean(MatchRepository::class.java)
        deckRepositoryMock = context.getBean(DeckRepository::class.java)
        userFactory = context.getBean(UserFactory::class.java)
        matchFactory = context.getBean(MatchFactory::class.java)
        superHeroIntegrationMock = context.getBean(SuperHeroIntegration::class.java)
        instance = context.getBean(MatchesController::class.java)
    }

    @Bean
    fun getUserRepository(): UserRepository = mock(UserRepository::class.java)

    @Bean
    fun getMatchRepository(): MatchRepository = mock(MatchRepository::class.java)

    @Bean
    fun getDeckRepository(): DeckRepository = mock(DeckRepository::class.java)

    @Bean
    fun getSuperHeroIntegrationBean(): SuperHeroIntegration {
        val superHeroIntegrationMock = mock(SuperHeroIntegration::class.java)

        `when`(superHeroIntegrationMock.getCard("69")).thenReturn(batman)
        `when`(superHeroIntegrationMock.getCard("70")).thenReturn(batmanII)

        return superHeroIntegrationMock
    }

    @Bean
    fun getSimpMessagingTemplate(): SimpMessagingTemplate = mock(SimpMessagingTemplate::class.java)


    @Nested
    inner class CreateMatch {

        @Test
        fun `Create match with 2 humans and 2 cards`() {
            val matchEntity = matchFactory.toEntity(
                Match(
                    player = Player(user, true).copy(availableCards = listOf(batman)),
                    opponent = Player(humanOpponent).copy(availableCards = listOf(batman)),
                    deck = deckHistory,
                    status = MatchStatus.PENDING,
                    duelHistoryList = emptyList()
                )
            )

            `when`(deckRepositoryMock.findDeckByIdAndName("0")).thenReturn(listOf(DeckEntity(deck)))
            `when`(userRepositoryMock.findHumanByIdAndUserNameAndFullNameAndToken(token = "token"))
                .thenReturn(listOf(userFactory.toEntity(user)))
            `when`(userRepositoryMock.getById(1L)).thenReturn(userFactory.toEntity(humanOpponent))
            `when`(matchRepositoryMock.save(matchEntity)).thenReturn(matchEntity.copy(id = 0L))

            `when`(
                matchRepositoryMock.save(
                    matchEntity.copy(
                        player = matchEntity.player.reversed(),
                        playerIdTurn = matchEntity.player.reversed().first().id!!
                    )
                )
            )
                .thenReturn(
                    matchEntity.copy(
                        player = matchEntity.player.reversed(),
                        id = 0L,
                        playerIdTurn = matchEntity.player.reversed().first().id!!
                    )
                )

            `when`(userRepositoryMock.findHumanByIdAndUserNameAndFullNameAndToken(id = "1"))
                .thenReturn(listOf(userFactory.toEntity(humanOpponent)))

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
            val matchEntity = matchFactory.toEntity(
                Match(
                    player = Player(user, true).startMatch().copy(availableCards = listOf(batman)),
                    opponent = Player(iaOpponent).startMatch().copy(availableCards = listOf(batman)),
                    deck = deckHistory,
                    status = MatchStatus.IN_PROGRESS,
                    duelHistoryList = emptyList()
                )
            )

            `when`(deckRepositoryMock.findDeckByIdAndName("0")).thenReturn(listOf(DeckEntity(deck)))
            `when`(userRepositoryMock.findHumanByIdAndUserNameAndFullNameAndToken(token = "token"))
                .thenReturn(listOf(userFactory.toEntity(user)))
            `when`(userRepositoryMock.getById(2L)).thenReturn(userFactory.toEntity(iaOpponent))

            `when`(matchRepositoryMock.save(matchEntity)).thenReturn(matchEntity.copy(id = 0L))

            `when`(
                matchRepositoryMock.save(
                    matchEntity.copy(
                        player = matchEntity.player.reversed(),
                        playerIdTurn = matchEntity.player.reversed().first().id!!
                    )
                )
            )
                .thenReturn(
                    matchEntity.copy(
                        player = matchEntity.player.reversed(),
                        id = 0L,
                        playerIdTurn = matchEntity.player.reversed().first().id!!
                    )
                )

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
            `when`(deckRepositoryMock.findDeckByIdAndName("0")).thenReturn(emptyList())

            val response = instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not create match by empty users`() {
            `when`(deckRepositoryMock.findDeckByIdAndName("0")).thenReturn(listOf(DeckEntity(deck)))
            `when`(userRepositoryMock.findHumanByIdAndUserNameAndFullNameAndToken(token = "token"))
                .thenReturn(listOf(userFactory.toEntity(user)))
            `when`(userRepositoryMock.getById(1L)).thenReturn(null)

            val response = instance.createMatch(CreateMatchRequest("1", "HUMAN", "0"), "token")
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

    }

    @Nested
    inner class NextDuel {

        @Test
        fun `Play next duel with type COMBAT`() {
            val matchEntity = matchFactory.toEntity(
                Match(
                    id = 0,
                    player = Player(user).startMatch().copy(availableCards = listOf(batman)),
                    opponent = Player(humanOpponent, true).startMatch().copy(availableCards = listOf(batman)),
                    deck = deckHistory,
                    status = MatchStatus.IN_PROGRESS,
                    duelHistoryList = emptyList()
                )
            )

            val newMatchEntity = matchFactory.toEntity(
                Match(
                    id = 0,
                    player = Player(humanOpponent, true).startMatch().tieMatch()
                        .copy(availableCards = emptyList(), prizeCards = listOf(batman)),
                    opponent = Player(user).startMatch().tieMatch()
                        .copy(availableCards = emptyList(), prizeCards = listOf(batman)),
                    deck = deckHistory,
                    status = MatchStatus.FINALIZED,
                    duelHistoryList = listOf(
                        DuelHistory(
                            null,
                            PlayerHistory(Player(user).copy(availableCards = listOf(batman))),
                            PlayerHistory(Player(humanOpponent).copy(availableCards = listOf(batman))),
                            DuelType.COMBAT,
                            DuelResult.TIE
                        )
                    )
                )
            )

            `when`(matchRepositoryMock.getById(0L)).thenReturn(matchEntity)
            `when`(userRepositoryMock.findHumanByIdAndUserNameAndFullNameAndToken("0"))
                .thenReturn(listOf(userFactory.toEntity(user)))
            `when`(matchRepositoryMock.save(newMatchEntity)).thenReturn(newMatchEntity)

            val response = instance.nextDuel("0", NextDuelRequest(DuelType.COMBAT), "token")
            assertEquals(200, response.statusCodeValue)

            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(DeckHistory(deck), match.deck)
            assertEquals(MatchStatus.FINALIZED, match.status)

            val duelHistory = match.duelHistoryList.first()
            assertEquals(DuelType.COMBAT, duelHistory.duelType)
        }

        @Test
        fun `Play next duel when userType is IA`() {
            val matchEntity = matchFactory.toEntity(
                Match(
                    id = 0,
                    player = Player(iaOpponent).startMatch().copy(availableCards = listOf(batman)),
                    opponent = Player(user, true).startMatch().copy(availableCards = listOf(batman)),
                    deck = deckHistory,
                    status = MatchStatus.IN_PROGRESS,
                    duelHistoryList = emptyList()
                )
            )

            val newMatchEntity = matchFactory.toEntity(
                Match(
                    id = 0,
                    opponent = Player(iaOpponent).startMatch().tieMatch()
                        .copy(availableCards = emptyList(), prizeCards = listOf(batman)),
                    player = Player(user, true).startMatch().tieMatch()
                        .copy(availableCards = emptyList(), prizeCards = listOf(batman)),
                    deck = deckHistory,
                    status = MatchStatus.FINALIZED,
                    duelHistoryList = listOf(
                        DuelHistory(
                            null,
                            PlayerHistory(Player(iaOpponent).copy(availableCards = listOf(batman))),
                            PlayerHistory(Player(user).copy(availableCards = listOf(batman))),
                            DuelType.HEIGHT,
                            DuelResult.TIE
                        )
                    )
                )
            )

            `when`(matchRepositoryMock.getById(0L)).thenReturn(matchEntity)
            `when`(matchRepositoryMock.save(newMatchEntity)).thenReturn(newMatchEntity)

            val response = instance.nextDuel("0", NextDuelRequest(null), "token")
            assertEquals(200, response.statusCodeValue)

            val match = response.body!!
            assertEquals(0L, match.id)
            assertEquals(DeckHistory(deck), match.deck)
            assertEquals(MatchStatus.FINALIZED, match.status)

            val duelHistory = match.duelHistoryList.first()
            assertEquals(DuelType.HEIGHT, duelHistory.duelType)
        }

        @Test
        fun `Not play next duel by empty match`() {
            `when`(matchRepositoryMock.getById(0L)).thenReturn(null)

            val response = instance.nextDuel("0", NextDuelRequest(DuelType.COMBAT), "token")
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not play next duel by match is CANCELLED`() {
            val matchEntity = matchFactory.toEntity(
                Match(
                    id = 0,
                    player = Player(user).startMatch().copy(availableCards = listOf(batman)),
                    opponent = Player(humanOpponent).startMatch().copy(availableCards = listOf(batman)),
                    deck = deckHistory,
                    status = MatchStatus.CANCELLED,
                    duelHistoryList = emptyList()
                )
            )

            `when`(matchRepositoryMock.getById(0L)).thenReturn(matchEntity)
            `when`(userRepositoryMock.findHumanByIdAndUserNameAndFullNameAndToken("0"))
                .thenReturn(listOf(userFactory.toEntity(user)))

            val response = instance.nextDuel("0", NextDuelRequest(DuelType.COMBAT), "token")
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not play next duel by not is user turn`() {
            val matchEntity = matchFactory.toEntity(
                Match(
                    id = 0,
                    player = Player(user).startMatch().copy(availableCards = listOf(batman)),
                    opponent = Player(humanOpponent).startMatch().copy(availableCards = listOf(batman)),
                    deck = deckHistory,
                    status = MatchStatus.IN_PROGRESS,
                    duelHistoryList = emptyList()
                )
            )

            `when`(matchRepositoryMock.getById(0L)).thenReturn(matchEntity)
            `when`(userRepositoryMock.findHumanByIdAndUserNameAndFullNameAndToken("0"))
                .thenReturn(listOf(userFactory.toEntity(user)))

            val response = instance.nextDuel("0", NextDuelRequest(DuelType.COMBAT), "token2")
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

    }

    @Nested
    inner class GetMatch {

        @Test
        fun `Search match by valid match id`() {
            val matchEntity = matchFactory.toEntity(
                Match(
                    id = 0L,
                    player = Player(user).copy(availableCards = listOf(batman)),
                    opponent = Player(humanOpponent).copy(availableCards = listOf(batman)),
                    deck = deckHistory,
                    status = MatchStatus.PENDING,
                    duelHistoryList = emptyList()
                )
            )

            `when`(matchRepositoryMock.getById(0L)).thenReturn(matchEntity)

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
            `when`(matchRepositoryMock.getById(0L)).thenReturn(null)

            val response = instance.getMatch("0")

            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

    }

    @Nested
    inner class AbortMatch {

        @Test
        fun `Abort match by match id`() {
            val matchEntity = matchFactory.toEntity(
                Match(
                    id = 0L,
                    player = Player(user).copy(availableCards = listOf(batman)),
                    opponent = Player(humanOpponent).copy(availableCards = listOf(batman)),
                    deck = deckHistory,
                    status = MatchStatus.PENDING,
                    duelHistoryList = emptyList()
                )
            )

            val newMatchEntity = matchFactory.toEntity(
                Match(
                    id = 0L,
                    player = Player(user).copy(availableCards = listOf(batman)).loseMatch(),
                    opponent = Player(humanOpponent).copy(availableCards = listOf(batman)).winMatch(),
                    deck = deckHistory,
                    status = MatchStatus.CANCELLED,
                    duelHistoryList = emptyList()
                )
            )

            `when`(matchRepositoryMock.getById(0L)).thenReturn(matchEntity)
            `when`(userRepositoryMock.findHumanByIdAndUserNameAndFullNameAndToken("0"))
                .thenReturn(listOf(userFactory.toEntity(user)))
            `when`(matchRepositoryMock.save(newMatchEntity)).thenReturn(newMatchEntity)

            val response = instance.abortMatch("0", "token")
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
            `when`(matchRepositoryMock.getById(0L)).thenReturn(null)

            val response = instance.abortMatch("0", "token2")
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not abort match by match is CANCELLED`() {
            val matchEntity = matchFactory.toEntity(
                Match(
                    id = 0L,
                    player = Player(user).copy(availableCards = listOf(batman)),
                    opponent = Player(humanOpponent).copy(availableCards = listOf(batman)),
                    deck = deckHistory,
                    status = MatchStatus.CANCELLED,
                    duelHistoryList = emptyList()
                )
            )

            `when`(matchRepositoryMock.getById(0L)).thenReturn(matchEntity)
            `when`(userRepositoryMock.findHumanByIdAndUserNameAndFullNameAndToken("0"))
                .thenReturn(listOf(userFactory.toEntity(user)))

            instance.abortMatch("0", "token")

            val response = instance.abortMatch("0", "token2")
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not abort match by not is user turn`() {
            val matchEntity = matchFactory.toEntity(
                Match(
                    id = 0L,
                    player = Player(user).copy(availableCards = listOf(batman)),
                    opponent = Player(humanOpponent).copy(availableCards = listOf(batman)),
                    deck = deckHistory,
                    status = MatchStatus.IN_PROGRESS,
                    duelHistoryList = emptyList()
                )
            )

            `when`(matchRepositoryMock.getById(0L)).thenReturn(matchEntity)
            `when`(userRepositoryMock.findHumanByIdAndUserNameAndFullNameAndToken("0"))
                .thenReturn(listOf(userFactory.toEntity(user)))

            val response = instance.abortMatch("0", "token2")
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

    }

    @Nested
    inner class MatchConfirmation {

        @Test
        fun `Confirm match when the match is pending`() {
            val matchEntity = matchFactory.toEntity(
                Match(
                    id = 0L,
                    player = Player(user).copy(availableCards = listOf(batman)),
                    opponent = Player(humanOpponent).copy(availableCards = listOf(batman)),
                    deck = deckHistory,
                    status = MatchStatus.PENDING,
                    duelHistoryList = emptyList()
                )
            )

            val newMatchEntity = matchFactory.toEntity(
                Match(
                    id = 0L,
                    player = Player(user).copy(availableCards = listOf(batman)).startMatch(),
                    opponent = Player(humanOpponent).copy(availableCards = listOf(batman)).startMatch(),
                    deck = deckHistory,
                    status = MatchStatus.IN_PROGRESS,
                    duelHistoryList = emptyList()
                )
            )

            `when`(matchRepositoryMock.getById(0L)).thenReturn(matchEntity)
            `when`(matchRepositoryMock.save(newMatchEntity)).thenReturn(newMatchEntity)
            `when`(userRepositoryMock.findHumanByIdAndUserNameAndFullNameAndToken("0"))
                .thenReturn(listOf(userFactory.toEntity(user)))
            `when`(userRepositoryMock.findHumanByIdAndUserNameAndFullNameAndToken("1"))
                .thenReturn(listOf(userFactory.toEntity(humanOpponent)))
            `when`(userRepositoryMock.findHumanByIdAndUserNameAndFullNameAndToken(token = "token"))
                .thenReturn(listOf(userFactory.toEntity(user)))

            val response =
                instance.confirmMatch("0", MatchConfirmationRequest(true), "token")
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
            val matchEntity = matchFactory.toEntity(
                Match(
                    id = 0L,
                    player = Player(user).copy(availableCards = listOf(batman)),
                    opponent = Player(humanOpponent).copy(availableCards = listOf(batman)),
                    deck = deckHistory,
                    status = MatchStatus.PENDING,
                    duelHistoryList = emptyList()
                )
            )

            val newMatchEntity = matchFactory.toEntity(
                Match(
                    id = 0L,
                    player = Player(user).copy(availableCards = listOf(batman)),
                    opponent = Player(humanOpponent).copy(availableCards = listOf(batman)),
                    deck = deckHistory,
                    status = MatchStatus.CANCELLED,
                    duelHistoryList = emptyList()
                )
            )

            `when`(matchRepositoryMock.getById(0L)).thenReturn(matchEntity)
            `when`(matchRepositoryMock.save(newMatchEntity)).thenReturn(newMatchEntity)
            `when`(userRepositoryMock.findHumanByIdAndUserNameAndFullNameAndToken("0"))
                .thenReturn(listOf(userFactory.toEntity(user)))
            `when`(userRepositoryMock.findHumanByIdAndUserNameAndFullNameAndToken("1"))
                .thenReturn(listOf(userFactory.toEntity(humanOpponent)))
            `when`(userRepositoryMock.findHumanByIdAndUserNameAndFullNameAndToken(token = "token"))
                .thenReturn(listOf(userFactory.toEntity(user)))

            val response = instance.confirmMatch("0", MatchConfirmationRequest(false), "token")
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
            val matchEntity = matchFactory.toEntity(
                Match(
                    id = 0L,
                    player = Player(user).copy(availableCards = listOf(batman)),
                    opponent = Player(humanOpponent).copy(availableCards = listOf(batman)),
                    deck = deckHistory,
                    status = MatchStatus.IN_PROGRESS,
                    duelHistoryList = emptyList()
                )
            )

            `when`(matchRepositoryMock.getById(0L)).thenReturn(matchEntity)

            val response =
                instance.confirmMatch("0", MatchConfirmationRequest(false), "token")
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Confirm match when the match not found`() {
            `when`(matchRepositoryMock.getById(0L)).thenReturn(null)

            val response = instance.confirmMatch("0", MatchConfirmationRequest(false), "token")
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

    }

    @Nested
    inner class GetMatchByUserId {

        @Test
        fun `Search match by user id only those created by the user`() {
            val matchEntity = matchFactory.toEntity(
                Match(
                    id = 0L,
                    player = Player(user).copy(availableCards = listOf(batman)),
                    opponent = Player(humanOpponent).copy(availableCards = listOf(batman)),
                    deck = deckHistory,
                    status = MatchStatus.IN_PROGRESS,
                    duelHistoryList = emptyList()
                )
            )

            `when`(matchRepositoryMock.findMatchByCreatedUserId(0L)).thenReturn(listOf(matchEntity))
            val response = instance.getMatchByUserId("0", true)
            assertEquals(200, response.statusCodeValue)

            val founds = response.body!!
            assertEquals(1, founds.size)

            val first = founds.first()
            assertEquals(0L, first.matchId)
            assertEquals(MatchStatus.IN_PROGRESS, first.matchStatus)
            assertEquals(humanOpponent, first.userOpponent)
            assertFalse(first.isMatchCreatedByUser)
        }

        @Test
        fun `Search match by user id only those created by the user but there no`() {
            `when`(matchRepositoryMock.findMatchByCreatedUserId(0L)).thenReturn(emptyList())

            val response = instance.getMatchByUserId("0", true)
            assertEquals(200, response.statusCodeValue)
            val matchUserResponse = response.body!!
            assertTrue(matchUserResponse.isEmpty())
        }

        @Test
        fun `Search match by user id no matter who created them`() {
            val matchEntity = matchFactory.toEntity(
                Match(
                    id = 0L,
                    player = Player(user).copy(availableCards = listOf(batman)),
                    opponent = Player(humanOpponent).copy(availableCards = listOf(batman)),
                    deck = deckHistory,
                    status = MatchStatus.IN_PROGRESS,
                    duelHistoryList = emptyList()
                )
            )

            `when`(matchRepositoryMock.findMatchByUserId(0L)).thenReturn(listOf(matchEntity))

            val response = instance.getMatchByUserId("0", false)
            assertEquals(200, response.statusCodeValue)
            val founds = response.body!!
            assertEquals(1, founds.size)

            val first = founds.first()
            assertEquals(0L, first.matchId)
            assertEquals(MatchStatus.IN_PROGRESS, first.matchStatus)
            assertEquals(humanOpponent, first.userOpponent)
            assertFalse(first.isMatchCreatedByUser)
        }

        @Test
        fun `Search match by user id Search match but there no`() {
            `when`(matchRepositoryMock.findMatchByUserId(0L)).thenReturn(emptyList())

            val response = instance.getMatchByUserId("0", false)
            assertEquals(200, response.statusCodeValue)
            val matchUserResponse = response.body!!
            assertTrue(matchUserResponse.isEmpty())
        }


    }

    private fun validatePlayers(user: User, user2: User, match: Match) {
        val player = match.player
        val opponent = match.opponent

        val isConfirmAutomatic = match.status == MatchStatus.IN_PROGRESS

        if (user.id == player.user.id) {
            validateUser(user, player.user, isConfirmAutomatic)
            validateUser(user2, opponent.user, isConfirmAutomatic)
        } else {
            validateUser(user2, player.user, isConfirmAutomatic)
            validateUser(user, opponent.user, isConfirmAutomatic)
        }

        assertTrue(player.availableCards.isNotEmpty() && player.prizeCards.isEmpty())
        assertTrue(opponent.availableCards.isNotEmpty() && opponent.prizeCards.isEmpty())
    }

    private fun validateUser(user: User, playerUser: User, isConfirmAutomatic: Boolean) =
        assertEquals(if (isConfirmAutomatic) user.startMatch() else user, playerUser)

}