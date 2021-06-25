package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.player

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.PlayerHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PlayerHistoryEntityTest {

    private val id: Long = 0L
    private val version: Long = 0L
    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    @Nested
    inner class ToEntity {

        @Test
        fun `Build entity with version`() {
            val model = PlayerHistory(id, version, batman, listOf(batman), listOf(flash))

            val entity = PlayerHistoryEntity(playerHistory = model)
            assertEquals(id, entity.id)
            assertEquals(version, entity.version)
            assertTrue(entity.availableCardIds.contains(batman.id))
            assertTrue(entity.prizeCardIds.contains(flash.id))
        }

        @Test
        fun `Build entity without version`() {
            val model = PlayerHistory(0L, null, batman, listOf(batman), listOf(flash))

            val entity = PlayerHistoryEntity(1L, model)
            assertEquals(id, entity.id)
            assertEquals(1L, entity.version)
            assertTrue(entity.availableCardIds.contains(batman.id))
            assertTrue(entity.prizeCardIds.contains(flash.id))
        }
    }

    @Test
    fun toModel() {
        val entity = PlayerHistoryEntity(version, PlayerHistory(id, null, batman, listOf(batman), listOf(flash)))

        val model = entity.toModel(listOf(batman), listOf(flash))
        assertEquals(id, model.id)
        assertEquals(version, model.version)
        assertTrue(model.availableCards.contains(batman))
        assertTrue(model.prizeCards.contains(flash))
        assertEquals(batman, model.cardPlayed)
    }
}