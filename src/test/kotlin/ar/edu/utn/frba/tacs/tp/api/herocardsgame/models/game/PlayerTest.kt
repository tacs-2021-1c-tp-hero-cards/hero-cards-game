package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PlayerTest {

    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    lateinit var human: Human
    lateinit var player: Player
    lateinit var humanOpponent: Human
    lateinit var opponent: Player

    @BeforeEach
    fun init() {
        human = Human(0L, "userName", "fullName", "password")
        player = Player(id = 0L, user = human, availableCards = listOf(batman))
        humanOpponent = Human(1L, "userName2", "fullName2", "password2")
        opponent = Player(id = 1L, user = human)
    }

    @Nested
    inner class ResolveDuel {

        @Test
        fun winDuel() {
            val winPlayer = player.winDuel(flash)

            assertEquals(human, winPlayer.user)
            assertTrue(winPlayer.prizeCards.contains(batman))
            assertTrue(winPlayer.prizeCards.contains(flash))
            assertTrue(winPlayer.availableCards.isEmpty())
        }

        @Test
        fun loseDuel() {
            val losePlayer = player.loseDuel()

            assertEquals(human, losePlayer.user)
            assertTrue(losePlayer.availableCards.isEmpty())
            assertTrue(losePlayer.prizeCards.isEmpty())
        }

        @Test
        fun tieDuel() {
            val tie = player.tieDuel()

            assertEquals(human, tie.user)
            assertTrue(tie.availableCards.isEmpty())
            assertTrue(tie.prizeCards.contains(batman))
        }

    }

    @Nested
    inner class ResultMatch {

        @Test
        fun `User win match and add a victory`() {
            val stats = player.startMatch().winMatch().user.stats
            assertEquals(1, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

        @Test
        fun `User tied game and add a tie`() {
            val stats = player.startMatch().tieMatch().user.stats
            assertEquals(0, stats.winCount)
            assertEquals(1, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

        @Test
        fun `User loses match and add a loss`() {
            val stats = player.startMatch().loseMatch().user.stats
            assertEquals(0, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(1, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

        @Test
        fun `User started game and add a in progress match`() {
            val stats = player.startMatch().user.stats
            assertEquals(0, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(1, stats.inProgressCount)
        }

        @Test
        fun `User ended game and dec a in progress match`() {
            val stats = player.startMatch().endMatch().user.stats
            assertEquals(0, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

    }

    @Nested
    inner class CalculateWinPlayer {

        @Test
        fun `Player win match`() {
            val winPlayer = player.copy(prizeCards = listOf(batman)).startMatch()
            val loseOpponent = opponent.copy(prizeCards = emptyList()).startMatch()

            val resultPlayers = winPlayer.calculateWinPlayer(loseOpponent)

            val win = resultPlayers.first()
            assertEquals(0L, win.id)
            assertEquals(0, win.user.stats.inProgressCount)
            assertEquals(1, win.user.stats.winCount)
            assertEquals(0, win.user.stats.loseCount)
            assertEquals(0, win.user.stats.tieCount)

            val lose = resultPlayers.last()
            assertEquals(1L, lose.id)
            assertEquals(0, lose.user.stats.inProgressCount)
            assertEquals(0, lose.user.stats.winCount)
            assertEquals(1, lose.user.stats.loseCount)
            assertEquals(0, lose.user.stats.tieCount)
        }

        @Test
        fun `Player lose match`() {
            val loseOpponent = player.copy(prizeCards = emptyList()).startMatch()
            val winPlayer = opponent.copy(prizeCards = listOf(batman)).startMatch()

            val resultPlayers = loseOpponent.calculateWinPlayer(winPlayer)

            val lose = resultPlayers.first()
            assertEquals(0L, lose.id)
            assertEquals(0, lose.user.stats.inProgressCount)
            assertEquals(0, lose.user.stats.winCount)
            assertEquals(1, lose.user.stats.loseCount)
            assertEquals(0, lose.user.stats.tieCount)

            val win = resultPlayers.last()
            assertEquals(1L, win.id)
            assertEquals(0, win.user.stats.inProgressCount)
            assertEquals(1, win.user.stats.winCount)
            assertEquals(0, win.user.stats.loseCount)
            assertEquals(0, win.user.stats.tieCount)
        }

        @Test
        fun `Player tie match`() {
            val tiePlayer = player.copy(prizeCards = listOf(batman)).startMatch()
            val tieOpponent = opponent.copy(prizeCards = listOf(batman)).startMatch()

            val resultPlayers = tiePlayer.calculateWinPlayer(tieOpponent)

            val tie = resultPlayers.first()
            assertEquals(0L, tie.id)
            assertEquals(0, tie.user.stats.inProgressCount)
            assertEquals(0, tie.user.stats.winCount)
            assertEquals(0, tie.user.stats.loseCount)
            assertEquals(1, tie.user.stats.tieCount)

            val otherTie = resultPlayers.last()
            assertEquals(1L, otherTie.id)
            assertEquals(0, otherTie.user.stats.inProgressCount)
            assertEquals(0, otherTie.user.stats.winCount)
            assertEquals(0, otherTie.user.stats.loseCount)
            assertEquals(1, otherTie.user.stats.tieCount)
        }

    }

}