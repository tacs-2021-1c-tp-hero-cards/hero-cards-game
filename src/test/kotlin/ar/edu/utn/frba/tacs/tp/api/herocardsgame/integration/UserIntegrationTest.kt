package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidDifficultyException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidHumanUserException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidIAUserException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user.UserFactory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository.UserRepository
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@DataJpaTest
internal class UserIntegrationTest {

    lateinit var instance: UserIntegration
    lateinit var factory: UserFactory

    @Autowired
    lateinit var repository: UserRepository
    
    private val userName = "userNameTest"
    private val fullName = "fullNameTest"
    private val password = "passwordTest"
    private val token = "tokenTest"
    private val difficulty = IADifficulty.HARD

    private val human =
        Human(id = 1L, userName = userName, fullName = fullName, password = password, token = token)
    private val ia = IA(id = 2L, userName = userName, difficulty = difficulty)

    @BeforeEach
    fun init() {
        factory = UserFactory()
        instance = UserIntegration(factory, repository)
    }

    @Nested
    inner class CreateHuman {

        @Test
        fun `Create new human`() {
            val user = instance.createUser(userName, fullName, false, password)

            val allUser = repository.findAllBy()
            assertEquals(1, allUser.size)
            val userFound = allUser.first()
            assertEquals(user, userFound)
        }

        @Test
        fun `Create new admin`() {
            val user = instance.createUser(userName, fullName, true, password)

            val allUser = repository.findAllBy()
            assertEquals(1, allUser.size)
            val userFound = allUser.first()
            assertEquals(user, userFound)
        }

        @Test
        fun `Create new human if another human has same userName but different fullName`() {
            val user = human.copy(fullName = "fullNameTest2")
            repository.save(factory.toEntity(user))

            val newUser = instance.createUser(userName, fullName, false, password)

            val allUser = repository.findAllBy()
            assertEquals(2, allUser.size)
            assertTrue(allUser.contains(user))
            assertTrue(allUser.contains(newUser))
        }

        @Test
        fun `Create new human if another human has same fullName but different userName`() {
            val user = human.copy(userName = "userNameTest2")
            repository.save(factory.toEntity(user))

            val newUser = instance.createUser(userName, fullName, false, password)

            val allUser = repository.findAllBy().map { it.toModel() }
            assertEquals(2, allUser.size)
            assertTrue(allUser.contains(user))
            assertTrue(allUser.contains(newUser))
        }

        @Test
        fun `Create human and it already exists`() {
            repository.save(factory.toEntity(human))

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

            val allUser = repository.findAllBy()
            assertEquals(1, allUser.size)
            assertTrue(allUser.contains(user))
        }

        @Test
        fun `Create ia and it already exists`() {
            repository.save(factory.toEntity(ia))

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
            repository.save(factory.toEntity(human))

            instance.activateUserSession(userName, password)

            val foundUser = repository.findAllBy().first() as Human
            assertEquals(1L, foundUser.id)
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
            repository.save(factory.toEntity(human.copy(password = "passwordTest2")))

            assertThrows(ElementNotFoundException::class.java) {
                instance.activateUserSession(userName, password)
            }
        }

        @Test
        fun `Non activate section if only password matches`() {
            repository.save(factory.toEntity(human.copy(userName = "userNameTest2")))

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
            repository.save(factory.toEntity(human.copy(token = token)))

            instance.disableUserSession(token)

            val allUser = repository.findAllBy()
            assertEquals(1, allUser.size)

            val foundUser = allUser.first() as Human
            assertEquals(1L, foundUser.id)
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
            repository.save(factory.toEntity(human.copy(token = "tokenTest2")))

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
            repository.save(factory.toEntity(human))

            val userById = instance.getUserById(1L)
            assertEquals(human, userById)
        }

        @Test
        fun `Get human by id but not exist`() {
            repository.save(factory.toEntity(human.copy(id = 1L)))

            assertThrows(ElementNotFoundException::class.java) {
                instance.getUserById(1L)
            }
        }

        @Test
        fun `Get human by id but no human exists in the system`() {
            assertThrows(ElementNotFoundException::class.java) {
                instance.getUserById(1L)
            }
        }

    }

    @Nested
    inner class GetIAById {

        @Test
        fun `Get ia by id`() {
            repository.save(factory.toEntity(ia))

            val userById = instance.getUserById(1L)
            assertEquals(ia, userById)
        }

        @Test
        fun `Get ia by id but not exist`() {
            repository.save(factory.toEntity(ia.copy(id = 1L)))

            assertThrows(ElementNotFoundException::class.java) {
                instance.getUserById(1L)
            }
        }

        @Test
        fun `Get ia by id but no ia exists in the system`() {
            assertThrows(ElementNotFoundException::class.java) {
                instance.getUserById(1L)
            }
        }

    }

    @Test
    fun getAllUser() {
        repository.save(factory.toEntity(human.copy(id = 1L)))
        repository.save(factory.toEntity(human.copy(id = 2L)))
        repository.save(factory.toEntity(ia.copy(id = 3L)))
        repository.save(factory.toEntity(ia.copy(id = 4L)))

        val allUser = instance.getAllUser()
        assertEquals(4, allUser.size)
        assertTrue(allUser.contains(human.copy(id = 1L)))
        assertTrue(allUser.contains(human.copy(id = 2L)))
        assertTrue(allUser.contains(ia.copy(id = 3L)))
        assertTrue(allUser.contains(ia.copy(id = 4L)))
    }

    @Nested
    inner class SaveUser {

        @Test
        fun `Save user type human`() {
            instance.saveUser(human.copy(token = token))

            val allUser = repository.findAllBy()
            assertEquals(1, allUser.size)

            val foundUser = allUser.first() as Human
            assertEquals(1L, foundUser.id)
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

            repository.findAllBy()

            val allUser = repository.findAllBy()
            assertEquals(1, allUser.size)

            val foundUser = allUser.first() as IA
            assertEquals(1L, foundUser.id)
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
            repository.save(factory.toEntity(human))

            val usersFound = instance.searchHumanUserByIdUserNameFullNameOrToken(id = 1L.toString())

            assertEquals(1, usersFound.size)
            assertEquals(human ,usersFound.first())
        }

        @Test
        fun `Search user by id and can't find any`() {
            repository.save(factory.toEntity(human))

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
            repository.save(factory.toEntity(human))

            val usersFound = instance.searchHumanUserByIdUserNameFullNameOrToken(userName = "userNameTest")

            assertEquals(1, usersFound.size)
            assertEquals(human ,usersFound.first())
        }

        @Test
        fun `Search user by userName ignoreCase and find one`() {
            repository.save(factory.toEntity(human))

            val usersFound = instance.searchHumanUserByIdUserNameFullNameOrToken(userName = "USERNAMETEST")

            assertEquals(1, usersFound.size)
            assertEquals(human ,usersFound.first())
        }

        @Test
        fun `Search user by userName and can't find any`() {
            repository.save(factory.toEntity(human))

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
            repository.save(factory.toEntity(human))

            val usersFound = instance.searchHumanUserByIdUserNameFullNameOrToken(fullName = "fullNameTest")

            assertEquals(1, usersFound.size)
            assertEquals(human ,usersFound.first())
        }

        @Test
        fun `Search user by fullName ignoreCase and find one`() {
            repository.save(factory.toEntity(human))

            val usersFound = instance.searchHumanUserByIdUserNameFullNameOrToken(fullName = "FULLNAMETEST")

            assertEquals(1, usersFound.size)
            assertEquals(human ,usersFound.first())
        }

        @Test
        fun `Search user by fullName and can't find any`() {
            repository.save(factory.toEntity(human))

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
            repository.save(factory.toEntity(human))

            val usersFound = instance.searchHumanUserByIdUserNameFullNameOrToken(token = token.toString())

            assertEquals(1, usersFound.size)
            assertEquals(human ,usersFound.first())
        }

        @Test
        fun `Search user by token and can't find any`() {
            repository.save(factory.toEntity(human))

            val usersFound = instance.searchHumanUserByIdUserNameFullNameOrToken(token = "token-test")

            assertTrue(usersFound.isEmpty())
        }

    }

    @Nested
    inner class SearchIAUserByIdUserNameFullNameOrToken {

        @Test
        fun `Search user by id and find one`() {
            repository.save(factory.toEntity(ia))

            val usersFound = instance.searchIAUserByIdUserNameFullNameOrToken(id = 1L.toString())

            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(ia))
        }

        @Test
        fun `Search user by id and can't find any`() {
            repository.save(factory.toEntity(ia))

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
            repository.save(factory.toEntity(ia))

            val usersFound = instance.searchIAUserByIdUserNameFullNameOrToken(userName = "userNameTest")

            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(ia))
        }

        @Test
        fun `Search user by userName ignoreCase and find one`() {
            repository.save(factory.toEntity(ia))

            val usersFound = instance.searchIAUserByIdUserNameFullNameOrToken(userName = "USERNAMETEST")

            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(ia))
        }

        @Test
        fun `Search user by userName and can't find any`() {
            repository.save(factory.toEntity(ia))

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
            repository.save(factory.toEntity(ia))

            val usersFound = instance.searchIAUserByIdUserNameFullNameOrToken(difficulty = "HARD")

            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(ia))
        }

        @Test
        fun `Search user by difficulty ignoreCase and find one`() {
            repository.save(factory.toEntity(ia))

            val usersFound = instance.searchIAUserByIdUserNameFullNameOrToken(difficulty = "hard")

            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(ia))
        }

        @Test
        fun `Search user by difficulty and can't find any`() {
            repository.save(factory.toEntity(ia))

            repository.save(factory.toEntity(ia))

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