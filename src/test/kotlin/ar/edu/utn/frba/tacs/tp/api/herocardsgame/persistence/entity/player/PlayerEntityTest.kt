package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.player

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Stats
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
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

    private val human =
        Human(id, userName, fullName, password, token, Stats(winCount, tieCount, loseCount, inProgressCount))
    private val ia =
        IA(id, userName, Stats(winCount, tieCount, loseCount, inProgressCount), IADifficulty.HARD)

    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    @Nested
    inner class ToEntity {

        @Test
        fun `Build entity with id and human`() {
            val model = Player(id, human, listOf(batman), listOf(flash))

            val entity = PlayerEntity(player = model)
            assertEquals(id, entity.id)
            assertEquals(id, entity.userId)
            assertEquals(UserType.HUMAN.name, entity.userType)
            assertTrue(entity.availableCardIds.first() == batman.id)
            assertTrue(entity.prizeCardIds.first() == flash.id)
        }

        @Test
        fun `Build entity with id and ia`() {
            val model = Player(id, ia, listOf(batman), listOf(flash))

            val entity = PlayerEntity(player = model)
            assertEquals(id, entity.id)
            assertEquals(id, entity.userId)
            assertEquals(UserType.IA.name, entity.userType)
            assertTrue(entity.availableCardIds.first() == batman.id)
            assertTrue(entity.prizeCardIds.first() == flash.id)
        }

        @Test
        fun `Build entity with human without id`() {
            val model = Player(null, human, listOf(batman), listOf(flash))

            val entity = PlayerEntity(1L, model)
            assertEquals(1L, entity.id)
            assertEquals(id, entity.userId)
            assertEquals(UserType.HUMAN.name, entity.userType)
            assertTrue(entity.availableCardIds.first() == batman.id)
            assertTrue(entity.prizeCardIds.first() == flash.id)
        }

        @Test
        fun `Build entity with ia without id`() {
            val model = Player(null, ia, listOf(batman), listOf(flash))

            val entity = PlayerEntity(1L, model)
            assertEquals(1L, entity.id)
            assertEquals(id, entity.userId)
            assertEquals(UserType.IA.name, entity.userType)
            assertTrue(entity.availableCardIds.first() == batman.id)
            assertTrue(entity.prizeCardIds.first() == flash.id)
        }
    }

    @Nested
    inner class ToModel{

        @Test
        fun `Build model with human`() {
            val entity =
                PlayerEntity(
                    id,
                    Player(null, human, listOf(batman), listOf(flash))
                )

            val model = entity.toModel(human, listOf(batman), listOf(flash))
            assertEquals(id, model.id)
            assertEquals(human, model.user)
            assertTrue(model.availableCards.first() == batman)
            assertTrue(model.prizeCards.first() == flash)
        }

        @Test
        fun `Build model with ia`() {
            val entity =
                PlayerEntity(
                    id,
                    Player(null, ia, listOf(batman), listOf(flash))
                )

            val model = entity.toModel(ia, listOf(batman), listOf(flash))
            assertEquals(id, model.id)
            assertEquals(ia, model.user)
            assertTrue(model.availableCards.first() == batman)
            assertTrue(model.prizeCards.first() == flash)
        }

    }

}