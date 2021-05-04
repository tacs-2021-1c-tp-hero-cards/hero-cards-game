package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.HashService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class UserIntegrationTest {

    private val userMapMock: HashMap<Long, User> = hashMapOf()
    private val userSessionMapMock: HashMap<String, Long> = hashMapOf()
    private val instance = UserIntegration(userMapMock, userSessionMapMock)

    private val userId = 0L
    private val userName = "userNameTest"
    private val fullName = "fullNameTest"
    private val password = "passwordTest"
    private val token = "tokenTest"

    @Test
    fun getAllUser() {
        val user = User(userId, userName, fullName, password, token)
        userMapMock[0L] = user

        val allUser = instance.getAllUser()
        assertEquals(1, allUser.size)

        val found = allUser.first()
        assertEquals(user, found)
    }

    @Test
    fun saveUser() {
        instance.saveUser(User(userName = userName, fullName = fullName, password = password, token = token))

        val allUsers = userMapMock.values.toList()
        assertEquals(1, allUsers.size)

        val foundUser = allUsers.first()
        assertEquals(userId, foundUser.id)
        assertEquals(userName, foundUser.userName)
        assertEquals(fullName, foundUser.fullName)
        assertEquals(password, foundUser.password)
        assertEquals(token, foundUser.token)
    }

    @Test
    fun getAllUserSession() {
        userSessionMapMock[token] = userId

        val allUserSession = instance.getAllUserSession()
        assertEquals(1, allUserSession.size)
        assertEquals(userId, allUserSession[token])
    }

    @Test
    fun addUserSession() {
        val user = User(id = userId, userName = userName, fullName = fullName, password = password)

        instance.addUserSession(user)

        assertEquals(1, userSessionMapMock.size)

        assertEquals(HashService.calculateToken(user), userSessionMapMock.keys.first())
        assertEquals(user.token, userSessionMapMock.keys.first())
        assertEquals(userId, userSessionMapMock.values.first())
    }

    @Test
    fun deleteUserSession(){
        val user = User(id = userId, userName = userName, fullName = fullName, password = password)
        val token = HashService.calculateToken(user)
        user.updateToken(token)
        userSessionMapMock[token] = userId
        userMapMock[userId] = user

        instance.deleteUserSession(user)
        assertEquals(0, userSessionMapMock.size)
        assertEquals(userId, userMapMock.keys.first())

        val userFound = userMapMock.values.first()
        assertEquals(userId, userFound.id)
        assertEquals(userName, userFound.userName)
        assertEquals(fullName, userFound.fullName)
        assertEquals(password, userFound.password)
        assertNull(userFound.token)
    }
}