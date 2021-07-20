package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidDifficultyException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidHumanUserException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidIAUserException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user.UserEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user.UserFactory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository.UserRepository
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class UserIntegrationTest {

    private val repositoryMock = mock(UserRepository::class.java)
    private val instance = UserIntegration(UserFactory(), repositoryMock)

    private val userName = "userNameTest"
    private val fullName = "fullNameTest"
    private val password = "passwordTest"
    private val token = "tokenTest"
    private val difficulty = IADifficulty.HARD

    private val human =
        Human(userName = userName, fullName = fullName, password = password, token = token, isAdmin = false)
    private val humanUserEntity =
        UserEntity(
            userName = userName,
            userType = UserType.HUMAN.name,
            winCount = 0,
            tieCount = 0,
            loseCount = 0,
            inProgressCount = 0,
            fullName = fullName,
            password = password,
            token = token,
            isAdmin = false
        )

    private val ia = IA(userName = userName, difficulty = difficulty)
    private val iaUserEntity =
        UserEntity(
            userName = userName,
            userType = UserType.IA.name,
            winCount = 0,
            tieCount = 0,
            loseCount = 0,
            inProgressCount = 0,
            difficulty = difficulty.name
        )

    @Nested
    inner class CreateHuman {

        @Test
        fun `Create human and it already exists`() {
            `when`(repositoryMock.findHumanByIdAndUserNameAndFullNameAndToken(userName = userName, fullName = fullName))
                .thenReturn(listOf(humanUserEntity))

            assertThrows(InvalidHumanUserException::class.java) {
                instance.createUser(userName, fullName, false, password)
            }
        }

        @Test
        fun `Create new human`() {
            val newHuman = humanUserEntity.copy(isAdmin = false, token = null)

            `when`(repositoryMock.findHumanByIdAndUserNameAndFullNameAndToken(userName = userName, fullName = fullName))
                .thenReturn(emptyList())
            `when`(repositoryMock.save(newHuman)).thenReturn(newHuman)

            instance.createUser(userName, fullName, false, password)
            verify(repositoryMock, times(1)).save(newHuman)
        }

        @Test
        fun `Create new admin`() {
            val newHuman = humanUserEntity.copy(isAdmin = true, token = null)

            `when`(repositoryMock.findHumanByIdAndUserNameAndFullNameAndToken(userName = userName, fullName = fullName))
                .thenReturn(emptyList())
            `when`(repositoryMock.save(newHuman)).thenReturn(newHuman)

            instance.createUser(userName, fullName, true, password)
            verify(repositoryMock, times(1)).save(newHuman)
        }

    }

    @Nested
    inner class CreateIA {

        @Test
        fun `Create ia and it already exists`() {
            `when`(repositoryMock.findIAByIdAndUserNameAndDifficulty(userName = userName, difficulty = difficulty.name))
                .thenReturn(listOf(iaUserEntity))

            assertThrows(InvalidIAUserException::class.java) {
                instance.createUser(userName, difficulty.name)
            }
        }

        @Test
        fun `Create ia with non exist difficulty`() {
            `when`(repositoryMock.findIAByIdAndUserNameAndDifficulty(userName = userName, difficulty = "HARDY"))
                .thenReturn(emptyList())

            assertThrows(InvalidDifficultyException::class.java) {
                instance.createUser(userName, "HARDY")
            }
        }

        @Test
        fun `Create new ia`() {
            `when`(repositoryMock.findIAByIdAndUserNameAndDifficulty(userName = userName, difficulty = difficulty.name))
                .thenReturn(emptyList())
            `when`(repositoryMock.save(iaUserEntity)).thenReturn(iaUserEntity)

            instance.createUser(userName, difficulty.name)
            verify(repositoryMock, times(1)).save(iaUserEntity)
        }

    }

    @Nested
    inner class ActivateHumanSession {

        @Test
        fun `Non activate section if there are no users`() {
            `when`(repositoryMock.findHumanByUserNameAndPassword(userName, password)).thenReturn(null)

            assertThrows(ElementNotFoundException::class.java) {
                instance.activateUserSession(userName, password)
            }
        }

    }

    @Nested
    inner class DisableHumanSession {

        @Test
        fun `Non disable section if token does not match`() {
            `when`(repositoryMock.findHumanByToken(token)).thenReturn(null)

            assertThrows(ElementNotFoundException::class.java) {
                instance.disableUserSession(token)
            }
        }

        @Test
        fun `Disable session of an existing user`() {
            val newHuman = humanUserEntity.copy(token = null)
            `when`(repositoryMock.findHumanByToken(token)).thenReturn(humanUserEntity)
            `when`(repositoryMock.save(newHuman)).thenReturn(newHuman)

            instance.disableUserSession(token)
            verify(repositoryMock, times(1)).save(newHuman)
        }

    }

    @Nested
    inner class GetUserById {

        @Test
        fun `Get human by id`() {
            `when`(repositoryMock.getById(0L)).thenReturn(humanUserEntity)

            val userById = instance.getUserById(0L)
            assertEquals(human, userById)
        }

        @Test
        fun `Get ia by id`() {
            `when`(repositoryMock.getById(0L)).thenReturn(iaUserEntity)

            val userById = instance.getUserById(0L)
            assertEquals(ia, userById)
        }

        @Test
        fun `Get user by id but not exist`() {
            assertThrows(ElementNotFoundException::class.java) {
                instance.getUserById(100)
            }
        }

    }

    @Test
    fun getAllUser() {
        `when`(repositoryMock.findAll()).thenReturn(listOf(humanUserEntity, iaUserEntity))

        val allUser = instance.getAllUser()
        assertEquals(2, allUser.size)
        assertTrue(allUser.contains(human))
        assertTrue(allUser.contains(ia))
    }

}