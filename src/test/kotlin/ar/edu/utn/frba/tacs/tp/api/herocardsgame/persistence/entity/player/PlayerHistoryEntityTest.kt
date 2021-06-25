package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.player

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.PlayerHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PlayerHistoryEntityTest {

    private val id: Long = 0L
    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    @Nested
    inner class ToEntity {

        @Test
        fun `Build entity with id`() {
            val model = PlayerHistory(id, batman, listOf(batman), listOf(flash))

            val entity = PlayerHistoryEntity(playerHistory = model)
            assertEquals(id, entity.id)
            assertTrue(entity.availableCardIds.contains(batman.id))
            assertTrue(entity.prizeCardIds.contains(flash.id))
        }

        @Test
        fun `Build entity without id`() {
            val model = PlayerHistory(null, batman, listOf(batman), listOf(flash))

            val entity = PlayerHistoryEntity(1L, model)
            assertEquals(1L, entity.id)
            assertTrue(entity.availableCardIds.contains(batman.id))
            assertTrue(entity.prizeCardIds.contains(flash.id))
        }
    }

    @Test
    fun toModel() {
        val entity = PlayerHistoryEntity(id, PlayerHistory(null, batman, listOf(batman), listOf(flash)))

        val model = entity.toModel(listOf(batman), listOf(flash))
        assertEquals(id, model.id)
        assertTrue(model.availableCards.contains(batman))
        assertTrue(model.prizeCards.contains(flash))
        assertEquals(batman, model.cardPlayed)
    }
}