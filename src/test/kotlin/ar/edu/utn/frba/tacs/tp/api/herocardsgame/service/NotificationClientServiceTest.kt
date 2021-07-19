package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.DuelHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.NotifyResponse
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.messaging.simp.SimpMessagingTemplate

internal class NotificationClientServiceTest {

    private val userIntegrationMock = mock(UserIntegration::class.java)
    private val templateMock = mock(SimpMessagingTemplate::class.java)
    private val instance = NotificationClientService(userIntegrationMock, templateMock)

    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    private val user = Human(0L, "userName", "fullName", "password", "userToken")
    private val player = Player(0L, user = user, availableCards = listOf(batman), prizeCards = listOf(flash))

    private val humanOpponentUser =
        Human(1L, "humanOpponentUserName", "humanOpponentUserFullName", "humanOpponentUserPassword", "humanToken")
    private val humanOpponentPlayer =
        Player(1L, user = humanOpponentUser, availableCards = listOf(batman), prizeCards = listOf(flash))

    private val iaOpponentUser =
        IA(2L, "iaOpponentUserName", difficulty = IADifficulty.HARD)
    private val iaOpponentPlayer =
        Player(2L, user = iaOpponentUser, availableCards = listOf(batman), prizeCards = listOf(flash))
    private val deckHistory = DeckHistory(Deck(0L, 0L, "nameDeck", listOf(batman, flash)))

    private val match = Match(0L, player, humanOpponentPlayer, deckHistory, MatchStatus.IN_PROGRESS)

    @Nested
    inner class NotifyCreateMatch {

        @Test
        fun `Notify creation match if opponent is human with token`() {
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(id = "1"))
                .thenReturn(listOf(humanOpponentUser))
            instance.notifyCreateMatch("1", UserType.HUMAN, match)
            verify(templateMock, times(1)).convertAndSend(
                "/topic/user/humanToken/notifications",
                NotifyResponse(match.id!!, user)
            )
        }

        @Test
        fun `Non notify creation match if opponent is human without token`() {
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(id = "1"))
                .thenReturn(listOf(humanOpponentUser.copy(token = null)))
            instance.notifyCreateMatch("1", UserType.HUMAN, match)
            verifyNoInteractions(templateMock)
        }

        @Test
        fun `Non notify creation match if opponent is ia`() {
            instance.notifyCreateMatch("2", UserType.IA, match)
            verifyNoInteractions(templateMock)
        }

        @Test
        fun `Non notify creation match if not found user`() {
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(id = "1")).thenReturn(emptyList())

            assertThrows(ElementNotFoundException::class.java) {
                instance.notifyCreateMatch("1", UserType.HUMAN, match)
            }

            verifyNoInteractions(templateMock)
        }

    }

    @Nested
    inner class NotifyConfirmMatch {

        @Test
        fun `Notify confirm match if opponent is human with token`() {
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(id = "0"))
                .thenReturn(listOf(user))
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(id = "1"))
                .thenReturn(listOf(humanOpponentUser))

            val token = humanOpponentUser.token!!
            instance.notifyConfirmMatch(token, match)

            verify(templateMock, times(1)).convertAndSend(
                "/topic/user/${user.token}/confirmations",
                NotifyResponse(match.id!!, user)
            )
        }

        @Test
        fun `Non notify confirm match if opponent is human without token`() {
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(id = "0"))
                .thenReturn(listOf(user.copy(token = null)))
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(id = "1"))
                .thenReturn(listOf(humanOpponentUser))

            val token = humanOpponentUser.token!!
            instance.notifyConfirmMatch(token, match)

            verifyNoInteractions(templateMock)
        }

        @Test
        fun `Notify reject match if opponent is human with token`() {
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(id = "0"))
                .thenReturn(listOf(user))
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(id = "1"))
                .thenReturn(listOf(humanOpponentUser))

            val token = humanOpponentUser.token!!
            instance.notifyConfirmMatch(token, match.copy(status = MatchStatus.CANCELLED))

            verify(templateMock, times(1)).convertAndSend(
                "/topic/user/${user.token}/rejections",
                NotifyResponse(match.id!!, user)
            )
        }

        @Test
        fun `Non notify reject match if opponent is human without token`() {
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(id = "0"))
                .thenReturn(listOf(user.copy(token = null)))
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(id = "1"))
                .thenReturn(listOf(humanOpponentUser))

            val token = humanOpponentUser.token!!
            instance.notifyConfirmMatch(token, match.copy(status = MatchStatus.CANCELLED))

            verifyNoInteractions(templateMock)
        }

        @Test
        fun `Non notify confirm match if opponent is ia`() {
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(id = "0"))
                .thenReturn(listOf(user))

            instance.notifyConfirmMatch(user.token!!, match.copy(player = player, opponent = iaOpponentPlayer))

            verifyNoInteractions(templateMock)
        }

        @Test
        fun `Non notify reject match if opponent is ia`() {
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(id = "0"))
                .thenReturn(listOf(user))

            instance.notifyConfirmMatch(
                user.token!!,
                match.copy(player = player, opponent = iaOpponentPlayer, status = MatchStatus.CANCELLED)
            )

            verifyNoInteractions(templateMock)
        }

    }

    @Nested
    inner class NotifyResultDuel {

        @Test
        fun `Notify result if player win against a human with token`() {
            val duelResult = DuelHistory(player, humanOpponentPlayer, DuelType.SPEED, DuelResult.WIN)
            val duelResultOpponent = DuelHistory(humanOpponentPlayer, player, DuelType.SPEED, DuelResult.LOSE)

            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(id = "1"))
                .thenReturn(listOf(humanOpponentUser))

            instance.notifyResultDuel(
                match.copy(player = humanOpponentPlayer, opponent = player, duelHistoryList = listOf(duelResult))
            )

            verify(templateMock, times(1)).convertAndSend("/topic/user/humanToken/nextDuel", duelResultOpponent)
        }

        @Test
        fun `Non notify result if player win against a human without token`() {
            val duelResult = DuelHistory(player, humanOpponentPlayer, DuelType.SPEED, DuelResult.WIN)

            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(id = "1"))
                .thenReturn(listOf(humanOpponentUser.copy(token = null)))

            instance.notifyResultDuel(
                match.copy(player = humanOpponentPlayer, opponent = player, duelHistoryList = listOf(duelResult))
            )

            verifyNoInteractions(templateMock)
        }

        @Test
        fun `Notify result if player lose against a human with token`() {
            val duelResult = DuelHistory(player, humanOpponentPlayer, DuelType.SPEED, DuelResult.LOSE)
            val duelResultOpponent = DuelHistory(humanOpponentPlayer, player, DuelType.SPEED, DuelResult.WIN)

            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(id = "1"))
                .thenReturn(listOf(humanOpponentUser))

            instance.notifyResultDuel(
                match.copy(
                    player = humanOpponentPlayer,
                    opponent = player,
                    duelHistoryList = listOf(duelResult)
                )
            )

            verify(templateMock, times(1)).convertAndSend("/topic/user/humanToken/nextDuel", duelResultOpponent)
        }

        @Test
        fun `Non notify result if player lose against a human without token`() {
            val duelResult = DuelHistory(player, humanOpponentPlayer, DuelType.SPEED, DuelResult.LOSE)

            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(id = "1"))
                .thenReturn(listOf(humanOpponentUser.copy(token = null)))

            instance.notifyResultDuel(
                match.copy(
                    player = humanOpponentPlayer,
                    opponent = player,
                    duelHistoryList = listOf(duelResult)
                )
            )

            verifyNoInteractions(templateMock)
        }

        @Test
        fun `Notify result if player tie against a human with token`() {
            val duelResult = DuelHistory(player, humanOpponentPlayer, DuelType.SPEED, DuelResult.TIE)
            val duelResultOpponent = DuelHistory(humanOpponentPlayer, player, DuelType.SPEED, DuelResult.TIE)

            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(id = "1"))
                .thenReturn(listOf(humanOpponentUser))

            instance.notifyResultDuel(
                match.copy(
                    player = humanOpponentPlayer,
                    opponent = player,
                    duelHistoryList = listOf(duelResult)
                )
            )

            verify(templateMock, times(1)).convertAndSend("/topic/user/humanToken/nextDuel", duelResultOpponent)
        }

        @Test
        fun `Non notify result if player tie against a human without token`() {
            val duelResult = DuelHistory(player, humanOpponentPlayer, DuelType.SPEED, DuelResult.TIE)

            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(id = "1"))
                .thenReturn(listOf(humanOpponentUser.copy(token = null)))

            instance.notifyResultDuel(
                match.copy(
                    player = humanOpponentPlayer,
                    opponent = player,
                    duelHistoryList = listOf(duelResult)
                )
            )

            verifyNoInteractions(templateMock)
        }

    }

    @Nested
    inner class NotifyAbort {

        @Test
        fun `Notify abort match if opponent is human and not empty token`() {
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(id = "1"))
                .thenReturn(listOf(humanOpponentUser))

            instance.notifyAbort(match)

            verify(templateMock, times(1)).convertAndSend(
                "/topic/user/${humanOpponentUser.token}/abortions",
                NotifyResponse(match.id!!, humanOpponentUser)
            )
        }

        @Test
        fun `Notify abort match if opponent is human and empty token`() {
            `when`(userIntegrationMock.searchHumanUserByIdUserNameFullNameOrToken(id = "1"))
                .thenReturn(listOf(humanOpponentUser.copy(token = null)))

            instance.notifyAbort(match)

            verifyNoInteractions(templateMock)
        }

        @Test
        fun `Notify abort match if opponent is ia`() {
            instance.notifyAbort(match.copy(opponent = iaOpponentPlayer))
            verifyNoInteractions(templateMock)
        }

    }
}