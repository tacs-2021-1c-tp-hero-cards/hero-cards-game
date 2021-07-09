package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.StatsService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

internal class StatsControllerTest {

    private lateinit var dao: Dao
    private lateinit var instance: StatsController

    private var human = Human(userName = "humanName", fullName = "fullName", password = "password")
    private var ia = IA(userName = "iaName", difficulty = IADifficulty.HARD)

    @BeforeEach
    fun init() {
        val context = AnnotationConfigWebApplicationContext()
        context.register(StatsControllerTest::class.java)
        context.register(StatsController::class.java)
        context.register(StatsService::class.java)
        context.register(UserIntegration::class.java)
        context.register(Dao::class.java)

        context.refresh()
        context.start()

        dao = context.getBean(Dao::class.java)
        instance = context.getBean(StatsController::class.java)
    }

    @Nested
    inner class GetStatsByHuman {

        @Test
        fun `User not exist`() {
            val response = instance.getStatsByUser("0", "HUMAN")
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `User not play any match`() {
            dao.saveHuman(human)

            val response = instance.getStatsByUser("0", "HUMAN")
            assertEquals(200, response.statusCodeValue)
            val stats = response.body!!
            assertEquals("0", stats.id)
            assertEquals("humanName", stats.userName)
            assertEquals(0, stats.inProgressCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.winCount)
            assertEquals(0, stats.totalPoint)
            assertEquals("HUMAN", stats.userType)
        }

        @Test
        fun `User plays multiple matches`() {
            dao.saveHuman(human.startMatch().loseMatch().loseMatch().tieMatch().winMatch().winMatch())

            val response = instance.getStatsByUser("0", "HUMAN")
            assertEquals(200, response.statusCodeValue)
            val stats = response.body!!
            assertEquals("0", stats.id)
            assertEquals("humanName", stats.userName)
            assertEquals(1, stats.inProgressCount)
            assertEquals(2, stats.loseCount)
            assertEquals(1, stats.tieCount)
            assertEquals(2, stats.winCount)
            assertEquals(7, stats.totalPoint)
            assertEquals("HUMAN", stats.userType)
        }

    }

    @Nested
    inner class GetStatsByIA {

        @Test
        fun `User not exist`() {
            val response = instance.getStatsByUser("0", "IA")
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `User not play any match`() {
            dao.saveIA(ia)

            val response = instance.getStatsByUser("0", "IA")
            assertEquals(200, response.statusCodeValue)
            val stats = response.body!!
            assertEquals("0", stats.id)
            assertEquals("iaName", stats.userName)
            assertEquals(0, stats.inProgressCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.winCount)
            assertEquals(0, stats.totalPoint)
            assertEquals("IA", stats.userType)
        }

        @Test
        fun `User plays multiple matches`() {
            dao.saveIA(ia.startMatch().loseMatch().loseMatch().tieMatch().winMatch().winMatch())

            val response = instance.getStatsByUser("0", "IA")
            assertEquals(200, response.statusCodeValue)
            val stats = response.body!!
            assertEquals("0", stats.id)
            assertEquals("iaName", stats.userName)
            assertEquals(1, stats.inProgressCount)
            assertEquals(2, stats.loseCount)
            assertEquals(1, stats.tieCount)
            assertEquals(2, stats.winCount)
            assertEquals(7, stats.totalPoint)
            assertEquals("IA", stats.userType)
        }

    }

    @Nested
    inner class GetScoreBoards {

        @Test
        fun `Not user in system`() {
            val response = instance.getScoreBoards()
            assertEquals(200, response.statusCodeValue)
            assertTrue(response.body!!.isEmpty())
        }

        @Test
        fun `Users plays multiple matches`() {
            dao.saveHuman(human.startMatch().loseMatch().tieMatch())
            dao.saveIA(ia.winMatch())
            dao.saveHuman(human.startMatch().loseMatch().loseMatch().tieMatch().winMatch().winMatch())

            val response = instance.getScoreBoards()
            assertEquals(200, response.statusCodeValue)

            val allStats = response.body!!
            assertEquals(3, allStats.size)

            val firstStats = allStats.first()
            assertEquals("humanName", firstStats.userName)
            assertEquals(1, firstStats.inProgressCount)
            assertEquals(2, firstStats.loseCount)
            assertEquals(1, firstStats.tieCount)
            assertEquals(2, firstStats.winCount)
            assertEquals(7, firstStats.totalPoint)
            assertEquals("HUMAN", firstStats.userType)

            val secondStats = allStats[1]
            assertEquals("iaName", secondStats.userName)
            assertEquals(0, secondStats.inProgressCount)
            assertEquals(0, secondStats.loseCount)
            assertEquals(0, secondStats.tieCount)
            assertEquals(1, secondStats.winCount)
            assertEquals(3, secondStats.totalPoint)
            assertEquals("IA", secondStats.userType)

            val lastStats = allStats.last()
            assertEquals("humanName", lastStats.userName)
            assertEquals(1, lastStats.inProgressCount)
            assertEquals(1, lastStats.loseCount)
            assertEquals(1, lastStats.tieCount)
            assertEquals(0, lastStats.winCount)
            assertEquals(1, lastStats.totalPoint)
            assertEquals("HUMAN", lastStats.userType)
        }

    }
}