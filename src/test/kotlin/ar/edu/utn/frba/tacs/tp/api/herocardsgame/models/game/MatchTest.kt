package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

internal class MatchTest {

    private val player = Player(0L, "player")
    private val opponent = Player(0L, "opponent")
    private val deckMock = mock(Deck::class.java)
    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    @Test
    fun statusMatch_onePlayerEmptyAvailableCards_FINALIZED() {
        val match = Match(
            players = listOf(player.copy(availableCards = listOf(batman)), opponent),
            deck = deckMock,
            status = MatchStatus.IN_PROGRESS
        ).updateStatusMatch()

        assertEquals(MatchStatus.FINALIZED, match.status)
    }

    @Test
    fun statusMatch_allPlayersEmptyAvailableCards_FINALIZED() {
        val match = Match(
            players = listOf(player, opponent),
            deck = deckMock,
            status = MatchStatus.IN_PROGRESS
        ).updateStatusMatch()

        assertEquals(MatchStatus.FINALIZED, match.status)
    }


    @Test
    fun statusMatch_IN_PROGRESS() {
        val match = Match(
            players = listOf(
                player.copy(availableCards = listOf(batman)),
                opponent.copy(availableCards = listOf(flash))
            ),
            deck = deckMock,
            status = MatchStatus.IN_PROGRESS
        ).updateStatusMatch()

        assertEquals(MatchStatus.IN_PROGRESS, match.status)
    }

    @Test
    fun updateTurn() {
        val match = Match(
            players = listOf(player, opponent),
            deck = deckMock,
            status = MatchStatus.IN_PROGRESS
        ).updateTurn()

        val players = match.players
        assertEquals(opponent, players.first())
        assertEquals(player, players.last())
    }

    @Test
    fun resolveDuel_win() {
        val flash = flash.copy(powerstats = flash.powerstats.copy(speed = 1))
        val batman = batman.copy(powerstats = batman.powerstats.copy(speed = 0))

        val match = Match(
            players = listOf(
                player.copy(availableCards = listOf(flash)),
                opponent.copy(availableCards = listOf(batman))
            ),
            deck = deckMock,
            status = MatchStatus.IN_PROGRESS
        ).resolveDuel(DuelType.SPEED)

        val players = match.players

        val winPlayer = players.first()
        assertTrue(winPlayer.availableCards.isEmpty())
        assertTrue(winPlayer.prizeCards.contains(batman))
        assertTrue(winPlayer.prizeCards.contains(flash))

        val losePlayer = players.last()
        assertTrue(losePlayer.availableCards.isEmpty())
        assertTrue(losePlayer.prizeCards.isEmpty())
    }

    @Test
    fun resolveDuel_lose() {
        val flash = flash.copy(powerstats = flash.powerstats.copy(weight = 1))
        val batman = batman.copy(powerstats = batman.powerstats.copy(weight = 0))

        val match = Match(
            players = listOf(
                player.copy(availableCards = listOf(flash)),
                opponent.copy(availableCards = listOf(batman))
            ),
            deck = deckMock,
            status = MatchStatus.IN_PROGRESS
        ).resolveDuel(DuelType.WEIGHT)

        val players = match.players

        val losePlayer = players.first()
        assertTrue(losePlayer.availableCards.isEmpty())
        assertTrue(losePlayer.prizeCards.isEmpty())

        val winPlayer = players.last()
        assertTrue(winPlayer.availableCards.isEmpty())
        assertTrue(winPlayer.prizeCards.contains(batman))
        assertTrue(winPlayer.prizeCards.contains(flash))
    }

    @Test
    fun resolveDuel_tie() {
        val flash = flash.copy(powerstats = flash.powerstats.copy(combat = 1))
        val batman = batman.copy(powerstats = batman.powerstats.copy(combat = 1))

        val match = Match(
            players = listOf(
                player.copy(availableCards = listOf(flash)),
                opponent.copy(availableCards = listOf(batman))
            ),
            deck = deckMock,
            status = MatchStatus.IN_PROGRESS
        ).resolveDuel(DuelType.COMBAT)

        val players = match.players

        val tiePlayer = players.first()
        assertTrue(tiePlayer.availableCards.isEmpty())
        assertTrue(tiePlayer.prizeCards.contains(flash))

        val otherTiePlayer = players.last()
        assertTrue(otherTiePlayer.availableCards.isEmpty())
        assertTrue(otherTiePlayer.prizeCards.contains(batman))
    }


}