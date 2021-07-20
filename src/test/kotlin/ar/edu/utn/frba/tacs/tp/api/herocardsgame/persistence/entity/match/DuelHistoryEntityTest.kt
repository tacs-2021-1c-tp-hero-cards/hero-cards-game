package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.DuelHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.PlayerHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DuelHistoryEntityTest {

    private val flash = BuilderContextUtils.buildFlash()
    private val flashId = flash.id.toString()
    private val batman = BuilderContextUtils.buildBatman()
    private val batmanId = batman.id.toString()

    private val id: Long = 0L
    private val player = PlayerHistory(flash, listOf(flash))
    private val opponent = PlayerHistory(batman, listOf(batman))
    private val duelType = DuelType.SPEED
    private val duelResult = DuelResult.WIN

    @Nested
    inner class ToEntity {

        @Test
        fun `Build entity with id`() {
            val model = DuelHistory(id, player, opponent, duelType, duelResult)

            val entity = DuelHistoryEntity(duelHistory = model)
            assertEquals(id, entity.id)
            assertEquals(flashId, entity.playerAvailableCardIds)
            assertEquals("", entity.playerPrizeCardIds)
            assertEquals(batmanId, entity.opponentAvailableCardIds)
            assertEquals("", entity.opponentPrizeCardIds)
            assertEquals(duelType, entity.duelType)
            assertEquals(duelResult, entity.duelResult)
        }

        @Test
        fun `Build entity without id`() {
            val model = DuelHistory(null, player, opponent, duelType, duelResult)

            val entity = DuelHistoryEntity(model)
            assertNull(entity.id)
            assertEquals(flashId, entity.playerAvailableCardIds)
            assertEquals("", entity.playerPrizeCardIds)
            assertEquals(batmanId, entity.opponentAvailableCardIds)
            assertEquals("", entity.opponentPrizeCardIds)
            assertEquals(duelType, entity.duelType)
            assertEquals(duelResult, entity.duelResult)
        }

    }

    @Test
    fun toModel() {
        val entity = DuelHistoryEntity(
            playerAvailableCardIds = flashId,
            playerPrizeCardIds = "",
            opponentAvailableCardIds = batmanId,
            opponentPrizeCardIds = "",
            duelType = duelType,
            duelResult = duelResult
        )

        val model = entity.toModel(listOf(batman, flash))
        assertNull(model.id)
        assertEquals(player, model.player)
        assertEquals(opponent, model.opponent)
        assertEquals(duelResult, model.duelResult)
        assertEquals(duelType, model.duelType)
    }

}