package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.DuelHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.PlayerHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DuelHistoryEntityTest {

    private val flash = BuilderContextUtils.buildFlash()
    private val batman = BuilderContextUtils.buildBatman()

    private val id: Long = 0L
    private val player = PlayerHistory(0L, 0L, flash, listOf(flash))
    private val opponent = PlayerHistory(1L, 1L, batman, listOf(batman))
    private val duelType = DuelType.SPEED
    private val duelResult = DuelResult.WIN

    @Nested
    inner class ToEntity {

        @Test
        fun `Build entity with id`() {
            val model = DuelHistory(id, player, opponent, duelType, duelResult)

            val entity = DuelHistoryEntity(duelHistory = model)
            assertEquals(id, entity.id)
            assertEquals(0L, entity.playerVersion)
            assertEquals(1L, entity.opponentVersion)
            assertEquals("SPEED", entity.duelType)
            assertEquals("WIN", entity.duelResult)
        }

        @Test
        fun `Build entity without id`() {
            val model = DuelHistory(null, player, opponent, duelType, duelResult)

            val entity = DuelHistoryEntity(1L, model)
            assertEquals(1L, entity.id)
            assertEquals(0L, entity.playerVersion)
            assertEquals(1L, entity.opponentVersion)
            assertEquals("SPEED", entity.duelType)
            assertEquals("WIN", entity.duelResult)
        }

    }

    @Test
    fun toModel() {
        val entity = DuelHistoryEntity(id, DuelHistory(null, player, opponent, duelType, duelResult))

        val model = entity.toModel(player, opponent)
        assertEquals(id, model.id)
        assertEquals(player, model.player)
        assertEquals(opponent, model.opponent)
        assertEquals(duelResult, model.duelResult)
        assertEquals(duelType, model.duelType)
    }

}