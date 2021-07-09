package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidDifficultyException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidHumanUserException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidIAUserException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
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
    private val difficulty = IADifficulty.HARD

    private val human = Human(id = userId, userName = userName, fullName = fullName, password = password, token = token)
    private val ia = IA(id = userId, userName = userName, difficulty = difficulty)

    @BeforeEach
    fun init() {
        dao = Dao()
        instance = UserIntegration(dao)
    }

    @Nested
    inner class CreateHuman {

        @Test
        fun `Create new human`() {
            val user = instance.createUser(userName, fullName, false, password)

            val allUser = dao.getAllHuman().map { it.toModel() }
            assertEquals(1, allUser.size)

            val userFound = allUser.first()
            assertEquals(user, userFound)
            assertFalse(userFound.isAdmin)

        }

        @Test
        fun `Create new admin`() {
            val user = instance.createUser(userName, fullName, true, password)

            val allUser = dao.getAllHuman().map { it.toModel() }
            assertEquals(1, allUser.size)

            val userFound = allUser.first()
            assertEquals(user, userFound)
            assertTrue(userFound.isAdmin)
        }

        @Test
        fun `Create new human if another human has same userName but different fullName`() {
            dao.saveHuman(human.copy(fullName = "fullNameTest2"))

            val user = instance.createUser(userName, fullName, false, password)

            val allUser = dao.getAllHuman().map { it.toModel() }
            assertEquals(2, allUser.size)
            assertTrue(allUser.contains(user))
        }

        @Test
        fun `Create new human if another human has same fullName but different userName`() {
            dao.saveHuman(human.copy(userName = "userNameTest2"))

            val user = instance.createUser(userName, fullName, false, password)

            val allUser = dao.getAllHuman().map { it.toModel() }
            assertEquals(2, allUser.size)
            assertTrue(allUser.contains(user))
        }

        @Test
        fun `Create human and it already exists`() {
            dao.saveHuman(human)

            assertThrows(InvalidHumanUserException::class.java) {
                instance.createUser(userName, fullName, false, password)
            }
        }

    }

    @Nested
    inner class CreateIA {

        @Test
        fun `Create new ia`() {
            val user = instance.createUser(userName, difficulty.name)

            val allUser = dao.getAllIA().map { it.toModel() }
            assertEquals(1, allUser.size)
            assertTrue(allUser.contains(user))
        }

        @Test
        fun `Create ia and it already exists`() {
            dao.saveIA(ia)

            assertThrows(InvalidIAUserException::class.java) {
                instance.createUser(userName, difficulty.name)
            }
        }

        @Test
        fun `Create ia with non exist difficulty`() {
            assertThrows(InvalidDifficultyException::class.java) {
                instance.createUser(userName, "HARDY")
            }
        }

    }

    @Nested
    inner class ActivateHumanSession {

        @Test
        fun `Activate session of an existing user`() {
            dao.saveHuman(human)

            instance.activateUserSession(userName, password)

            val foundUser = dao.getAllHuman().first().toModel()
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
            dao.saveHuman(human.copy(password = "passwordTest2"))

            assertThrows(ElementNotFoundException::class.java) {
                instance.activateUserSession(userName, password)
            }
        }

        @Test
        fun `Non activate section if only password matches`() {
            dao.saveHuman(human.copy(userName = "userNameTest2"))

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
    inner class DisableHumanSession {

        @Test
        fun `Disable session of an existing user`() {
            dao.saveHuman(human.copy(token = token))

            instance.disableUserSession(token)

            val allUser = dao.getAllHuman()
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
            dao.saveHuman(human.copy(token = "tokenTest2"))

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
    inner class GetHumanById {

        @Test
        fun `Get human by id`() {
            dao.saveHuman(human)

            val userById = instance.getUserById(userId, UserType.HUMAN)
            assertEquals(human, userById)
        }

        @Test
        fun `Get human by id but not exist`() {
            dao.saveHuman(human.copy(id = 1L))

            assertThrows(ElementNotFoundException::class.java) {
                instance.getUserById(userId, UserType.HUMAN)
            }
        }

        @Test
        fun `Get human by id but no human exists in the system`() {
            assertThrows(ElementNotFoundException::class.java) {
                instance.getUserById(userId, UserType.HUMAN)
            }
        }

    }

    @Nested
    inner class GetIAById {

        @Test
        fun `Get ia by id`() {
            dao.saveIA(ia)

            val userById = instance.getUserById(userId, UserType.IA)
            assertEquals(ia, userById)
        }

        @Test
        fun `Get ia by id but not exist`() {
            dao.saveIA(ia.copy(id = 1L))

            assertThrows(ElementNotFoundException::class.java) {
                instance.getUserById(userId, UserType.IA)
            }
        }

        @Test
        fun `Get ia by id but no ia exists in the system`() {
            assertThrows(ElementNotFoundException::class.java) {
                instance.getUserById(userId, UserType.IA)
            }
        }

    }

    @Test
    fun getAllUser() {
        dao.saveHuman(human)
        dao.saveHuman(human.copy(id = 1L))
        dao.saveIA(ia)
        dao.saveIA(ia.copy(id = 1L))

        val allUser = instance.getAllUser()
        assertEquals(4, allUser.size)
        assertTrue(allUser.contains(human))
        assertTrue(allUser.contains(ia))
    }

    @Nested
    inner class SaveUser {

        @Test
        fun `Save user type human`() {
            instance.saveUser(human.copy(token = token))

            val allUser = dao.getAllHuman()
            assertEquals(1, dao.getAllHuman().size)

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

        @Test
        fun `Save user type ia`() {
            instance.saveUser(ia)

            val allUser = dao.getAllIA()
            assertEquals(1, dao.getAllIA().size)

            val foundUser = allUser.first().toModel()
            assertEquals(userId, foundUser.id)
            assertEquals(userName, foundUser.userName)
            assertEquals(difficulty, foundUser.difficulty)

            val stats = foundUser.stats
            assertEquals(0, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

    }

    @Nested
    inner class SearchHumanUserByIdUserNameFullNameOrToken {

        @Test
        fun `Search user by id and find one`() {
            dao.saveHuman(human)

            val usersFound = instance.searchHumanUserByIdUserNameFullNameOrToken(id = userId.toString())

            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(human))
        }

        @Test
        fun `Search user by id and can't find any`() {
            dao.saveHuman(human)

            val usersFound = instance.searchHumanUserByIdUserNameFullNameOrToken(id = "1")

            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by id and not users in system`() {
            val usersFound = instance.searchHumanUserByIdUserNameFullNameOrToken(id = "1")

            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by userName and find one`() {
            dao.saveHuman(human)

            val usersFound = instance.searchHumanUserByIdUserNameFullNameOrToken(userName = "userNameTest")

            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(human))
        }

        @Test
        fun `Search user by userName ignoreCase and find one`() {
            dao.saveHuman(human)

            val usersFound = instance.searchHumanUserByIdUserNameFullNameOrToken(userName = "USERNAMETEST")

            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(human))
        }

        @Test
        fun `Search user by userName and can't find any`() {
            dao.saveHuman(human)

            val usersFound = instance.searchHumanUserByIdUserNameFullNameOrToken(userName = "userNameTest2")

            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by userName and not users in system`() {
            val usersFound = instance.searchHumanUserByIdUserNameFullNameOrToken(userName = "userNameTest2")

            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by fullName and find one`() {
            dao.saveHuman(human)

            val usersFound = instance.searchHumanUserByIdUserNameFullNameOrToken(fullName = "fullNameTest")

            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(human))
        }

        @Test
        fun `Search user by fullName ignoreCase and find one`() {
            dao.saveHuman(human)

            val usersFound = instance.searchHumanUserByIdUserNameFullNameOrToken(fullName = "FULLNAMETEST")

            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(human))
        }

        @Test
        fun `Search user by fullName and can't find any`() {
            dao.saveHuman(human)

            val usersFound = instance.searchHumanUserByIdUserNameFullNameOrToken(fullName = "fullNameTest2")

            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by fullName and not users in system`() {
            val usersFound = instance.searchHumanUserByIdUserNameFullNameOrToken(fullName = "fullNameTest2")

            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by token and find one`() {
            dao.saveHuman(human)

            val usersFound = instance.searchHumanUserByIdUserNameFullNameOrToken(token = token.toString())

            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(human))
        }

        @Test
        fun `Search user by token and can't find any`() {
            dao.saveHuman(human)

            val usersFound = instance.searchHumanUserByIdUserNameFullNameOrToken(token = "token-test")

            assertTrue(usersFound.isEmpty())
        }

    }

    @Nested
    inner class SearchIAUserByIdUserNameFullNameOrToken {

        @Test
        fun `Search user by id and find one`() {
            dao.saveIA(ia)

            val usersFound = instance.searchIAUserByIdUserNameFullNameOrToken(id = userId.toString())

            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(ia))
        }

        @Test
        fun `Search user by id and can't find any`() {
            dao.saveIA(ia)

            val usersFound = instance.searchIAUserByIdUserNameFullNameOrToken(id = "1")

            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by id and not users in system`() {
            val usersFound = instance.searchIAUserByIdUserNameFullNameOrToken(id = "1")

            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by userName and find one`() {
            dao.saveIA(ia)

            val usersFound = instance.searchIAUserByIdUserNameFullNameOrToken(userName = "userNameTest")

            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(ia))
        }

        @Test
        fun `Search user by userName ignoreCase and find one`() {
            dao.saveIA(ia)

            val usersFound = instance.searchIAUserByIdUserNameFullNameOrToken(userName = "USERNAMETEST")

            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(ia))
        }

        @Test
        fun `Search user by userName and can't find any`() {
            dao.saveIA(ia)

            val usersFound = instance.searchIAUserByIdUserNameFullNameOrToken(userName = "userNameTest2")

            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by userName and not users in system`() {
            val usersFound = instance.searchIAUserByIdUserNameFullNameOrToken(userName = "userNameTest2")

            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by difficulty and find one`() {
            dao.saveIA(ia)

            val usersFound = instance.searchIAUserByIdUserNameFullNameOrToken(difficulty = "HARD")

            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(ia))
        }

        @Test
        fun `Search user by difficulty ignoreCase and find one`() {
            dao.saveIA(ia)

            val usersFound = instance.searchIAUserByIdUserNameFullNameOrToken(difficulty = "hard")

            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(ia))
        }

        @Test
        fun `Search user by difficulty and can't find any`() {
            dao.saveIA(ia)

            val usersFound = instance.searchIAUserByIdUserNameFullNameOrToken(difficulty = "hards")

            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by difficulty and not users in system`() {
            val usersFound = instance.searchIAUserByIdUserNameFullNameOrToken(difficulty = "HARD")

            assertTrue(usersFound.isEmpty())
        }

    }

}