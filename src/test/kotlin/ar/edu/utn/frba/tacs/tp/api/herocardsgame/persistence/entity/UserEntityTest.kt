package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Stats
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class UserEntityTest {

    private val id: Long = 0L
    private val userName: String = "userName"
    private val fullName: String = "fullName"
    private val password: String = "password"
    private val token: String = "token"
    private val winCount: Int = 1
    private val tieCount: Int = 0
    private val loseCount: Int = 2
    private val inProgressCount: Int = 4

    @Test
    fun toEntityWithId() {
        val model = User(id, userName, fullName, password, token, Stats(winCount, tieCount, loseCount, inProgressCount))

        val entity = UserEntity(user = model)
        assertEquals(id, entity.id)
        assertEquals(userName, entity.userName)
        assertEquals(fullName, entity.fullName)
        assertEquals(password, entity.password)
        assertEquals(token, entity.token)
        assertEquals(winCount, entity.winCount)
        assertEquals(tieCount, entity.tieCount)
        assertEquals(loseCount, entity.loseCount)
        assertEquals(inProgressCount, entity.inProgressCount)
    }

    @Test
    fun toEntityWithOutId() {
        val model = User(null, userName, fullName, password, token, Stats(winCount, tieCount, loseCount, inProgressCount))

        val entity = UserEntity(1L, model)
        assertEquals(1L, entity.id)
        assertEquals(userName, entity.userName)
        assertEquals(fullName, entity.fullName)
        assertEquals(password, entity.password)
        assertEquals(token, entity.token)
        assertEquals(winCount, entity.winCount)
        assertEquals(tieCount, entity.tieCount)
        assertEquals(loseCount, entity.loseCount)
        assertEquals(inProgressCount, entity.inProgressCount)
    }

    @Test
    fun toModel() {
        val entity =
            UserEntity(
                id,
                User(null, userName, fullName, password, token, Stats(winCount, tieCount, loseCount, inProgressCount))
            )

        val model = entity.toModel()
        assertEquals(id, model.id)
        assertEquals(userName, model.userName)
        assertEquals(fullName, model.fullName)
        assertEquals(password, model.password)
        assertEquals(token, model.token)

        val stats = model.stats
        assertEquals(winCount, stats.winCount)
        assertEquals(tieCount, stats.tieCount)
        assertEquals(loseCount, stats.loseCount)
        assertEquals(inProgressCount, stats.inProgressCount)
    }

}