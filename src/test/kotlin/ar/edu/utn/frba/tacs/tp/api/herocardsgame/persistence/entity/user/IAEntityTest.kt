package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Stats
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class IAEntityTest {

    private val id: Long = 0L
    private val userName: String = "userName"
    private val winCount: Int = 1
    private val tieCount: Int = 0
    private val loseCount: Int = 2
    private val inProgressCount: Int = 4
    private val iADifficulty = IADifficulty.HARD

    @Test
    fun toEntityWithId() {
        val model = IA(id, userName, Stats(winCount, tieCount, loseCount, inProgressCount), iADifficulty)

        val entity = IAEntity(ia = model)
        assertEquals(id, entity.id)
        assertEquals(userName, entity.userName)
        assertEquals(winCount, entity.winCount)
        assertEquals(tieCount, entity.tieCount)
        assertEquals(loseCount, entity.loseCount)
        assertEquals(inProgressCount, entity.inProgressCount)
        assertEquals(iADifficulty.name, entity.duelDifficulty)
    }

    @Test
    fun toEntityWithOutId() {
        val model =
            IA(null, userName, Stats(winCount, tieCount, loseCount, inProgressCount), iADifficulty)

        val entity = IAEntity(1L, model)
        assertEquals(1L, entity.id)
        assertEquals(userName, entity.userName)
        assertEquals(winCount, entity.winCount)
        assertEquals(tieCount, entity.tieCount)
        assertEquals(loseCount, entity.loseCount)
        assertEquals(inProgressCount, entity.inProgressCount)
        assertEquals(iADifficulty.name, entity.duelDifficulty)
    }

    @Test
    fun toModel() {
        val entity =
            IAEntity(
                id,
                IA(null, userName, Stats(winCount, tieCount, loseCount, inProgressCount), iADifficulty)
            )

        val model = entity.toModel()
        assertEquals(id, model.id)
        assertEquals(userName, model.userName)
        assertEquals(iADifficulty, model.difficulty)

        val stats = model.stats
        assertEquals(winCount, stats.winCount)
        assertEquals(tieCount, stats.tieCount)
        assertEquals(loseCount, stats.loseCount)
        assertEquals(inProgressCount, stats.inProgressCount)
    }

}