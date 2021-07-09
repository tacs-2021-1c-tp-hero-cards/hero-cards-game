package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.DuelHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.PlayerHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class MatchIntegrationTest {

    private lateinit var dao: Dao
    private lateinit var instance: MatchIntegration

    private val deckIntegrationMock: DeckIntegration = mock(DeckIntegration::class.java)
    private val playerIntegrationMock: PlayerIntegration = mock(PlayerIntegration::class.java)

    private val batman = BuilderContextUtils.buildBatman()
    private val batmanII = BuilderContextUtils.buildBatmanII()

    private val userPlayer = Human(0L, "userName", "fullName", "password")
    private val player = Player(0L, userPlayer, listOf(batman))
    private val playerHistory = PlayerHistory(0L, 0L, batman)

    private val humanUserOpponent = Human(1L, "userNameOpponent", "fullName", "password")
    private val humanOpponent = Player(1L, humanUserOpponent, listOf(batmanII))
    private val humanOpponentHistory = PlayerHistory(1L, 1L, batmanII)

    private val iaUserOpponent = IA(2L, "userNameOpponent", difficulty = IADifficulty.HARD)
    private val iaOpponent = Player(2L, iaUserOpponent, listOf(batmanII))
    private val iaOpponentHistory = PlayerHistory(2L, 2L, batmanII)

    private val deck = Deck(0L, 1L, "deckNameTest", listOf(batman, batmanII))
    private val deckHistory = DeckHistory(deck.copy(version = 0L))
    private val duelHistoryWithHuman =
        DuelHistory(0L, playerHistory, humanOpponentHistory, DuelType.SPEED, DuelResult.WIN)
    private val duelHistoryWithIA =
        DuelHistory(0L, playerHistory, iaOpponentHistory, DuelType.SPEED, DuelResult.WIN)

    private val matchWithHuman =
        Match(0L, listOf(player, humanOpponent), deckHistory, MatchStatus.IN_PROGRESS, listOf(duelHistoryWithHuman))
    private val matchWithIA =
        Match(1L, listOf(player, iaOpponent), deckHistory, MatchStatus.IN_PROGRESS, listOf(duelHistoryWithIA))

    @BeforeEach
    fun init() {
        dao = Dao()
        instance = MatchIntegration(dao, deckIntegrationMock, playerIntegrationMock)
    }

    @Nested
    inner class GetMatchById {

        @Test
        fun `Get match with human by id`() {
            dao.saveMatch(matchWithHuman)
            dao.saveDuelHistory(duelHistoryWithHuman)

            `when`(playerIntegrationMock.getPlayerById(0L)).thenReturn(player)
            `when`(playerIntegrationMock.getPlayerById(1L)).thenReturn(humanOpponent)
            `when`(deckIntegrationMock.getDeckById(0L)).thenReturn(deck.copy(deckHistoryList = listOf(deckHistory)))
            `when`(playerIntegrationMock.getPlayerHistoryByVersion(0L)).thenReturn(playerHistory)
            `when`(playerIntegrationMock.getPlayerHistoryByVersion(1L)).thenReturn(humanOpponentHistory)

            val found = instance.getMatchById(0L)

            assertEquals(matchWithHuman, found)
        }

        @Test
        fun `Get match with ia by id`() {
            dao.saveMatch(matchWithIA)
            dao.saveDuelHistory(duelHistoryWithIA)

            `when`(playerIntegrationMock.getPlayerById(0L)).thenReturn(player)
            `when`(playerIntegrationMock.getPlayerById(2L)).thenReturn(iaOpponent)
            `when`(deckIntegrationMock.getDeckById(0L)).thenReturn(deck.copy(deckHistoryList = listOf(deckHistory)))
            `when`(playerIntegrationMock.getPlayerHistoryByVersion(0L)).thenReturn(playerHistory)
            `when`(playerIntegrationMock.getPlayerHistoryByVersion(2L)).thenReturn(iaOpponentHistory)

            val found = instance.getMatchById(1L)

            assertEquals(matchWithIA, found)
        }

        @Test
        fun `Get match with human by id but not exist`() {
            dao.saveMatch(matchWithHuman.copy(id = 2L))

            assertThrows(ElementNotFoundException::class.java) {
                instance.getMatchById(0L)
            }
        }

        @Test
        fun `Get match with ia by id but not exist`() {
            dao.saveMatch(matchWithIA.copy(id = 2L))

            assertThrows(ElementNotFoundException::class.java) {
                instance.getMatchById(0L)
            }
        }

        @Test
        fun `Get match by id but no user exists in the system`() {
            assertThrows(ElementNotFoundException::class.java) {
                instance.getMatchById(0L)
            }
        }

        @Test
        fun `Get match with human by id but not exist duel`() {
            dao.saveMatch(matchWithHuman)

            `when`(playerIntegrationMock.getPlayerById(0L)).thenReturn(player)
            `when`(playerIntegrationMock.getPlayerById(2L)).thenReturn(iaOpponent)
            `when`(deckIntegrationMock.getDeckById(0L)).thenReturn(deck.copy(deckHistoryList = listOf(deckHistory)))

            assertThrows(ElementNotFoundException::class.java) {
                instance.getMatchById(0L)
            }
        }

        @Test
        fun `Get match with ia by id but not exist duel`() {
            dao.saveMatch(matchWithIA)

            `when`(playerIntegrationMock.getPlayerById(0L)).thenReturn(player)
            `when`(playerIntegrationMock.getPlayerById(2L)).thenReturn(iaOpponent)
            `when`(deckIntegrationMock.getDeckById(0L)).thenReturn(deck.copy(deckHistoryList = listOf(deckHistory)))

            assertThrows(ElementNotFoundException::class.java) {
                instance.getMatchById(0L)
            }
        }

    }

    @Nested
    inner class SaveMatch {

        @Test
        fun `Save match with two humanPlayer`() {
            `when`(playerIntegrationMock.savePlayer(player)).thenReturn(player)
            `when`(playerIntegrationMock.savePlayerHistory(playerHistory)).thenReturn(playerHistory)
            `when`(playerIntegrationMock.savePlayer(humanOpponent)).thenReturn(humanOpponent)
            `when`(playerIntegrationMock.savePlayerHistory(humanOpponentHistory)).thenReturn(humanOpponentHistory)
            `when`(deckIntegrationMock.saveDeck(deck)).thenReturn(deck)

            val saveMatch = instance.saveMatch(matchWithHuman)
            assertEquals(matchWithHuman, saveMatch)

            val found = dao.getMatchById(matchWithHuman.id!!)!!
            assertEquals(matchWithHuman.id, found.id)
            assertEquals(matchWithHuman.deck.id, found.deckId)
            assertEquals(matchWithHuman.status.name, found.status)
            assertTrue(found.playerIds.contains(player.id))
            assertTrue(found.playerIds.contains(humanOpponent.id))
        }

        @Test
        fun `Save match with humanPlayer and iaPlayer`() {
            `when`(playerIntegrationMock.savePlayer(player)).thenReturn(player)
            `when`(playerIntegrationMock.savePlayerHistory(playerHistory)).thenReturn(playerHistory)
            `when`(playerIntegrationMock.savePlayer(iaOpponent)).thenReturn(iaOpponent)
            `when`(playerIntegrationMock.savePlayerHistory(iaOpponentHistory)).thenReturn(iaOpponentHistory)
            `when`(deckIntegrationMock.saveDeck(deck)).thenReturn(deck)

            val saveMatch = instance.saveMatch(matchWithIA)
            assertEquals(matchWithIA, saveMatch)

            val found = dao.getMatchById(matchWithIA.id!!)!!
            assertEquals(matchWithIA.id, found.id)
            assertEquals(matchWithIA.deck.id, found.deckId)
            assertEquals(matchWithIA.status.name, found.status)
            assertTrue(found.playerIds.contains(player.id))
            assertTrue(found.playerIds.contains(iaOpponent.id))
        }

    }

}