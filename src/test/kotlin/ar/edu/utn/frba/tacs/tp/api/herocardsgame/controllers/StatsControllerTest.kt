package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.StatsService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.context.annotation.Bean
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

internal class StatsControllerTest {

    private lateinit var dao: Dao
    private lateinit var instance: StatsController

    private var user = User(userName = "userName", fullName = "fullName", password = "password")

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
    inner class GetStatsByUser {

        @Test
        fun `User not exist`() {
            val response = instance.getStatsByUser("0")
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `User not play any match`() {
            dao.saveUser(user)

            val response = instance.getStatsByUser("0")
            assertEquals(200, response.statusCodeValue)
            val stats = response.body!!
            assertEquals("0", stats.id)
            assertEquals(0, stats.inProgressCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.winCount)
            assertEquals(0, stats.totalPoint)
        }

        @Test
        fun `User plays multiple matches`() {
            dao.saveUser(user.startMatch().loseMatch().loseMatch().tieMatch().winMatch().winMatch())

            val response = instance.getStatsByUser("0")
            assertEquals(200, response.statusCodeValue)
            val stats = response.body!!
            assertEquals("0", stats.id)
            assertEquals(1, stats.inProgressCount)
            assertEquals(2, stats.loseCount)
            assertEquals(1, stats.tieCount)
            assertEquals(2, stats.winCount)
            assertEquals(7, stats.totalPoint)
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
            dao.saveUser(user.startMatch().loseMatch().tieMatch())
            dao.saveUser(user.winMatch())
            dao.saveUser(user.startMatch().loseMatch().loseMatch().tieMatch().winMatch().winMatch())

            val response = instance.getScoreBoards()
            assertEquals(200, response.statusCodeValue)

            val allStats = response.body!!
            assertEquals(3, allStats.size)

            val firstStats = allStats.first()
            assertEquals(1, firstStats.inProgressCount)
            assertEquals(2, firstStats.loseCount)
            assertEquals(1, firstStats.tieCount)
            assertEquals(2, firstStats.winCount)
            assertEquals(7, firstStats.totalPoint)

            val secondStats = allStats[1]
            assertEquals(0, secondStats.inProgressCount)
            assertEquals(0, secondStats.loseCount)
            assertEquals(0, secondStats.tieCount)
            assertEquals(1, secondStats.winCount)
            assertEquals(3, secondStats.totalPoint)

            val lastStats = allStats.last()
            assertEquals(1, lastStats.inProgressCount)
            assertEquals(1, lastStats.loseCount)
            assertEquals(1, lastStats.tieCount)
            assertEquals(0, lastStats.winCount)
            assertEquals(1, lastStats.totalPoint)
        }

    }
}