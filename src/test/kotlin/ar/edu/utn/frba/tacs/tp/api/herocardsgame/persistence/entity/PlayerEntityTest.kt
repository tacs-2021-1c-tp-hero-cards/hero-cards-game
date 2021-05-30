package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Stats
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class PlayerEntityTest {

    private val id = 1L

    private val userName: String = "userName"
    private val fullName: String = "fullName"
    private val password: String = "password"
    private val token: String = "token"
    private val winCount: Int = 1
    private val tieCount: Int = 0
    private val loseCount: Int = 2
    private val inProgressCount: Int = 4
    private val user =
        User(id, userName, fullName, password, token, Stats(winCount, tieCount, loseCount, inProgressCount))

    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()


    @Test
    fun toEntityWithId() {
        val model = Player(id, user, listOf(batman), listOf(flash))

        val entity = PlayerEntity(player = model)
        assertEquals(id, entity.id)
        assertEquals(id, entity.userId)
        assertTrue(entity.availableCardIds.first() == batman.id)
        assertTrue(entity.prizeCardIds.first() == flash.id)
    }

    @Test
    fun toEntityWithOutId() {
        val model = Player(null, user, listOf(batman), listOf(flash))

        val entity = PlayerEntity(1L, model)
        assertEquals(1L, entity.id)
        assertEquals(id, entity.userId)
        assertTrue(entity.availableCardIds.first() == batman.id)
        assertTrue(entity.prizeCardIds.first() == flash.id)
    }

    @Test
    fun toModel() {
        val entity =
            PlayerEntity(
                id,
                Player(null, user, listOf(batman), listOf(flash))
            )

        val model = entity.toModel(user, listOf(batman), listOf(flash))
        assertEquals(id, model.id)
        assertEquals(user, model.user)
        assertTrue(model.availableCards.first() == batman)
        assertTrue(model.prizeCards.first() == flash)
    }
}