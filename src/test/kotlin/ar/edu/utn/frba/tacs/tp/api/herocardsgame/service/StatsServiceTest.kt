package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidUserTypeException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

internal class StatsServiceTest {

    private val userServiceMock = mock(UserIntegration::class.java)
    private val instance = StatsService(userServiceMock)

    private val human = Human(0L, "player", "fullName", "password")
    private val ia = IA(0L, "player", difficulty = IADifficulty.HARD)

    @Test
    fun `Can't build statistics with non-existent user type`(){
        assertThrows(InvalidUserTypeException::class.java) {
            instance.buildUserStats("0", "ROBOT")
        }
    }

    @Nested
    inner class BuildHumanStats {

        @Test
        fun `User not exist`() {
            `when`(userServiceMock.getHumanUserById(0L)).thenThrow(ElementNotFoundException::class.java)

            assertThrows(ElementNotFoundException::class.java) {
                instance.buildUserStats("0", UserType.HUMAN.name)
            }
        }

        @Test
        fun `User not play any match`() {
            `when`(userServiceMock.getHumanUserById(0L)).thenReturn(human)

            val stats = instance.buildUserStats("0", UserType.HUMAN.name)
            assertEquals(0, stats.inProgressCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.winCount)
            assertEquals(0, stats.totalPoint)
            assertEquals(UserType.HUMAN.name, stats.userType)
        }

        @Test
        fun `User plays multiple matches`() {
            `when`(userServiceMock.getHumanUserById(0L)).thenReturn(
                human.startMatch().loseMatch().loseMatch().tieMatch().winMatch().winMatch()
            )

            val stats = instance.buildUserStats("0", UserType.HUMAN.name)
            assertEquals(1, stats.inProgressCount)
            assertEquals(2, stats.loseCount)
            assertEquals(1, stats.tieCount)
            assertEquals(2, stats.winCount)
            assertEquals(7, stats.totalPoint)
            assertEquals(UserType.HUMAN.name, stats.userType)
        }

    }

    @Nested
    inner class BuildIAStats {

        @Test
        fun `User not exist`() {
            `when`(userServiceMock.getIAUserById(0L)).thenThrow(ElementNotFoundException::class.java)

            assertThrows(ElementNotFoundException::class.java) {
                instance.buildUserStats("0", UserType.IA.name)
            }
        }

        @Test
        fun `User not play any match`() {
            `when`(userServiceMock.getIAUserById(0L)).thenReturn(ia)

            val stats = instance.buildUserStats("0", UserType.IA.name)
            assertEquals(0, stats.inProgressCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.winCount)
            assertEquals(0, stats.totalPoint)
            assertEquals(UserType.IA.name, stats.userType)
        }

        @Test
        fun `User plays multiple matches`() {
            `when`(userServiceMock.getIAUserById(0L)).thenReturn(
                ia.startMatch().loseMatch().loseMatch().tieMatch().winMatch().winMatch()
            )

            val stats = instance.buildUserStats("0", UserType.IA.name)
            assertEquals(1, stats.inProgressCount)
            assertEquals(2, stats.loseCount)
            assertEquals(1, stats.tieCount)
            assertEquals(2, stats.winCount)
            assertEquals(7, stats.totalPoint)
            assertEquals(UserType.IA.name, stats.userType)
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
            assertEquals(1, firstStats.inProgressCount)
            assertEquals(2, firstStats.loseCount)
            assertEquals(1, firstStats.tieCount)
            assertEquals(2, firstStats.winCount)
            assertEquals(7, firstStats.totalPoint)
            assertEquals(UserType.IA.name, firstStats.userType)

            val secondStats = allStats[1]
            assertEquals(0, secondStats.inProgressCount)
            assertEquals(0, secondStats.loseCount)
            assertEquals(0, secondStats.tieCount)
            assertEquals(1, secondStats.winCount)
            assertEquals(3, secondStats.totalPoint)
            assertEquals(UserType.HUMAN.name, secondStats.userType)

            val lastStats = allStats.last()
            assertEquals(1, lastStats.inProgressCount)
            assertEquals(1, lastStats.loseCount)
            assertEquals(1, lastStats.tieCount)
            assertEquals(0, lastStats.winCount)
            assertEquals(1, lastStats.totalPoint)
            assertEquals(UserType.HUMAN.name, lastStats.userType)
        }

    }

}