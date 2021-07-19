package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

internal class StatsServiceTest {

    private val userServiceMock = mock(UserIntegration::class.java)
    private val instance = StatsService(userServiceMock)

    private val human = Human(0L, "humanName", "fullName", "password")
    private val ia = IA(0L, "iaName", difficulty = IADifficulty.HARD)

    @Nested
    inner class BuildHumanStats {

        @Test
        fun `User not exist`() {
            assertThrows(ElementNotFoundException::class.java) {
                instance.buildUserStats("0")
            }
        }

        @Test
        fun `User not play any match`() {
            `when`(userServiceMock.getUserById(0L)).thenReturn(human)

            val stats = instance.buildUserStats("0")
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
            `when`(userServiceMock.getUserById(0L)).thenReturn(
                human.startMatch().loseMatch().loseMatch().tieMatch().winMatch().winMatch()
            )

            val stats = instance.buildUserStats("0")
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
    inner class BuildIAStats {

        @Test
        fun `User not exist`() {
            `when`(userServiceMock.getUserById(0L)).thenThrow(ElementNotFoundException::class.java)

            assertThrows(ElementNotFoundException::class.java) {
                instance.buildUserStats("0")
            }
        }

        @Test
        fun `User not play any match`() {
            `when`(userServiceMock.getUserById(0L)).thenReturn(ia)

            val stats = instance.buildUserStats("0")
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
            `when`(userServiceMock.getUserById(0L)).thenReturn(
                ia.startMatch().loseMatch().loseMatch().tieMatch().winMatch().winMatch()
            )

            val stats = instance.buildUserStats("0")
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
    inner class BuildAllUserStats {

        @Test
        fun `Not user in system`() {
            `when`(userServiceMock.getAllUser()).thenReturn(emptyList())
            assertTrue(instance.buildAllUserStats().isEmpty())
        }

        @Test
        fun `Users plays multiple matches`() {
            `when`(userServiceMock.getAllUser()).thenReturn(
                listOf(
                    human.startMatch().loseMatch().tieMatch(),
                    human.winMatch(),
                    ia.startMatch().loseMatch().loseMatch().tieMatch().winMatch().winMatch()
                )
            )

            val allStats = instance.buildAllUserStats()
            assertEquals(3, allStats.size)

            val firstStats = allStats.first()
            assertEquals("iaName", firstStats.userName)
            assertEquals(1, firstStats.inProgressCount)
            assertEquals(2, firstStats.loseCount)
            assertEquals(1, firstStats.tieCount)
            assertEquals(2, firstStats.winCount)
            assertEquals(7, firstStats.totalPoint)
            assertEquals("IA", firstStats.userType)

            val secondStats = allStats[1]
            assertEquals("humanName", secondStats.userName)
            assertEquals(0, secondStats.inProgressCount)
            assertEquals(0, secondStats.loseCount)
            assertEquals(0, secondStats.tieCount)
            assertEquals(1, secondStats.winCount)
            assertEquals(3, secondStats.totalPoint)
            assertEquals("HUMAN", secondStats.userType)

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