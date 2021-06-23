package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidMatchException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

internal class MatchTest {

    private val deckMock = mock(Deck::class.java)
    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    lateinit var player: Player
    lateinit var opponent: Player

    @BeforeEach
    fun init() {
        player = Player(0L, User(0L, "player", "fullName", "password"))
        opponent = Player(0L, User(1L, "opponent", "fullName", "password"))
    }

    @Nested
    inner class UpdateStatusMatch {

        @Test
        fun `If player has no cards available then match is finalized`() {
            val match = Match(
                players = listOf(player.copy(availableCards = listOf(batman)), opponent),
                deck = deckMock,
                status = MatchStatus.IN_PROGRESS
            ).updateStatusMatch()

            assertEquals(MatchStatus.FINALIZED, match.status)
        }

        @Test
        fun `If all players have no cards available then match is finalized`() {
            val match = Match(
                players = listOf(player, opponent),
                deck = deckMock,
                status = MatchStatus.IN_PROGRESS
            ).updateStatusMatch()

            assertEquals(MatchStatus.FINALIZED, match.status)
        }


        @Test
        fun `If all players have cards available then match is in progress`() {
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

    @Nested
    inner class ResolveDuel {

        @Test
        fun `Resolve Duel when match is cancelled`() {
            val match = Match(
                id = 0L,
                players = listOf(
                    player.copy(availableCards = listOf(flash)),
                    opponent.copy(availableCards = listOf(batman))
                ),
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
                players = listOf(
                    player.copy(availableCards = listOf(flash)),
                    opponent.copy(availableCards = listOf(batman))
                ),
                deck = deckMock,
                status = MatchStatus.FINALIZED
            )

            assertThrows(InvalidMatchException::class.java) {
                match.resolveDuel(DuelType.SPEED)
            }
        }

        @Test
        fun `Resolve duel that wins`() {
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
        fun `Resolve duel that lose`() {
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
        fun `Resolve duel that tie`() {
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

    @Nested
    inner class AbortMatch {

        @Test
        fun `Abort match when match is cancelled`() {
            val match = Match(
                id = 0L,
                players = listOf(
                    player.copy(availableCards = listOf(flash)),
                    opponent.copy(availableCards = listOf(batman))
                ),
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
                players = listOf(
                    player.copy(availableCards = listOf(flash)),
                    opponent.copy(availableCards = listOf(batman))
                ),
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
                players = listOf(player, opponent),
                deck = deckMock,
                status = MatchStatus.IN_PROGRESS
            )

            val abortMatch = match.abortMatch()
            assertEquals(MatchStatus.CANCELLED, abortMatch.status)

            val players = abortMatch.players
            assertEquals(1, players.first().user.stats.loseCount)
            assertEquals(1, players.last().user.stats.winCount)
        }

    }

    @Nested
    inner class ValidateNotFinalizedOrCancelled {

        @Test
        fun `Failure when match is FINALIZED`() {
            assertThrows(InvalidMatchException::class.java) {
                Match(0L, emptyList(), deckMock, MatchStatus.FINALIZED).validateNotFinalizedOrCancelled()
            }
        }

        @Test
        fun `Failure when match is CANCELLED`() {
            assertThrows(InvalidMatchException::class.java) {
                Match(0L, emptyList(), deckMock, MatchStatus.CANCELLED).validateNotFinalizedOrCancelled()
            }
        }

        @Test
        fun `Success when match is IN_PROGRESS`() {
            Match(
                players = emptyList(),
                deck = deckMock,
                status = MatchStatus.IN_PROGRESS
            ).validateNotFinalizedOrCancelled()
        }

    }
}