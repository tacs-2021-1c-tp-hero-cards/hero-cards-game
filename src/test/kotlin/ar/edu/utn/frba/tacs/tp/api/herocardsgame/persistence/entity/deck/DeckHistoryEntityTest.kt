package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DeckHistoryEntityTest {

    private val id: Long = 0L
    private val version: Long = 0L
    private val name: String = "deckName"
    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()
    private val cards = listOf(batman, flash)
    private val cardsId: String = "69,2"

    @Nested
    inner class ToEntity {

        @Test
        fun `Build entity without version with model with version`() {
            val model = DeckHistory(id, version, name, cards)

            val entity = DeckHistoryEntity(model)
            assertEquals(id, entity.id)
            assertEquals(name, entity.name)
            assertEquals(cardsId, entity.cardIds)
        }

        @Test
        fun `Build entity with id`() {
            val model = DeckHistory(id, version, name, cards)

            val entity = DeckHistoryEntity(model)
            assertEquals(id, entity.id)
            assertEquals(name, entity.name)
            assertEquals(cardsId, entity.cardIds)
        }

    }

    @Test
    fun toModel() {
        val entity = DeckHistoryEntity(DeckHistory(id, version, name, cards))

        val model = entity.toModel(deckId = id, listOf(batman, flash))
        assertEquals(id, model.deckId)
        assertEquals(version, model.deckVersion)
        assertEquals(name, model.name)
        assertTrue(model.cards.contains(batman))
        assertTrue(model.cards.contains(flash))
    }

}