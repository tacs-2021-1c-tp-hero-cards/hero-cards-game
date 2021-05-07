package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidMatchException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidTurnException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.MatchIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class MatchServiceTest {

    private val matchIntegrationMock = mock(MatchIntegration::class.java)
    private val deckServiceMock = mock(DeckService::class.java)
    private val userServiceMock = mock(UserService::class.java)
    private val instance = MatchService(matchIntegrationMock, deckServiceMock, userServiceMock)

    private val userName = "userName"
    private val otherUserName = "otherUserName"
    private val userId = 0L
    private val otherUserId = 1L
    private val deckId = 0L
    private val matchId = 0L

    private val user1 = User(userId, userName, "fullNameTest1", "passwordTest1", token = "tokenTest")
    private val user2 = User(otherUserId, otherUserName, "fullNameTest2", "passwordTest2")
    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()
    private val deck = Deck(deckId, "nameDeckTest", listOf(batman, flash))
    private val player = Player(id = userId, userName = userName)
    private val otherPlayer = Player(id = otherUserId, userName = otherUserName)

    @Test
    fun buildPlayers() {
        `when`(userServiceMock.searchUser(id = userId)).thenReturn(listOf(user1))
        `when`(userServiceMock.searchUser(id = otherUserId)).thenReturn(listOf(user2))

        val players = instance.buildPlayers(listOf("0", "1"), deck)

        assertEquals(2, players.size)

        val player1 = players.first { it.userName == userName }
        val availableCards1 = player1.availableCards
        assertEquals(1, availableCards1.size)
        assertEquals(batman, availableCards1.first())

        val player2 = players.first { it.userName == otherUserName }
        val availableCards2 = player2.availableCards
        assertEquals(1, availableCards2.size)
        assertEquals(flash, availableCards2.first())
    }

    @Test
    fun dealCards() {
        val players = instance.dealCards(listOf(player, otherPlayer), batman)

        var first = players.first()
        assertEquals(otherUserId, first.id)
        assertTrue(first.availableCards.isEmpty())
        assertTrue(first.prizeCards.isEmpty())

        var last = players.last()
        assertEquals(userId, last.id)
        assertTrue(last.availableCards.contains(batman))
        assertTrue(last.prizeCards.isEmpty())

        val otherPlayers = instance.dealCards(players, flash)
        first = otherPlayers.first()
        assertEquals(userId, first.id)
        assertTrue(first.availableCards.contains(batman))
        assertTrue(first.prizeCards.isEmpty())

        last = otherPlayers.last()
        assertEquals(otherUserId, last.id)
        assertTrue(last.availableCards.contains(flash))
        assertTrue(last.prizeCards.isEmpty())
    }

    @Test
    fun searchMatchById() {

        `when`(matchIntegrationMock.getAllMatches()).thenReturn(
            listOf(
                Match(
                    0L,
                    listOf(player),
                    deck,
                    MatchStatus.IN_PROGRESS
                ), Match(1L, listOf(otherPlayer), deck, MatchStatus.FINALIZED)
            )
        )

        val matchFound = instance.searchMatchById("1")

        assertEquals(1L, matchFound.id)
        assertEquals(deck, matchFound.deck)
        assertEquals(MatchStatus.FINALIZED, matchFound.status)
        assertEquals(otherPlayer, matchFound.players.first())
    }

    @Test
    fun nextDuel() {
        val flash = flash.copy(powerstats = flash.powerstats.copy(speed = 1))
        val batman = batman.copy(powerstats = batman.powerstats.copy(speed = 0))

        val match = Match(
            id = matchId,
            players = listOf(
                player.copy(availableCards = listOf(flash)),
                otherPlayer.copy(availableCards = listOf(batman))
            ),
            deck = deck,
            status = MatchStatus.IN_PROGRESS
        )

        `when`(matchIntegrationMock.getAllMatches()).thenReturn(listOf(match))
        `when`(userServiceMock.searchUser(id = userId, token = "tokenTest")).thenReturn(listOf(user1))

        val resultNextDuel = instance.nextDuel(deckId.toString(), "tokenTest", DuelType.SPEED)
        assertEquals(MatchStatus.FINALIZED, resultNextDuel.status)
        assertEquals(deck, resultNextDuel.deck)
        assertEquals(0L, resultNextDuel.id)

        val players = resultNextDuel.players

        val winPlayer = players.last()
        assertTrue(winPlayer.availableCards.isEmpty())
        assertTrue(winPlayer.prizeCards.contains(batman))
        assertTrue(winPlayer.prizeCards.contains(flash))

        val losePlayer = players.first()
        assertTrue(losePlayer.availableCards.isEmpty())
        assertTrue(losePlayer.prizeCards.isEmpty())
    }

    @Test
    fun nextDuel_turnException() {
        val flash = flash.copy(powerstats = flash.powerstats.copy(speed = 1))
        val batman = batman.copy(powerstats = batman.powerstats.copy(speed = 0))

        val match = Match(
            id = matchId,
            players = listOf(
                player.copy(availableCards = listOf(flash)),
                otherPlayer.copy(availableCards = listOf(batman))
            ),
            deck = deck,
            status = MatchStatus.IN_PROGRESS
        )

        `when`(matchIntegrationMock.getAllMatches()).thenReturn(listOf(match))
        `when`(userServiceMock.searchUser(id = userId, token = "tokenTest")).thenReturn(emptyList())

        Assertions.assertThrows(InvalidTurnException::class.java) {
            instance.nextDuel(deckId.toString(), "tokenTest", DuelType.SPEED)
        }
    }

    @Test
    fun nextDuel_matchException() {
        val flash = flash.copy(powerstats = flash.powerstats.copy(speed = 1))
        val batman = batman.copy(powerstats = batman.powerstats.copy(speed = 0))

        val match = Match(
            id = matchId,
            players = listOf(
                player.copy(availableCards = listOf(flash)),
                otherPlayer.copy(availableCards = listOf(batman))
            ),
            deck = deck,
            status = MatchStatus.FINALIZED
        )

        `when`(matchIntegrationMock.getAllMatches()).thenReturn(listOf(match))
        `when`(userServiceMock.searchUser(id = userId, token = "tokenTest")).thenReturn(emptyList())

        Assertions.assertThrows(InvalidMatchException::class.java) {
            instance.nextDuel(deckId.toString(), "tokenTest", DuelType.SPEED)
        }
    }

    @Test
    fun abortMatch() {
        val match = Match(
            id = matchId,
            players = listOf(
                player.copy(availableCards = listOf(flash)),
                otherPlayer.copy(availableCards = listOf(batman))
            ),
            deck = deck,
            status = MatchStatus.IN_PROGRESS
        )

        `when`(matchIntegrationMock.getAllMatches()).thenReturn(listOf(match))
        `when`(userServiceMock.searchUser(id = userId, token = "tokenTest")).thenReturn(listOf(user1))

        val abortMatch = instance.abortMatch(matchId.toString(), "tokenTest")

        assertEquals(MatchStatus.CANCELLED, abortMatch.status)
        assertEquals(deck, abortMatch.deck)
        assertEquals(0L, abortMatch.id)

        val players = abortMatch.players

        val player = players.first()
        assertTrue(player.availableCards.contains(flash))
        assertTrue(player.prizeCards.isEmpty())

        val otherPlayer = players.last()
        assertTrue(otherPlayer.availableCards.contains(batman))
        assertTrue(otherPlayer.prizeCards.isEmpty())
    }

    @Test
    fun abortMatch_matchFinalizedException() {
        val match = Match(
            id = matchId,
            players = listOf(
                player.copy(availableCards = listOf(flash)),
                otherPlayer.copy(availableCards = listOf(batman))
            ),
            deck = deck,
            status = MatchStatus.FINALIZED
        )

        `when`(matchIntegrationMock.getAllMatches()).thenReturn(listOf(match))
        `when`(userServiceMock.searchUser(id = userId, token = "tokenTest")).thenReturn(listOf(user1))

        Assertions.assertThrows(InvalidMatchException::class.java) {
            instance.abortMatch(matchId.toString(), "tokenTest")
        }
    }

    @Test
    fun abortMatch_matchCancelledException() {
        val match = Match(
            id = matchId,
            players = listOf(
                player.copy(availableCards = listOf(flash)),
                otherPlayer.copy(availableCards = listOf(batman))
            ),
            deck = deck,
            status = MatchStatus.CANCELLED
        )

        `when`(matchIntegrationMock.getAllMatches()).thenReturn(listOf(match))
        `when`(userServiceMock.searchUser(id = userId, token = "tokenTest")).thenReturn(listOf(user1))

        Assertions.assertThrows(InvalidMatchException::class.java) {
            instance.abortMatch(matchId.toString(), "tokenTest")
        }
    }
    @Test
    fun abortMatch_turnException() {
        val match = Match(
            id = matchId,
            players = listOf(
                player.copy(availableCards = listOf(flash)),
                otherPlayer.copy(availableCards = listOf(batman))
            ),
            deck = deck,
            status = MatchStatus.IN_PROGRESS
        )

        `when`(matchIntegrationMock.getAllMatches()).thenReturn(listOf(match))
        `when`(userServiceMock.searchUser(id = userId, token = "tokenTest")).thenReturn(emptyList())

        Assertions.assertThrows(InvalidTurnException::class.java) {
            instance.abortMatch(matchId.toString(), "tokenTest")
        }
    }

}