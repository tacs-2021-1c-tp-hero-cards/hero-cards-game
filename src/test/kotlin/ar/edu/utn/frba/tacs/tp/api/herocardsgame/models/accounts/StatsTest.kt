package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class StatsTest {

    @Nested
    inner class CalculateTotalPoint {

        @Test
        fun `1 win and 1 tie equals 4`() {
            val stats = Stats(winCount = 1, tieCount = 1, loseCount = 1, inProgressCount = 1)
            assertEquals(4, stats.calculateTotalPoint())
        }

        @Test
        fun `Empty matches play user equals 0`() {
            assertEquals(0, Stats().calculateTotalPoint())
        }

    }

    @Nested
    inner class AddMatch {

        @Test
        fun `User win match and add a victory`() {
            val stats = Stats().addWinMatch()

            assertEquals(1, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

        @Test
        fun `User tied game and add a tie`() {
            val stats = Stats().addTieMatch()

            assertEquals(0, stats.winCount)
            assertEquals(1, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

        @Test
        fun `User loses match and add a loss`() {
            val stats = Stats().addLoseMatch()

            assertEquals(0, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(1, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

        @Test
        fun `User started game and add a in progress match`() {
            val stats = Stats().addInProgressMatch()

            assertEquals(0, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(1, stats.inProgressCount)
        }

        @Test
        fun `User end game and dec a in progress match`() {
            val stats = Stats().addInProgressMatch().decInProgressMatch()

            assertEquals(0, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

    }
}