package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Stats
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class HumanEntityTest {

    private val id: Long = 0L
    private val userName: String = "userName"
    private val fullName: String = "fullName"
    private val password: String = "password"
    private val token: String = "token"
    private val winCount: Int = 1
    private val tieCount: Int = 0
    private val loseCount: Int = 2
    private val inProgressCount: Int = 4

    @Nested
    inner class ToEntity {

        @Test
        fun `Transform human model object to entity`() {
            val model =
                Human(id, userName, fullName, password, token, Stats(winCount, tieCount, loseCount, inProgressCount))

            val entity = HumanEntity(human = model)
            assertEquals(id, entity.id)
            assertEquals(userName, entity.userName)
            assertEquals(fullName, entity.fullName)
            assertEquals(password, entity.password)
            assertEquals(token, entity.token)
            assertEquals(winCount, entity.winCount)
            assertEquals(tieCount, entity.tieCount)
            assertEquals(loseCount, entity.loseCount)
            assertEquals(inProgressCount, entity.inProgressCount)
            assertFalse(entity.isAdmin)
        }

        @Test
        fun `Transform admin model object to entity`() {
            val model =
                Human(
                    id,
                    userName,
                    fullName,
                    password,
                    token,
                    Stats(winCount, tieCount, loseCount, inProgressCount),
                    isAdmin = true
                )

            val entity = HumanEntity(human = model)
            assertEquals(id, entity.id)
            assertEquals(userName, entity.userName)
            assertEquals(fullName, entity.fullName)
            assertEquals(password, entity.password)
            assertEquals(token, entity.token)
            assertEquals(winCount, entity.winCount)
            assertEquals(tieCount, entity.tieCount)
            assertEquals(loseCount, entity.loseCount)
            assertEquals(inProgressCount, entity.inProgressCount)
            assertTrue(entity.isAdmin)
        }

        @Test
        fun `Transform human model object to entity withOut id`() {
            val model =
                Human(null, userName, fullName, password, token, Stats(winCount, tieCount, loseCount, inProgressCount))

            val entity = HumanEntity(1L, model)
            assertEquals(1L, entity.id)
            assertEquals(userName, entity.userName)
            assertEquals(fullName, entity.fullName)
            assertEquals(password, entity.password)
            assertEquals(token, entity.token)
            assertEquals(winCount, entity.winCount)
            assertEquals(tieCount, entity.tieCount)
            assertEquals(loseCount, entity.loseCount)
            assertEquals(inProgressCount, entity.inProgressCount)
            assertFalse(entity.isAdmin)
        }

        @Test
        fun `Transform admin model object to entity withOut id`() {
            val model = Human(
                null,
                userName,
                fullName,
                password,
                token,
                Stats(winCount, tieCount, loseCount, inProgressCount),
                isAdmin = true
            )

            val entity = HumanEntity(1L, model)
            assertEquals(1L, entity.id)
            assertEquals(userName, entity.userName)
            assertEquals(fullName, entity.fullName)
            assertEquals(password, entity.password)
            assertEquals(token, entity.token)
            assertEquals(winCount, entity.winCount)
            assertEquals(tieCount, entity.tieCount)
            assertEquals(loseCount, entity.loseCount)
            assertEquals(inProgressCount, entity.inProgressCount)
            assertTrue(entity.isAdmin)
        }

    }

    @Nested
    inner class ToModel {

        @Test
        fun `Transform user entity object to model`() {
            val entity =
                HumanEntity(
                    id,
                    Human(
                        null,
                        userName,
                        fullName,
                        password,
                        token,
                        Stats(winCount, tieCount, loseCount, inProgressCount)
                    )
                )

            val model = entity.toModel()
            assertEquals(id, model.id)
            assertEquals(userName, model.userName)
            assertEquals(fullName, model.fullName)
            assertEquals(password, model.password)
            assertEquals(token, model.token)
            assertFalse(model.isAdmin)

            val stats = model.stats
            assertEquals(winCount, stats.winCount)
            assertEquals(tieCount, stats.tieCount)
            assertEquals(loseCount, stats.loseCount)
            assertEquals(inProgressCount, stats.inProgressCount)
        }

        @Test
        fun `Transform admin entity object to model`() {
            val entity =
                HumanEntity(
                    id,
                    Human(
                        null,
                        userName,
                        fullName,
                        password,
                        token,
                        Stats(winCount, tieCount, loseCount, inProgressCount),
                        isAdmin = true
                    )
                )

            val model = entity.toModel()
            assertEquals(id, model.id)
            assertEquals(userName, model.userName)
            assertEquals(fullName, model.fullName)
            assertEquals(password, model.password)
            assertEquals(token, model.token)
            assertTrue(model.isAdmin)

            val stats = model.stats
            assertEquals(winCount, stats.winCount)
            assertEquals(tieCount, stats.tieCount)
            assertEquals(loseCount, stats.loseCount)
            assertEquals(inProgressCount, stats.inProgressCount)
        }

    }

}