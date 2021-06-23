package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidTurnException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.MatchIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Stats
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class MatchServiceTest {

    private val matchIntegrationMock = mock(MatchIntegration::class.java)
    private val deckServiceMock = mock(DeckService::class.java)
    private val userIntegrationMock = mock(UserIntegration::class.java)
    private val instance = MatchService(matchIntegrationMock, deckServiceMock, userIntegrationMock)

    private val user = User(0L, "userName", "fullName", "password", token = "tokenTest")
    private val player = Player(user = user)

    private val opponentUser = User(1L, "userOpponentName", "opponentFullName", "opponentPassword")
    private val opponentPlayer = Player(user = opponentUser)

    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()
    private val deck = Deck(0L, 0L, "nameDeck", listOf(batman, batman))
    private val deckHistory = DeckHistory(deck)

    @Nested
    inner class CreateMatch {

        @Test
        fun `Create match with deck that non exist`() {
            `when`(deckServiceMock.searchDeck(0L.toString())).thenReturn(emptyList())

            assertThrows(ElementNotFoundException::class.java) {
                instance.createMatch(listOf("0", "1"), 0L.toString())
            }
        }

        @Test
        fun `Create match with user that non exist`() {
            `when`(deckServiceMock.searchDeck(0L.toString())).thenReturn(listOf(deck))
            `when`(userIntegrationMock.getUserById(0L)).thenThrow(ElementNotFoundException::class.java)

            assertThrows(ElementNotFoundException::class.java) {
                instance.createMatch(listOf("0", "1"), 0L.toString())
            }
        }

        @Test
        fun `Create match`() {
            val match =
                Match(deck = deckHistory, status = MatchStatus.IN_PROGRESS, players = listOf(player, opponentPlayer).map {
                    it.copy(
                        availableCards = listOf(batman)
                    )
                })

            `when`(deckServiceMock.searchDeck(0L.toString())).thenReturn(listOf(deck))
            `when`(userIntegrationMock.getUserById(0L)).thenReturn(user)
            `when`(userIntegrationMock.getUserById(1L)).thenReturn(opponentUser)
            `when`(matchIntegrationMock.saveMatch(match)).thenReturn(match.copy(id = 0L))

            val result = instance.createMatch(listOf(0L.toString(), 1L.toString()), 0L.toString())

            assertEquals(0L, result.id)
            assertTrue(result.players.contains(player.copy(availableCards = listOf(batman))))
            assertTrue(result.players.contains(opponentPlayer.copy(availableCards = listOf(batman))))
            assertEquals(deckHistory, result.deck)
            assertEquals(MatchStatus.IN_PROGRESS, result.status)
        }

    }

    @Nested
    inner class BuildPlayers {

        @Test
        fun `Build players with users and deck`() {
            `when`(userIntegrationMock.getUserById(0L)).thenReturn(user)
            `when`(userIntegrationMock.getUserById(1L)).thenReturn(opponentUser)

            val players = instance.buildPlayers(listOf("0", "1"), deck)

            assertEquals(2, players.size)

            val player1 = players.first { it.user == user }
            val availableCards1 = player1.availableCards
            assertEquals(1, availableCards1.size)

            val player2 = players.first { it.user == opponentUser }
            val availableCards2 = player2.availableCards
            assertEquals(1, availableCards2.size)
        }

        @Test
        fun `Build players with a user that does not exist`() {
            `when`(userIntegrationMock.getUserById(0L)).thenReturn(user)
            `when`(userIntegrationMock.getUserById(1L)).thenThrow(ElementNotFoundException::class.java)

            assertThrows(ElementNotFoundException::class.java) {
                instance.buildPlayers(listOf("0", "1"), deck)
            }
        }

    }

    @Test
    fun dealCards() {
        val players = instance.dealCards(listOf(player, opponentPlayer), batman)

        var first = players.first()
        assertEquals(opponentUser, first.user)
        assertTrue(first.availableCards.isEmpty())
        assertTrue(first.prizeCards.isEmpty())

        var last = players.last()
        assertEquals(user, last.user)
        assertTrue(last.availableCards.contains(batman))
        assertTrue(last.prizeCards.isEmpty())

        val opponentPlayers = instance.dealCards(players, flash)
        first = opponentPlayers.first()
        assertEquals(user, first.user)
        assertTrue(first.availableCards.contains(batman))
        assertTrue(first.prizeCards.isEmpty())

        last = opponentPlayers.last()
        assertEquals(opponentUser, last.user)
        assertTrue(last.availableCards.contains(flash))
        assertTrue(last.prizeCards.isEmpty())
    }

    @Test
    fun searchMatchById() {
        instance.searchMatchById("0")
        verify(matchIntegrationMock, times(1)).getMatchById(0L)
    }

    @Nested
    inner class NextDuel {

        @Test
        fun `User with the turn plays next duel`() {
            val flash = flash.copy(powerstats = flash.powerstats.copy(speed = 1))
            val batman = batman.copy(powerstats = batman.powerstats.copy(speed = 0))

            val match = Match(
                id = 0L,
                players = listOf(
                    player.copy(availableCards = listOf(flash)),
                    opponentPlayer.copy(availableCards = listOf(batman))
                ),
                deck = deckHistory,
                status = MatchStatus.IN_PROGRESS
            )

            val matchResult = match.copy(
                players = listOf(
                    opponentPlayer.copy(availableCards = emptyList(), prizeCards = emptyList()),
                    player.copy(availableCards = emptyList(), prizeCards = listOf(batman, flash))
                ), status = MatchStatus.FINALIZED
            )

            `when`(matchIntegrationMock.getMatchById(0L)).thenReturn(match)
            `when`(userIntegrationMock.getUserById(0L)).thenReturn(user)
            `when`(matchIntegrationMock.saveMatch(matchResult)).thenReturn(matchResult)

            val resultNextDuel = instance.nextDuel(0L.toString(), "tokenTest", DuelType.SPEED)
            assertEquals(MatchStatus.FINALIZED, resultNextDuel.status)
            assertEquals(deckHistory, resultNextDuel.deck)
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
        fun `User without the turn plays next duel`() {
            val flash = flash.copy(powerstats = flash.powerstats.copy(speed = 1))
            val batman = batman.copy(powerstats = batman.powerstats.copy(speed = 0))

            val match = Match(
                id = 0L,
                players = listOf(
                    player.copy(availableCards = listOf(flash)),
                    opponentPlayer.copy(availableCards = listOf(batman))
                ),
                deck = deckHistory,
                status = MatchStatus.IN_PROGRESS
            )

            `when`(matchIntegrationMock.getMatchById(0L)).thenReturn(match)
            `when`(userIntegrationMock.getUserById(0L)).thenReturn(user.copy(token = "tokenTest2"))

            assertThrows(InvalidTurnException::class.java) {
                instance.nextDuel(0L.toString(), "tokenTest", DuelType.SPEED)
            }
        }

    }

    @Nested
    inner class AbortMatch {

        @Test
        fun `User with the turn aborts match`() {
            val match = Match(
                id = 0L,
                players = listOf(
                    player.copy(availableCards = listOf(flash)),
                    opponentPlayer.copy(availableCards = listOf(batman))
                ),
                deck = deckHistory,
                status = MatchStatus.IN_PROGRESS
            )

            val matchResult = match.copy(
                status = MatchStatus.CANCELLED,
                players = listOf(
                    player.copy(availableCards = listOf(flash), user = user.copy(stats = Stats().addLoseMatch())),
                    opponentPlayer.copy(
                        availableCards = listOf(batman),
                        user = opponentUser.copy(stats = Stats().addWinMatch())
                    )
                )
            )

            `when`(matchIntegrationMock.getMatchById(0L)).thenReturn(match)
            `when`(userIntegrationMock.getUserById(0L)).thenReturn(user)
            `when`(matchIntegrationMock.saveMatch(matchResult)).thenReturn(matchResult)

            val abortMatch = instance.abortMatch(0L.toString(), "tokenTest")

            assertEquals(MatchStatus.CANCELLED, abortMatch.status)
            assertEquals(deckHistory, abortMatch.deck)
            assertEquals(0L, abortMatch.id)

            val players = abortMatch.players

            val player = players.first()
            assertTrue(player.availableCards.contains(flash))
            assertTrue(player.prizeCards.isEmpty())
            assertEquals(1, player.user.stats.loseCount)

            val opponentPlayer = players.last()
            assertTrue(opponentPlayer.availableCards.contains(batman))
            assertTrue(opponentPlayer.prizeCards.isEmpty())
            assertEquals(1, opponentPlayer.user.stats.winCount)
        }

        @Test
        fun `User without the turn aborts match`() {
            val match = Match(
                id = 0L,
                players = listOf(
                    player.copy(availableCards = listOf(flash)),
                    opponentPlayer.copy(availableCards = listOf(batman))
                ),
                deck = deckHistory,
                status = MatchStatus.IN_PROGRESS
            )

            `when`(matchIntegrationMock.getMatchById(0L)).thenReturn(match)
            `when`(userIntegrationMock.getUserById(0L)).thenReturn(user.copy(token = "tokenTest2"))

            assertThrows(InvalidTurnException::class.java) {
                instance.abortMatch(0L.toString(), "tokenTest")
            }
        }

    }

}