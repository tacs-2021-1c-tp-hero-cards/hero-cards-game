package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck.DeckHistoryEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match.DuelHistoryEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match.MatchEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match.MatchFactory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user.UserEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user.UserFactory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository.MatchRepository
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class MatchIntegrationTest {

    private val cardIntegrationMock = mock(CardIntegration::class.java)
    private val repositoryMock = mock(MatchRepository::class.java)
    private val instance = MatchIntegration(cardIntegrationMock, MatchFactory(UserFactory()), repositoryMock)

    private val id = 0L

    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    private val userName: String = "userName"
    private val fullName: String = "fullName"
    private val password: String = "password"
    private val token: String = "token"
    private val winCount: Int = 1
    private val tieCount: Int = 0
    private val loseCount: Int = 2
    private val inProgressCount: Int = 4
    private val iADifficulty = IADifficulty.HARD

    private val deckName = "deckName"
    private val matchStatus = MatchStatus.IN_PROGRESS

    private val duelType = DuelType.SPEED
    private val duelResult = DuelResult.TIE

    private val iaEntity =
        UserEntity(
            id + 1,
            userName,
            UserType.IA,
            winCount,
            tieCount,
            loseCount,
            inProgressCount,
            difficulty = iADifficulty
        )

    private val humanEntity =
        UserEntity(
            id,
            userName,
            UserType.HUMAN,
            winCount, tieCount, loseCount, inProgressCount,
            fullName,
            password,
            token,
            false
        )

    private val deckHistory =
        DeckHistoryEntity(name = deckName, cardIds = batman.id.toString() + "," + flash.id.toString())

    private val duelHistory = DuelHistoryEntity(
        playerAvailableCardIds = batman.id.toString(),
        playerPrizeCardIds = flash.id.toString(),
        opponentAvailableCardIds = flash.id.toString(),
        opponentPrizeCardIds = batman.id.toString(),
        duelType = duelType,
        duelResult = duelResult
    )
    private val matchEntity =
        MatchEntity(
            id,
            listOf(humanEntity,iaEntity),
            id,
            batman.id.toString(),
            flash.id.toString(),
            flash.id.toString(),
            batman.id.toString(),
            0L,
            deckHistory,
            matchStatus,
            duelHistory = listOf(duelHistory)
        )

    @Nested
    inner class GetMatchById {

        @Test
        fun `Get match by id`() {
            `when`(repositoryMock.getById(0L)).thenReturn(matchEntity)
            `when`(cardIntegrationMock.getCardById("69")).thenReturn(batman)
            `when`(cardIntegrationMock.getCardById("2")).thenReturn(flash)

            val found = instance.getMatchById(0L)
            assertEquals(matchEntity.toModel(listOf(batman, flash)), found)
        }


        @Test
        fun `Get match by id but not exist`() {
            `when`(repositoryMock.getById(0L)).thenReturn(matchEntity)

            assertThrows(ElementNotFoundException::class.java) {
                instance.getMatchById(1L)
            }
        }

    }

    @Test
    fun saveMatch() {
        `when`(repositoryMock.save(matchEntity)).thenReturn(matchEntity)

        instance.saveMatch(matchEntity.toModel(listOf(batman, flash)))
        verify(repositoryMock, times(1)).save(matchEntity)
    }

}