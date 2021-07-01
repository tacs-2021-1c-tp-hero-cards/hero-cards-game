package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidUserException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class UserIntegrationTest {

    lateinit var dao: Dao
    lateinit var instance: UserIntegration

    private val userId = 0L
    private val userName = "userNameTest"
    private val fullName = "fullNameTest"
    private val password = "passwordTest"
    private val token = "tokenTest"

    private val user = User(id = userId, userName = userName, fullName = fullName, password = password)

    @BeforeEach
    fun init() {
        dao = Dao()
        instance = UserIntegration(dao)
    }

    @Nested
    inner class CreateUser {

        @Test
        fun `Create new user`() {
            val user = instance.createUser(userName, fullName, password)

            val allUser = dao.getAllUser().map { it.toModel() }
            assertEquals(1, allUser.size)
            assertTrue(allUser.contains(user))
        }

        @Test
        fun `Create new user if another user has same userName but different fullName`() {
            dao.saveUser(user.copy(fullName = "fullNameTest2"))

            val user = instance.createUser(userName, fullName, password)

            val allUser = dao.getAllUser().map { it.toModel() }
            assertEquals(2, allUser.size)
            assertTrue(allUser.contains(user))
        }

        @Test
        fun `Create new user if another user has same fullName but different userName`() {
            dao.saveUser(user.copy(userName = "userNameTest2"))

            val user = instance.createUser(userName, fullName, password)

            val allUser = dao.getAllUser().map { it.toModel() }
            assertEquals(2, allUser.size)
            assertTrue(allUser.contains(user))
        }

        @Test
        fun `Create user and it already exists`() {
            dao.saveUser(user)

            assertThrows(InvalidUserException::class.java) {
                instance.createUser(userName, fullName, password)
            }
        }

    }

    @Nested
    inner class ActivateUserSession {

        @Test
        fun `Activate session of an existing user`() {
            dao.saveUser(user)

            instance.activateUserSession(userName, password)

            val foundUser = dao.getAllUser().first().toModel()
            assertEquals(userId, foundUser.id)
            assertEquals(userName, foundUser.userName)
            assertEquals(fullName, foundUser.fullName)
            assertEquals(password, foundUser.password)
            assertNotNull(foundUser.token)

            val stats = foundUser.stats
            assertEquals(0, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

        @Test
        fun `Non activate section if only username matches`() {
            dao.saveUser(user.copy(password = "passwordTest2"))

            assertThrows(ElementNotFoundException::class.java) {
                instance.activateUserSession(userName, password)
            }
        }

        @Test
        fun `Non activate section if only password matches`() {
            dao.saveUser(user.copy(userName = "userNameTest2"))

            assertThrows(ElementNotFoundException::class.java) {
                instance.activateUserSession(userName, password)
            }
        }

        @Test
        fun `Non activate section if there are no users`() {
            assertThrows(ElementNotFoundException::class.java) {
                instance.activateUserSession(userName, password)
            }
        }

    }

    @Nested
    inner class DisableUserSession {

        @Test
        fun `Disable session of an existing user`() {
            dao.saveUser(user.copy(token = token))

            instance.disableUserSession(token)

            val allUser = dao.getAllUser()
            assertEquals(1, allUser.size)

            val foundUser = allUser.first().toModel()
            assertEquals(userId, foundUser.id)
            assertEquals(userName, foundUser.userName)
            assertEquals(fullName, foundUser.fullName)
            assertEquals(password, foundUser.password)
            assertNull(foundUser.token)

            val stats = foundUser.stats
            assertEquals(0, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

        @Test
        fun `Non disable section if token does not match`() {
            dao.saveUser(user.copy(token = "tokenTest2"))

            assertThrows(ElementNotFoundException::class.java) {
                instance.disableUserSession(token)
            }
        }

        @Test
        fun `Non disable section if there are no users`() {
            assertThrows(ElementNotFoundException::class.java) {
                instance.disableUserSession(token)
            }
        }

    }

    @Nested
    inner class GetUserById {

        @Test
        fun `Get user by id`() {
            dao.saveUser(user)

            val userById = instance.getUserById(userId)
            assertEquals(user, userById)
        }

        @Test
        fun `Get user by id but not exist`() {
            dao.saveUser(user.copy(id = 1L))

            assertThrows(ElementNotFoundException::class.java) {
                instance.getUserById(userId)
            }
        }

        @Test
        fun `Get user by id but no user exists in the system`() {
            assertThrows(ElementNotFoundException::class.java) {
                instance.getUserById(userId)
            }
        }

    }

    @Test
    fun getAllUser() {
        dao.saveUser(user)
        dao.saveUser(user.copy(id = 1L))

        val allUser = instance.getAllUser()
        assertEquals(2, allUser.size)
        assertTrue(allUser.contains(user))
    }

    @Test
    fun saveUser() {
        instance.saveUser(user.copy(token = token))

        val allUser = dao.getAllUser()
        assertEquals(1, dao.getAllUser().size)

        val foundUser = allUser.first().toModel()
        assertEquals(userId, foundUser.id)
        assertEquals(userName, foundUser.userName)
        assertEquals(fullName, foundUser.fullName)
        assertEquals(password, foundUser.password)
        assertEquals(token, foundUser.token)

        val stats = foundUser.stats
        assertEquals(0, stats.winCount)
        assertEquals(0, stats.tieCount)
        assertEquals(0, stats.loseCount)
        assertEquals(0, stats.inProgressCount)
    }

    @Nested
    inner class SearchUserByIdUserNameOrFullName {

        @Test
        fun `Search user by id and find one`() {
            dao.saveUser(user)

            val usersFound = instance.searchUserByIdUserNameOrFullName(id = userId.toString())

            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(user))
        }

        @Test
        fun `Search user by id and can't find any`() {
            dao.saveUser(user)

            val usersFound = instance.searchUserByIdUserNameOrFullName(id = "1")

            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by id and not users in system`() {
            val usersFound = instance.searchUserByIdUserNameOrFullName(id = "1")

            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by userName and find one`() {
            dao.saveUser(user)

            val usersFound = instance.searchUserByIdUserNameOrFullName(userName = "userNameTest")

            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(user))
        }

        @Test
        fun `Search user by userName and can't find any`() {
            dao.saveUser(user)

            val usersFound = instance.searchUserByIdUserNameOrFullName(userName = "userNameTest2")

            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by userName and not users in system`() {
            val usersFound = instance.searchUserByIdUserNameOrFullName(userName = "userNameTest2")

            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by fullName and find one`() {
            dao.saveUser(user)

            val usersFound = instance.searchUserByIdUserNameOrFullName(fullName = "fullNameTest")

            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(user))
        }

        @Test
        fun `Search user by fullName and can't find any`() {
            dao.saveUser(user)

            val usersFound = instance.searchUserByIdUserNameOrFullName(fullName = "fullNameTest2")

            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by fullName and not users in system`() {
            val usersFound = instance.searchUserByIdUserNameOrFullName(fullName = "fullNameTest2")

            assertTrue(usersFound.isEmpty())
        }

    }

}