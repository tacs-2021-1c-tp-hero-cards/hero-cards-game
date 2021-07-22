package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user.UserEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user.UserFactory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository.UserRepository
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.StatsService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.context.annotation.Bean
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

internal class StatsControllerTest {

    private lateinit var repositoryMock: UserRepository
    private lateinit var instance: StatsController

    private fun userEntity(userType: UserType) =
        UserEntity(
            id = 0L,
            userName = "userName",
            userType = userType,
            winCount = 0,
            tieCount = 0,
            loseCount = 0,
            inProgressCount = 0,
            fullName = "fullName",
            password = "password",
            isAdmin = false,
            difficulty = IADifficulty.HARD
        )

    @BeforeEach
    fun init() {
        val context = AnnotationConfigWebApplicationContext()
        context.register(StatsControllerTest::class.java)
        context.register(StatsController::class.java)
        context.register(StatsService::class.java)
        context.register(UserIntegration::class.java)
        context.register(UserFactory::class.java)

        context.refresh()
        context.start()

        repositoryMock = context.getBean(UserRepository::class.java)
        instance = context.getBean(StatsController::class.java)
    }

    @Bean
    fun getUserRepository(): UserRepository = mock(UserRepository::class.java)

    @Nested
    inner class GetStatsByHuman {

        @Test
        fun `User not exist`() {
            `when`(repositoryMock.getById(0L)).thenReturn(null)

            val response = instance.getStatsByUser("0", "HUMAN")
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `User not play any match`() {
            `when`(repositoryMock.getById(0L)).thenReturn(userEntity(UserType.HUMAN))

            val response = instance.getStatsByUser("0", "HUMAN")
            assertEquals(200, response.statusCodeValue)
            val stats = response.body!!
            assertEquals("0", stats.id)
            assertEquals("userName", stats.userName)
            assertEquals(0, stats.inProgressCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.winCount)
            assertEquals(0, stats.totalPoint)
            assertEquals(UserType.HUMAN, stats.userType)
        }

        @Test
        fun `User plays multiple matches`() {
            `when`(repositoryMock.getById(0L)).thenReturn(
                userEntity(UserType.HUMAN).copy(
                    inProgressCount = 1,
                    loseCount = 2,
                    tieCount = 1,
                    winCount = 2
                )
            )

            val response = instance.getStatsByUser("0", "HUMAN")
            assertEquals(200, response.statusCodeValue)
            val stats = response.body!!
            assertEquals("0", stats.id)
            assertEquals("userName", stats.userName)
            assertEquals(1, stats.inProgressCount)
            assertEquals(2, stats.loseCount)
            assertEquals(1, stats.tieCount)
            assertEquals(2, stats.winCount)
            assertEquals(7, stats.totalPoint)
            assertEquals(UserType.HUMAN, stats.userType)
        }

    }

    @Nested
    inner class GetStatsByIA {

        @Test
        fun `User not exist`() {
            `when`(repositoryMock.getById(0L)).thenReturn(null)

            val response = instance.getStatsByUser("0", "IA")
            assertEquals(404, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `User not play any match`() {
            `when`(repositoryMock.getById(0L)).thenReturn(userEntity(UserType.IA))

            val response = instance.getStatsByUser("0", "IA")
            assertEquals(200, response.statusCodeValue)
            val stats = response.body!!
            assertEquals("0", stats.id)
            assertEquals("userName", stats.userName)
            assertEquals(0, stats.inProgressCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.winCount)
            assertEquals(0, stats.totalPoint)
            assertEquals(UserType.IA, stats.userType)
        }

        @Test
        fun `User plays multiple matches`() {
            `when`(repositoryMock.getById(0L)).thenReturn(
                userEntity(UserType.IA).copy(
                    inProgressCount = 1,
                    loseCount = 2,
                    tieCount = 1,
                    winCount = 2
                )
            )

            val response = instance.getStatsByUser("0", "IA")
            assertEquals(200, response.statusCodeValue)
            val stats = response.body!!
            assertEquals("0", stats.id)
            assertEquals("userName", stats.userName)
            assertEquals(1, stats.inProgressCount)
            assertEquals(2, stats.loseCount)
            assertEquals(1, stats.tieCount)
            assertEquals(2, stats.winCount)
            assertEquals(7, stats.totalPoint)
            assertEquals(UserType.IA, stats.userType)
        }

    }

    @Nested
    inner class GetScoreBoards {

        @Test
        fun `Not user in system`() {
            `when`(repositoryMock.findAll()).thenReturn(emptyList())

            val response = instance.getScoreBoards()
            assertEquals(200, response.statusCodeValue)
            assertTrue(response.body!!.isEmpty())
        }

        @Test
        fun `Users plays multiple matches`() {
            `when`(repositoryMock.findAll()).thenReturn(
                listOf(
                    userEntity(UserType.HUMAN).copy(
                        inProgressCount = 1,
                        loseCount = 1,
                        tieCount = 1
                    ),
                    userEntity(UserType.IA).copy(winCount = 1),
                    userEntity(UserType.HUMAN).copy(inProgressCount = 1, loseCount = 2, tieCount = 1, winCount = 2)
                )
            )

            val response = instance.getScoreBoards()
            assertEquals(200, response.statusCodeValue)

            val allStats = response.body!!
            assertEquals(3, allStats.size)

            val firstStats = allStats.first()
            assertEquals("userName", firstStats.userName)
            assertEquals(1, firstStats.inProgressCount)
            assertEquals(2, firstStats.loseCount)
            assertEquals(1, firstStats.tieCount)
            assertEquals(2, firstStats.winCount)
            assertEquals(7, firstStats.totalPoint)
            assertEquals(UserType.HUMAN, firstStats.userType)

            val secondStats = allStats[1]
            assertEquals("userName", secondStats.userName)
            assertEquals(0, secondStats.inProgressCount)
            assertEquals(0, secondStats.loseCount)
            assertEquals(0, secondStats.tieCount)
            assertEquals(1, secondStats.winCount)
            assertEquals(3, secondStats.totalPoint)
            assertEquals(UserType.IA, secondStats.userType)

            val lastStats = allStats.last()
            assertEquals("userName", lastStats.userName)
            assertEquals(1, lastStats.inProgressCount)
            assertEquals(1, lastStats.loseCount)
            assertEquals(1, lastStats.tieCount)
            assertEquals(0, lastStats.winCount)
            assertEquals(1, lastStats.totalPoint)
            assertEquals(UserType.HUMAN, lastStats.userType)
        }

    }
}