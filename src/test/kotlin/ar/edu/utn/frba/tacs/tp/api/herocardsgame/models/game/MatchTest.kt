package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidMatchException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

internal class MatchTest {

    private val deckMock = mock(DeckHistory::class.java)
    private val batman = BuilderContextUtils.buildBatman().copy()
    private val flash = BuilderContextUtils.buildFlash().copy()

    lateinit var player: Player
    lateinit var humanOpponent: Player
    lateinit var iaOpponent: Player

    @BeforeEach
    fun init() {
        player = Player(Human(0L, "player", "fullName", "password"))
        humanOpponent = Player(Human(1L, "humanUserName", "humanFullName", "humanPassword"))
        iaOpponent = Player(IA(2L, "iaUserName", difficulty = IADifficulty.HARD))
    }

    @Nested
    inner class UpdateStatusMatch {

        @Test
        fun `If player has no cards available then match is finalized`() {
            val match = Match(
                player = player.copy(availableCards = listOf(batman), prizeCards = listOf(batman)).startMatch(),
                opponent = humanOpponent.startMatch(),
                deck = deckMock,
                status = MatchStatus.IN_PROGRESS
            ).updateStatusMatch()

            assertEquals(MatchStatus.FINALIZED, match.status)

            val win = match.player
            assertEquals(0, win.user.stats.inProgressCount)
            assertEquals(1, win.user.stats.winCount)
            assertEquals(0, win.user.stats.loseCount)
            assertEquals(0, win.user.stats.tieCount)

            val lose = match.opponent
            assertEquals(0, lose.user.stats.inProgressCount)
            assertEquals(0, lose.user.stats.winCount)
            assertEquals(1, lose.user.stats.loseCount)
            assertEquals(0, lose.user.stats.tieCount)
        }

        @Test
        fun `If all players have no cards available then match is finalized`() {
            val match = Match(
                player = player.startMatch(),
                opponent = iaOpponent.startMatch(),
                deck = deckMock,
                status = MatchStatus.IN_PROGRESS
            ).updateStatusMatch()

            assertEquals(MatchStatus.FINALIZED, match.status)

            val tie = match.player
            assertEquals(0, tie.user.stats.inProgressCount)
            assertEquals(0, tie.user.stats.winCount)
            assertEquals(0, tie.user.stats.loseCount)
            assertEquals(1, tie.user.stats.tieCount)

            val otherTie = match.opponent
            assertEquals(0, otherTie.user.stats.inProgressCount)
            assertEquals(0, otherTie.user.stats.winCount)
            assertEquals(0, otherTie.user.stats.loseCount)
            assertEquals(1, otherTie.user.stats.tieCount)
        }


        @Test
        fun `If all players have cards available then match is in progress`() {
            val match = Match(
                player = player.copy(availableCards = listOf(batman)),
                opponent = humanOpponent.copy(availableCards = listOf(flash)),
                deck = deckMock,
                status = MatchStatus.IN_PROGRESS
            ).updateStatusMatch()

            assertEquals(MatchStatus.IN_PROGRESS, match.status)
        }

    }

    @Test
    fun updateTurn() {
        val match = Match(
            player = player,
            opponent = humanOpponent,
            deck = deckMock,
            status = MatchStatus.IN_PROGRESS
        ).updateTurn()

        assertEquals(humanOpponent, match.player)
        assertEquals(player, match.opponent)
    }

    @Nested
    inner class ResolveDuel {

        @Test
        fun `Resolve Duel when match is cancelled`() {
            val match = Match(
                id = 0L,
                player = player.copy(availableCards = listOf(flash)),
                opponent = humanOpponent.copy(availableCards = listOf(batman)),
                deck = deckMock,
                status = MatchStatus.CANCELLED
            )

            assertThrows(InvalidMatchException::class.java) {
                match.resolveDuel(DuelType.SPEED)
            }
        }

        @Test
        fun `Resolve Duel when match is finalized`() {
            val match = Match(
                id = 0L,
                player = player.copy(availableCards = listOf(flash)),
                opponent = humanOpponent.copy(availableCards = listOf(batman)),
                deck = deckMock,
                status = MatchStatus.FINALIZED
            )

            assertThrows(InvalidMatchException::class.java) {
                match.resolveDuel(DuelType.SPEED)
            }
        }

        @Test
        fun `Resolve duel that wins when user is human`() {
            val flash = flash.copy(powerstats = flash.powerstats.copy(speed = 1))
            val batman = batman.copy(powerstats = batman.powerstats.copy(speed = 0))

            val match = Match(
                player = player.copy(availableCards = listOf(flash)),
                opponent = humanOpponent.copy(availableCards = listOf(batman)),
                deck = deckMock,
                status = MatchStatus.IN_PROGRESS
            ).resolveDuel(DuelType.SPEED)

            val winPlayer = match.player
            assertTrue(winPlayer.availableCards.isEmpty())
            assertTrue(winPlayer.prizeCards.contains(batman))
            assertTrue(winPlayer.prizeCards.contains(flash))

            val losePlayer = match.opponent
            assertTrue(losePlayer.availableCards.isEmpty())
            assertTrue(losePlayer.prizeCards.isEmpty())

            val duelHistory = match.duelHistoryList.first()
            assertNull(duelHistory.id)
            assertEquals(DuelResult.WIN, duelHistory.duelResult)
            assertEquals(DuelType.SPEED, duelHistory.duelType)

            val duelHistoryPlayer = duelHistory.player
            assertEquals(flash, duelHistoryPlayer.cardPlayed)
            assertTrue(duelHistoryPlayer.availableCards.contains(flash))
            assertTrue(duelHistoryPlayer.prizeCards.isEmpty())

            val duelHistoryOpponent = duelHistory.opponent
            assertEquals(batman, duelHistoryOpponent.cardPlayed)
            assertTrue(duelHistoryOpponent.availableCards.contains(batman))
            assertTrue(duelHistoryOpponent.prizeCards.isEmpty())
        }

        @Test
        fun `Resolve duel that wins when user is ia`() {
            val match = Match(
                player = iaOpponent.copy(availableCards = listOf(flash)),
                opponent = player.copy(availableCards = listOf(batman)),
                deck = deckMock,
                status = MatchStatus.IN_PROGRESS
            ).resolveDuel()

            val winPlayer = match.player
            assertTrue(winPlayer.availableCards.isEmpty())
            assertTrue(winPlayer.prizeCards.contains(batman))
            assertTrue(winPlayer.prizeCards.contains(flash))

            val losePlayer = match.opponent
            assertTrue(losePlayer.availableCards.isEmpty())
            assertTrue(losePlayer.prizeCards.isEmpty())

            val duelHistory = match.duelHistoryList.first()
            assertNull(duelHistory.id)
            assertEquals(DuelResult.WIN, duelHistory.duelResult)
            assertEquals(DuelType.SPEED, duelHistory.duelType)

            val duelHistoryPlayer = duelHistory.player
            assertEquals(flash, duelHistoryPlayer.cardPlayed)
            assertTrue(duelHistoryPlayer.availableCards.contains(flash))
            assertTrue(duelHistoryPlayer.prizeCards.isEmpty())

            val duelHistoryOpponent = duelHistory.opponent
            assertEquals(batman, duelHistoryOpponent.cardPlayed)
            assertTrue(duelHistoryOpponent.availableCards.contains(batman))
            assertTrue(duelHistoryOpponent.prizeCards.isEmpty())
        }

        @Test
        fun `Resolve duel that lose when user is human`() {
            val flash = flash.copy(powerstats = flash.powerstats.copy(weight = 1))
            val batman = batman.copy(powerstats = batman.powerstats.copy(weight = 0))

            val match = Match(
                player = player.copy(availableCards = listOf(flash)),
                opponent = humanOpponent.copy(availableCards = listOf(batman)),
                deck = deckMock,
                status = MatchStatus.IN_PROGRESS
            ).resolveDuel(DuelType.WEIGHT)

            val losePlayer = match.player
            assertTrue(losePlayer.availableCards.isEmpty())
            assertTrue(losePlayer.prizeCards.isEmpty())

            val winPlayer = match.opponent
            assertTrue(winPlayer.availableCards.isEmpty())
            assertTrue(winPlayer.prizeCards.contains(batman))
            assertTrue(winPlayer.prizeCards.contains(flash))

            val duelHistory = match.duelHistoryList.first()
            assertNull(duelHistory.id)
            assertEquals(DuelResult.LOSE, duelHistory.duelResult)
            assertEquals(DuelType.WEIGHT, duelHistory.duelType)

            val duelHistoryPlayer = duelHistory.player
            assertEquals(flash, duelHistoryPlayer.cardPlayed)
            assertTrue(duelHistoryPlayer.availableCards.contains(flash))
            assertTrue(duelHistoryPlayer.prizeCards.isEmpty())

            val duelHistoryOpponent = duelHistory.opponent
            assertEquals(batman, duelHistoryOpponent.cardPlayed)
            assertTrue(duelHistoryOpponent.availableCards.contains(batman))
            assertTrue(duelHistoryOpponent.prizeCards.isEmpty())
        }

        @Test
        fun `Resolve duel that lose when user is ia`() {
            val flash = flash.copy(powerstats = flash.powerstats.copy(speed = 0, height = 0))

            val match = Match(
                player = iaOpponent.copy(availableCards = listOf(flash)),
                opponent = player.copy(availableCards = listOf(batman)),
                deck = deckMock,
                status = MatchStatus.IN_PROGRESS
            ).resolveDuel()

            val losePlayer = match.player
            assertTrue(losePlayer.availableCards.isEmpty())
            assertTrue(losePlayer.prizeCards.isEmpty())

            val winPlayer = match.opponent
            assertTrue(winPlayer.availableCards.isEmpty())
            assertTrue(winPlayer.prizeCards.contains(batman))
            assertTrue(winPlayer.prizeCards.contains(flash))

            val duelHistory = match.duelHistoryList.first()
            assertNull(duelHistory.id)
            assertEquals(DuelResult.LOSE, duelHistory.duelResult)
            assertEquals(DuelType.INTELLIGENCE, duelHistory.duelType)

            val duelHistoryPlayer = duelHistory.player
            assertEquals(flash, duelHistoryPlayer.cardPlayed)
            assertTrue(duelHistoryPlayer.availableCards.contains(flash))
            assertTrue(duelHistoryPlayer.prizeCards.isEmpty())

            val duelHistoryOpponent = duelHistory.opponent
            assertEquals(batman, duelHistoryOpponent.cardPlayed)
            assertTrue(duelHistoryOpponent.availableCards.contains(batman))
            assertTrue(duelHistoryOpponent.prizeCards.isEmpty())
        }

        @Test
        fun `Resolve duel that tie when user is human`() {
            val flash = flash.copy(powerstats = flash.powerstats.copy(combat = 1))
            val batman = batman.copy(powerstats = batman.powerstats.copy(combat = 1))

            val match = Match(
                player = player.copy(availableCards = listOf(flash)),
                opponent = humanOpponent.copy(availableCards = listOf(batman)),
                deck = deckMock,
                status = MatchStatus.IN_PROGRESS
            ).resolveDuel(DuelType.COMBAT)

            val tiePlayer = match.player
            assertTrue(tiePlayer.availableCards.isEmpty())
            assertTrue(tiePlayer.prizeCards.contains(flash))

            val otherTiePlayer = match.opponent
            assertTrue(otherTiePlayer.availableCards.isEmpty())
            assertTrue(otherTiePlayer.prizeCards.contains(batman))

            val duelHistory = match.duelHistoryList.first()
            assertNull(duelHistory.id)
            assertEquals(DuelResult.TIE, duelHistory.duelResult)
            assertEquals(DuelType.COMBAT, duelHistory.duelType)

            val duelHistoryPlayer = duelHistory.player
            assertEquals(flash, duelHistoryPlayer.cardPlayed)
            assertTrue(duelHistoryPlayer.availableCards.contains(flash))
            assertTrue(duelHistoryPlayer.prizeCards.isEmpty())

            val duelHistoryOpponent = duelHistory.opponent
            assertEquals(batman, duelHistoryOpponent.cardPlayed)
            assertTrue(duelHistoryOpponent.availableCards.contains(batman))
            assertTrue(duelHistoryOpponent.prizeCards.isEmpty())
        }

        @Test
        fun `Resolve duel that tie when user is ia`() {
            val batman = batman.copy(powerstats = batman.powerstats.copy(speed = 85000000))

            val match = Match(
                player = iaOpponent.copy(availableCards = listOf(flash)),
                opponent = player.copy(availableCards = listOf(batman)),
                deck = deckMock,
                status = MatchStatus.IN_PROGRESS
            ).resolveDuel()

            val tiePlayer = match.player
            assertTrue(tiePlayer.availableCards.isEmpty())
            assertTrue(tiePlayer.prizeCards.contains(flash))

            val otherTiePlayer = match.opponent
            assertTrue(otherTiePlayer.availableCards.isEmpty())
            assertTrue(otherTiePlayer.prizeCards.contains(batman))

            val duelHistory = match.duelHistoryList.first()
            assertNull(duelHistory.id)
            assertEquals(DuelResult.TIE, duelHistory.duelResult)
            assertEquals(DuelType.SPEED, duelHistory.duelType)

            val duelHistoryPlayer = duelHistory.player
            assertEquals(flash, duelHistoryPlayer.cardPlayed)
            assertTrue(duelHistoryPlayer.availableCards.contains(flash))
            assertTrue(duelHistoryPlayer.prizeCards.isEmpty())

            val duelHistoryOpponent = duelHistory.opponent
            assertEquals(batman, duelHistoryOpponent.cardPlayed)
            assertTrue(duelHistoryOpponent.availableCards.contains(batman))
            assertTrue(duelHistoryOpponent.prizeCards.isEmpty())
        }

    }

    @Nested
    inner class AbortMatch {

        @Test
        fun `Abort match when match is cancelled`() {
            val match = Match(
                id = 0L,
                player = player.copy(availableCards = listOf(flash)),
                opponent = humanOpponent.copy(availableCards = listOf(batman)),
                deck = deckMock,
                status = MatchStatus.CANCELLED
            )

            assertThrows(InvalidMatchException::class.java) {
                match.abortMatch()
            }
        }

        @Test
        fun `Abort match when match is finalized`() {
            val match = Match(
                id = 0L,
                player = player.copy(availableCards = listOf(flash)),
                opponent = humanOpponent.copy(availableCards = listOf(batman)),
                deck = deckMock,
                status = MatchStatus.FINALIZED
            )

            assertThrows(InvalidMatchException::class.java) {
                match.abortMatch()
            }
        }

        @Test
        fun `Abort match when match is in progress`() {
            val match = Match(
                player = player,
                opponent = humanOpponent,
                deck = deckMock,
                status = MatchStatus.IN_PROGRESS
            )

            val abortMatch = match.abortMatch()
            assertEquals(MatchStatus.CANCELLED, abortMatch.status)
            assertEquals(1, abortMatch.player.user.stats.loseCount)
            assertEquals(1, abortMatch.opponent.user.stats.winCount)
        }

    }

    @Nested
    inner class ValidateNotFinalizedOrCancelled {

        @Test
        fun `Failure when match is FINALIZED`() {
            assertThrows(InvalidMatchException::class.java) {
                Match(0L, player, humanOpponent, deckMock, MatchStatus.FINALIZED).validateNotFinalizedOrCancelled()
            }
        }

        @Test
        fun `Failure when match is CANCELLED`() {
            assertThrows(InvalidMatchException::class.java) {
                Match(0L, player, humanOpponent, deckMock, MatchStatus.CANCELLED).validateNotFinalizedOrCancelled()
            }
        }

        @Test
        fun `Success when match is IN_PROGRESS`() {
            Match(
                player = player,
                opponent = humanOpponent,
                deck = deckMock,
                status = MatchStatus.IN_PROGRESS
            ).validateNotFinalizedOrCancelled()
        }

    }

    @Nested
    inner class ConfirmMatch {

        @Test
        fun `Confirm match when the match is pending`() {
            val result = Match(
                player = player,
                opponent = humanOpponent,
                deck = deckMock,
                status = MatchStatus.PENDING
            ).confirmMatch(true)
            assertEquals(MatchStatus.IN_PROGRESS, result.status)

            val playerStat = result.player.user.stats
            assertTrue(playerStat.winCount == 0 && playerStat.tieCount == 0 && playerStat.loseCount == 0 && playerStat.inProgressCount == 1)
            val opponentStat = result.opponent.user.stats
            assertTrue(opponentStat.winCount == 0 && opponentStat.tieCount == 0 && opponentStat.loseCount == 0 && opponentStat.inProgressCount == 1)
        }

        @Test
        fun `Reject match when the match is pending`() {
            val result = Match(
                player = player,
                opponent = humanOpponent,
                deck = deckMock,
                status = MatchStatus.PENDING
            ).confirmMatch(false)
            assertEquals(MatchStatus.CANCELLED, result.status)

            val playerStat = result.player.user.stats
            assertTrue(playerStat.winCount == 0 && playerStat.tieCount == 0 && playerStat.loseCount == 0 && playerStat.inProgressCount == 0)
            val opponentStat = result.opponent.user.stats
            assertTrue(opponentStat.winCount == 0 && opponentStat.tieCount == 0 && opponentStat.loseCount == 0 && opponentStat.inProgressCount == 0)
        }

        @Test
        fun `Confirm match when the match is in progress`() {
            assertThrows(InvalidMatchException::class.java) {
                Match(id = 0L, player = player, opponent = humanOpponent, deck = deckMock, status = MatchStatus.PENDING)
                    .copy(
                        status = MatchStatus.IN_PROGRESS
                    ).confirmMatch(true)
            }
        }

    }

    @Nested
    inner class ConfirmMatchAutomatic{

        @Test
        fun `Confirm match automatic when opponent is IA`(){
            val result = Match(
                player = player,
                opponent = iaOpponent,
                deck = deckMock,
                status = MatchStatus.PENDING
            ).confirmMatchAutomatic(UserType.IA)

            assertEquals(MatchStatus.IN_PROGRESS, result.status)

            val playerStat = result.player.user.stats
            assertTrue(playerStat.winCount == 0 && playerStat.tieCount == 0 && playerStat.loseCount == 0 && playerStat.inProgressCount == 1)
            val opponentStat = result.opponent.user.stats
            assertTrue(opponentStat.winCount == 0 && opponentStat.tieCount == 0 && opponentStat.loseCount == 0 && opponentStat.inProgressCount == 1)
        }

        @Test
        fun `Not confirm match automatic when opponent is IA`(){
            val result = Match(
                player = player,
                opponent = humanOpponent,
                deck = deckMock,
                status = MatchStatus.PENDING
            ).confirmMatchAutomatic(UserType.HUMAN)

            assertEquals(MatchStatus.PENDING, result.status)

            val playerStat = result.player.user.stats
            assertTrue(playerStat.winCount == 0 && playerStat.tieCount == 0 && playerStat.loseCount == 0 && playerStat.inProgressCount == 0)
            val opponentStat = result.opponent.user.stats
            assertTrue(opponentStat.winCount == 0 && opponentStat.tieCount == 0 && opponentStat.loseCount == 0 && opponentStat.inProgressCount == 0)
        }

    }
}