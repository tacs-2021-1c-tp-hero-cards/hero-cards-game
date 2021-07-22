package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck.DeckHistoryEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user.UserEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class MatchEntityTest {

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

    @Test
    fun toModel() {
        val entity =
            MatchEntity(
                id,
                listOf(humanEntity, iaEntity),
                humanEntity.id!!,
                batman.id.toString(),
                flash.id.toString(),
                flash.id.toString(),
                batman.id.toString(),
                0L,
                deckHistory,
                matchStatus,
                duelHistory = listOf(duelHistory)
            )

        val model = entity.toModel(listOf(batman, flash))
        assertEquals(id, model.id)
        assertEquals(deckHistory.toModel(id, listOf(batman, flash)), model.deck)
        assertEquals(MatchStatus.IN_PROGRESS, model.status)
        assertEquals(humanEntity.toModel(), model.player.user)
        assertEquals(iaEntity.toModel(), model.opponent.user)

        val duelHistory = model.duelHistoryList.first()
        assertNull(duelHistory.id)
        assertEquals(duelType   , duelHistory.duelType)
        assertEquals(duelResult, duelHistory.duelResult)
    }
}