package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidUserException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class UserServiceTest {

    private val userIntegrationMock = mock(UserIntegration::class.java)
    private val instance = UserService(userIntegrationMock)

    private val userId = 0L
    private val userName = "userNameTest"
    private val fullName = "fullNameTest"
    private val password = "passwordTest"
    private val token = "tokenTest"

    @Test
    fun createUser() {
        `when`(userIntegrationMock.calculateId()).thenReturn(userId)

        instance.createUser(userName, fullName, password)

        verify(userIntegrationMock, times(1))
            .saveUser(User(userName = userName, fullName = fullName, password = password))
    }

    @Test
    fun createUserExisting() {
        `when`(userIntegrationMock.getAllUser())
            .thenReturn(listOf(User(userName = userName, fullName = fullName, password = password)))

        assertThrows(InvalidUserException::class.java) {
            instance.createUser(userName, fullName, password)
        }
    }

    @Test
    fun searchUser() {
        val userIdOk = User(userId, "null", "null", "null", "null")
        val userNameOk = User(1L, userName, "null", "null", "null")
        val fullNameOk = User(1L, "null", fullName, "null", "null")
        val passwordOk = User(1L, "null", "null", password, "null")
        val tokenOk = User(1L, "null", "null", "null", token)

        `when`(userIntegrationMock.getAllUser())
            .thenReturn(listOf(userIdOk, userNameOk, fullNameOk, passwordOk, tokenOk))

        assertEquals(userIdOk, instance.searchUser(id = userId).first())
        assertEquals(userNameOk, instance.searchUser(userName = userName).first())
        assertEquals(fullNameOk, instance.searchUser(fullName = fullName).first())
        assertEquals(passwordOk, instance.searchUser(password = password).first())
        assertEquals(tokenOk, instance.searchUser(token = token).first())
    }

    @Test
    fun searchUserById() {
        val user = User(userId, "null", "null", "null", "null")
        `when`(userIntegrationMock.getAllUser()).thenReturn(listOf(user))
        assertEquals(user, instance.searchUserById(userId.toString()))
    }

    @Test
    fun searchUserById_emptyUser() {
        `when`(userIntegrationMock.getAllUser()).thenReturn(emptyList())

        assertThrows(ElementNotFoundException::class.java) {
            instance.searchUserById(userId.toString())
        }
    }

    @Test
    fun activateUserSession() {
        `when`(userIntegrationMock.getAllUser())
            .thenReturn(listOf(User(userName = userName, fullName = fullName, password = password)))

        instance.activateUserSession(userName, password)

        verify(userIntegrationMock, times(1))
            .addUserSession(User(userName = userName, fullName = fullName, password = password))
    }

    @Test
    fun activateUserSessionNonExisting() {
        `when`(userIntegrationMock.getAllUser()).thenReturn(emptyList())

        assertThrows(ElementNotFoundException::class.java) {
            instance.activateUserSession(userName, password)
        }
    }

    @Test
    fun deactivateUserSession() {
        val user = User(userName = userName, fullName = fullName, password = password, token = token)
        `when`(userIntegrationMock.getAllUser())
            .thenReturn(listOf(user))

        instance.deactivateUserSession(token)

        verify(userIntegrationMock, times(1)).deleteUserSession(user)
    }

    @Test
    fun deactivateUserSessionNonExisting() {
        `when`(userIntegrationMock.getAllUser()).thenReturn(emptyList())

        assertThrows(ElementNotFoundException::class.java) {
            instance.deactivateUserSession(token)
        }
    }
}