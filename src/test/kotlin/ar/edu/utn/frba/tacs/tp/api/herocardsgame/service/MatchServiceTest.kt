package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidMatchException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidTurnException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.MatchIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Stats
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.DuelHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class MatchServiceTest {

    private val matchIntegrationMock = mock(MatchIntegration::class.java)
    private val deckServiceMock = mock(DeckService::class.java)
    private val userIntegrationMock = mock(UserIntegration::class.java)
    private val notificationClientServiceMock = mock(NotificationClientService::class.java)
    private val instance =
        MatchService(matchIntegrationMock, deckServiceMock, userIntegrationMock, notificationClientServiceMock)

    private val user = Human(0L, "userName", "fullName", "password", "tokenTest")
    private val player = Player(user, true)

    private val humanOpponentUser =
        Human(
            1L,
            "humanOpponentUserName",
            "humanOpponentUserFullName",
            "humanOpponentUserPassword",
            "humanOpponentUserToken"
        )
    private val humanOpponentPlayer = Player(humanOpponentUser)

    private val iaOpponentUser =
        IA(2L, "iaOpponentUserName", difficulty = IADifficulty.HARD)
    private val iaOpponentPlayer = Player(iaOpponentUser)

    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()
    private val deck = Deck(0L, "nameDeck", listOf(batman, batman))
    private val deckHistory = DeckHistory(deck)

    @Nested
    inner class CreateMatch {

        @Test
        fun `Create match with deck that non exist`() {
            `when`(deckServiceMock.searchDeck(0L.toString())).thenReturn(emptyList())

            assertThrows(ElementNotFoundException::class.java) {
                instance.createMatch("tokenTest", "1", UserType.HUMAN, 0L.toString())
            }
        }

        @Test
        fun `Create match with user that non exist`() {
            `when`(deckServiceMock.searchDeck(0L.toString())).thenReturn(listOf(deck))
            `when`(userIntegrationMock.getUserById(0L)).thenThrow(ElementNotFoundException::class.java)

            assertThrows(ElementNotFoundException::class.java) {
                instance.createMatch("tokenTest", "1", UserType.HUMAN, 0L.toString())
            }
        }

        @Test
        fun `Create match with two human`() {
            val match =
                Match(
                    deck = deckHistory,
                    status = MatchStatus.PENDING,
                    player = player.copy(availableCards = listOf(batman)),
                    opponent = humanOpponentPlayer.copy(availableCards = listOf(batman))
                )

            `when`(deckServiceMock.searchDeck(0L.toString())).thenReturn(listOf(deck))
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(token = user.token))
                .thenReturn(listOf(user))
            `when`(userIntegrationMock.getUserById(1L)).thenReturn(humanOpponentUser)
            `when`(matchIntegrationMock.saveMatch(match)).thenReturn(match.copy(id = 0L))
            val randomMatch = match.updateTurn()
            `when`(matchIntegrationMock.saveMatch(randomMatch)).thenReturn(randomMatch.copy(id = 0L))

            val result = instance.createMatch("tokenTest", "1", UserType.HUMAN, 0L.toString())

            assertEquals(0L, result.id)
            assertEquals(deckHistory, result.deck)
            assertEquals(MatchStatus.PENDING, result.status)

            verify(notificationClientServiceMock, times(1)).notifyCreateMatch("1", UserType.HUMAN, result)
        }

        @Test
        fun `Create match with human and ia`() {
            val match =
                Match(
                    deck = deckHistory,
                    status = MatchStatus.IN_PROGRESS,
                    player = player.copy(availableCards = listOf(batman)).startMatch(),
                    opponent = iaOpponentPlayer.copy(availableCards = listOf(batman)).startMatch()
                )

            `when`(deckServiceMock.searchDeck(0L.toString())).thenReturn(listOf(deck))
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(token = user.token))
                .thenReturn(listOf(user))
            `when`(userIntegrationMock.getUserById(2L)).thenReturn(iaOpponentUser)

            `when`(matchIntegrationMock.saveMatch(match)).thenReturn(match.copy(id = 0L))
            val randomMatch = match.updateTurn()
            `when`(matchIntegrationMock.saveMatch(randomMatch)).thenReturn(randomMatch.copy(id = 0L))

            val result = instance.createMatch("tokenTest", "2", UserType.IA, 0L.toString())

            assertEquals(0L, result.id)
            assertEquals(deckHistory, result.deck)
            assertEquals(MatchStatus.IN_PROGRESS, result.status)
        }

    }

    @Nested
    inner class BuildPlayers {

        @Test
        fun `Build human players with users and deck`() {
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(token = user.token))
                .thenReturn(listOf(user))
            `when`(userIntegrationMock.getUserById(1L)).thenReturn(humanOpponentUser)

            val players = instance.buildPlayers("tokenTest", "1", UserType.HUMAN, deck)

            assertEquals(2, players.size)

            val player1 = players.first { it.user == user }
            assertTrue(player1.createdMatch)
            val availableCards1 = player1.availableCards
            assertEquals(1, availableCards1.size)

            val player2 = players.first { it.user == humanOpponentUser }
            assertFalse(player2.createdMatch)
            val availableCards2 = player2.availableCards
            assertEquals(1, availableCards2.size)
        }

        @Test
        fun `Build human and ia players with users and deck`() {
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(token = user.token))
                .thenReturn(listOf(user))
            `when`(userIntegrationMock.getUserById(2L)).thenReturn(iaOpponentUser)

            val players = instance.buildPlayers("tokenTest", "2", UserType.IA, deck)

            assertEquals(2, players.size)

            val player1 = players.first { it.user == user }
            assertTrue(player1.createdMatch)
            val availableCards1 = player1.availableCards
            assertEquals(1, availableCards1.size)

            val player2 = players.first { it.user == iaOpponentUser }
            assertFalse(player2.createdMatch)
            val availableCards2 = player2.availableCards
            assertEquals(1, availableCards2.size)
        }

        @Test
        fun `Build players with a human that does not exist`() {
            `when`(userIntegrationMock.getUserById(0L)).thenReturn(user)
            `when`(userIntegrationMock.getUserById(1L)).thenThrow(ElementNotFoundException::class.java)

            assertThrows(ElementNotFoundException::class.java) {
                instance.buildPlayers("tokenTest", "1", UserType.HUMAN, deck)
            }
        }

        @Test
        fun `Build players with a ia that does not exist`() {
            `when`(userIntegrationMock.getUserById(0L)).thenReturn(user)
            `when`(userIntegrationMock.getUserById(2L)).thenThrow(ElementNotFoundException::class.java)

            assertThrows(ElementNotFoundException::class.java) {
                instance.buildPlayers("tokenTest", "2", UserType.IA, deck)
            }
        }

    }

    @Test
    fun dealCards() {
        val players = instance.dealCards(listOf(player, iaOpponentPlayer), batman)

        var first = players.first()
        assertEquals(iaOpponentUser, first.user)
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
        assertEquals(iaOpponentUser, last.user)
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
        fun `Human user with the turn plays next duel`() {
            val flash = flash.copy(powerstats = flash.powerstats.copy(speed = 1))
            val batman = batman.copy(powerstats = batman.powerstats.copy(speed = 0))

            val match = Match(
                0L,
                player.copy(availableCards = listOf(flash)).startMatch(),
                iaOpponentPlayer.copy(availableCards = listOf(batman)).startMatch(),
                deckHistory,
                MatchStatus.IN_PROGRESS
            )

            val matchResult = match.copy(
                player =
                iaOpponentPlayer.copy(availableCards = emptyList(), prizeCards = emptyList()).startMatch().loseMatch(),
                opponent = player.copy(availableCards = emptyList(), prizeCards = listOf(batman, flash)).startMatch()
                    .winMatch(),
                status = MatchStatus.FINALIZED,
                duelHistoryList = listOf(
                    DuelHistory(
                        player.copy(availableCards = listOf(flash)),
                        iaOpponentPlayer.copy(availableCards = listOf(batman)),
                        DuelType.SPEED,
                        DuelResult.WIN
                    )
                )
            )

            `when`(matchIntegrationMock.getMatchById(0L)).thenReturn(match)
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken("0")).thenReturn(listOf(user))
            `when`(matchIntegrationMock.saveMatch(matchResult)).thenReturn(matchResult)

            val resultNextDuel = instance.nextDuel(0L.toString(), "tokenTest", DuelType.SPEED)
            assertEquals(MatchStatus.FINALIZED, resultNextDuel.status)
            assertEquals(deckHistory, resultNextDuel.deck)
            assertEquals(0L, resultNextDuel.id)

            val winPlayer = resultNextDuel.opponent
            assertTrue(winPlayer.availableCards.isEmpty())
            assertTrue(winPlayer.prizeCards.contains(batman))
            assertTrue(winPlayer.prizeCards.contains(flash))

            val losePlayer = resultNextDuel.player
            assertTrue(losePlayer.availableCards.isEmpty())
            assertTrue(losePlayer.prizeCards.isEmpty())
        }

        @Test
        fun `IA user with the turn plays next duel`() {
            val flash = flash.copy(powerstats = flash.powerstats.copy(speed = 1))
            val batman = batman.copy(powerstats = batman.powerstats.copy(speed = 0))

            val match = Match(
                0L,
                iaOpponentPlayer.copy(availableCards = listOf(flash)).startMatch(),
                player.copy(availableCards = listOf(batman)).startMatch(),
                deckHistory,
                MatchStatus.IN_PROGRESS
            )

            val matchResult = match.copy(
                player = player.copy(availableCards = emptyList(), prizeCards = emptyList()).startMatch()
                    .loseMatch(),
                opponent = iaOpponentPlayer.copy(availableCards = emptyList(), prizeCards = listOf(batman, flash)).startMatch()
                    .winMatch(),
                status = MatchStatus.FINALIZED,
                duelHistoryList = listOf(
                    DuelHistory(
                        iaOpponentPlayer.copy(availableCards = listOf(flash)),
                        player.copy(availableCards = listOf(batman)),
                        DuelType.SPEED,
                        DuelResult.WIN
                    )
                )
            )

            `when`(matchIntegrationMock.getMatchById(0L)).thenReturn(match)
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken("0")).thenReturn(listOf(user))
            `when`(matchIntegrationMock.saveMatch(matchResult)).thenReturn(matchResult)

            val resultNextDuel = instance.nextDuel(0L.toString(), "tokenTest", DuelType.SPEED)
            assertEquals(MatchStatus.FINALIZED, resultNextDuel.status)
            assertEquals(deckHistory, resultNextDuel.deck)
            assertEquals(0L, resultNextDuel.id)

            val winPlayer = resultNextDuel.opponent
            assertTrue(winPlayer.availableCards.isEmpty())
            assertTrue(winPlayer.prizeCards.contains(batman))
            assertTrue(winPlayer.prizeCards.contains(flash))

            val losePlayer = resultNextDuel.player
            assertTrue(losePlayer.availableCards.isEmpty())
            assertTrue(losePlayer.prizeCards.isEmpty())
        }

        @Test
        fun `Human user without the turn plays next duel`() {
            val match = Match(
                0L,
                player.copy(availableCards = listOf(flash)),
                humanOpponentPlayer.copy(availableCards = listOf(batman)),
                deckHistory,
                MatchStatus.IN_PROGRESS
            )

            `when`(matchIntegrationMock.getMatchById(0L)).thenReturn(match)
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken("0")).thenReturn(
                listOf(
                    user.copy(
                        token = "tokenTest2"
                    )
                )
            )

            assertThrows(InvalidTurnException::class.java) {
                instance.nextDuel(0L.toString(), "tokenTest", DuelType.SPEED)
            }
        }

        @Test
        fun `IA user without the turn plays next duel`() {
            val match = Match(
                0L,
                player.copy(availableCards = listOf(flash)),
                iaOpponentPlayer.copy(availableCards = listOf(batman)),
                deckHistory,
                MatchStatus.IN_PROGRESS
            )

            `when`(matchIntegrationMock.getMatchById(0L)).thenReturn(match)
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken("0")).thenReturn(listOf(user))

            assertThrows(InvalidTurnException::class.java) {
                instance.nextDuel(0L.toString(), "tokenTest", null)
            }
        }

    }

    @Nested
    inner class AbortMatch {

        @Test
        fun `User with the turn aborts match`() {
            val match = Match(
                0L,
                player.copy(availableCards = listOf(flash)).startMatch(),
                humanOpponentPlayer.copy(availableCards = listOf(batman)).startMatch(),
                deckHistory,
                MatchStatus.IN_PROGRESS
            )

            val matchResult = match.copy(
                status = MatchStatus.CANCELLED,
                player = player.copy(availableCards = listOf(flash)).startMatch().loseMatch(),
                opponent = humanOpponentPlayer.copy(
                    availableCards = listOf(batman),
                    user = humanOpponentUser.copy(stats = Stats())
                ).startMatch().winMatch()
            )

            `when`(matchIntegrationMock.getMatchById(0L)).thenReturn(match)
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken("0")).thenReturn(listOf(user))
            `when`(matchIntegrationMock.saveMatch(matchResult)).thenReturn(matchResult)

            val abortMatch = instance.abortMatch(0L.toString(), "tokenTest")

            assertEquals(MatchStatus.CANCELLED, abortMatch.status)
            assertEquals(deckHistory, abortMatch.deck)
            assertEquals(0L, abortMatch.id)

            val player = abortMatch.player
            assertTrue(player.availableCards.contains(flash))
            assertTrue(player.prizeCards.isEmpty())
            assertEquals(1, player.user.stats.loseCount)

            val opponentPlayer = abortMatch.opponent
            assertTrue(opponentPlayer.availableCards.contains(batman))
            assertTrue(opponentPlayer.prizeCards.isEmpty())
            assertEquals(1, opponentPlayer.user.stats.winCount)
        }

        @Test
        fun `User without the turn aborts match`() {
            val match = Match(
                0L,
                player.copy(availableCards = listOf(flash)),
                humanOpponentPlayer.copy(availableCards = listOf(batman)),
                deckHistory,
                MatchStatus.IN_PROGRESS
            )

            `when`(matchIntegrationMock.getMatchById(0L)).thenReturn(match)
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken("0")).thenReturn(
                listOf(user.copy(token = "tokenTest2"))
            )

            assertThrows(InvalidTurnException::class.java) {
                instance.abortMatch(0L.toString(), "tokenTest")
            }
        }

    }

    @Nested
    inner class MatchConfirmation {

        @Test
        fun `Confirm match when the match is pending`() {
            val match = Match(0L, player, humanOpponentPlayer, deckHistory, MatchStatus.PENDING)
            `when`(matchIntegrationMock.getMatchById(0L)).thenReturn(match)

            instance.matchConfirmation("0", true, humanOpponentUser.token!!)
            verify(matchIntegrationMock, times(1)).saveMatch(
                match.copy(
                    status = MatchStatus.IN_PROGRESS,
                    player = player.startMatch(),
                    opponent = humanOpponentPlayer.startMatch()
                )
            )
        }

        @Test
        fun `Reject match when the match is pending`() {
            val match = Match(0L, player, humanOpponentPlayer, deckHistory, MatchStatus.PENDING)
            `when`(matchIntegrationMock.getMatchById(0L)).thenReturn(match)

            instance.matchConfirmation("0", false, humanOpponentUser.token!!)
            verify(matchIntegrationMock, times(1)).saveMatch(match.copy(status = MatchStatus.CANCELLED))
        }

        @Test
        fun `Confirm match when the match is in progress`() {
            `when`(matchIntegrationMock.getMatchById(0L)).thenReturn(
                Match(id = 0L, player, humanOpponentPlayer, deckHistory, MatchStatus.IN_PROGRESS)
            )

            assertThrows(InvalidMatchException::class.java) {
                instance.matchConfirmation("0", false, humanOpponentUser.token!!)
            }
        }

        @Test
        fun `Confirm match when the match not found`() {
            `when`(matchIntegrationMock.getMatchById(0L)).thenThrow(ElementNotFoundException::class.java)

            assertThrows(ElementNotFoundException::class.java) {
                instance.matchConfirmation("0", true, "token")
            }
        }

    }

    @Nested
    inner class SearchMatchByUserId {

        @Test
        fun `Search match by user id only those created by the user`() {
            val match =
                Match(
                    id = 1L,
                    deck = deckHistory,
                    status = MatchStatus.PENDING,
                    player = player.copy(availableCards = listOf(batman)),
                    opponent = humanOpponentPlayer.copy(availableCards = listOf(batman))
                )

            `when`(matchIntegrationMock.findMatchByUserId(0L, true)).thenReturn(listOf(match))
            val founds = instance.searchMatchByUserId("0", true)
            assertEquals(1, founds.size)

            val first = founds.first()
            assertEquals(1L, first.matchId)
            assertEquals(MatchStatus.PENDING, first.matchStatus)
            assertEquals(humanOpponentUser, first.userOpponent)
            assertTrue(first.isMatchCreatedByUser)
        }

        @Test
        fun `Search match by user id only those created by the user but there no`() {
            `when`(matchIntegrationMock.findMatchByUserId(0L, true)).thenReturn(emptyList())

            val founds = instance.searchMatchByUserId("0", true)
            assertTrue(founds.isEmpty())
        }

        @Test
        fun `Search match by user id no matter who created them`() {
            val match =
                Match(
                    id = 1L,
                    deck = deckHistory,
                    status = MatchStatus.IN_PROGRESS,
                    player = player.copy(availableCards = listOf(batman), createdMatch = false),
                    opponent = humanOpponentPlayer.copy(availableCards = listOf(batman), createdMatch = true)
                )

            `when`(matchIntegrationMock.findMatchByUserId(0L, false)).thenReturn(listOf(match))
            val founds = instance.searchMatchByUserId("0", false)
            assertEquals(1, founds.size)

            val first = founds.first()
            assertEquals(1L, first.matchId)
            assertEquals(MatchStatus.IN_PROGRESS, first.matchStatus)
            assertEquals(humanOpponentUser, first.userOpponent)
            assertFalse(first.isMatchCreatedByUser)
        }

        @Test
        fun `Search match by user id Search match but there no`() {
            `when`(matchIntegrationMock.findMatchByUserId(0L, false)).thenReturn(emptyList())

            val founds = instance.searchMatchByUserId("0", false)
            assertTrue(founds.isEmpty())
        }


    }

}