package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user.UserEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class UserRepositoryTest @Autowired constructor(
    val entityManager: TestEntityManager,
    val instance: UserRepository
) {

    private val userName = "userNameTest"
    private val fullName = "fullNameTest"
    private val password = "passwordTest"
    private val token = "tokenTest"
    private val difficulty = IADifficulty.HARD

    private val humanUserEntity =
        UserEntity(
            userName = userName,
            userType = UserType.HUMAN.name,
            winCount = 3,
            tieCount = 2,
            loseCount = 1,
            inProgressCount = 0,
            fullName = fullName,
            password = password,
            token = token,
            isAdmin = true
        )

    private val iaUserEntity =
        UserEntity(
            userName = userName,
            userType = UserType.IA.name,
            winCount = 0,
            tieCount = 1,
            loseCount = 2,
            inProgressCount = 3,
            difficulty = difficulty.name
        )

    @Nested
    inner class GetById{

        @Test
        fun `Search human by id`() {
            entityManager.persist(humanUserEntity)

            val found = instance.getById(humanUserEntity.id!!)
            assertEquals(humanUserEntity, found)
        }

        @Test
        fun `Search ia by id`() {
            entityManager.persist(iaUserEntity)

            val found = instance.getById(iaUserEntity.id!!)
            assertEquals(iaUserEntity, found)
        }

        @Test
        fun `Search user by id and can't find`() {
            val found = instance.getById(1L)
            assertNull(found)
        }

    }

    @Nested
    inner class FindHumanByToken{

        @Test
        fun `Search human by token`() {
            entityManager.persist(humanUserEntity)

            val found = instance.findHumanByToken(token)
            assertEquals(humanUserEntity, found)
        }

        @Test
        fun `Search human by token and can't find`() {
            val found = instance.findHumanByToken("tokenTest2")
            assertNull(found)
        }

    }

    @Nested
    inner class FindHumanByUserNameAndPassword {

        @Test
        fun `Find human by username and password and username and password don't match`(){
            entityManager.persist(humanUserEntity)

            val found = instance.findHumanByUserNameAndPassword("userNameTest2", "passwordTest2")
            assertNull(found)
        }

        @Test
        fun `Find human by username and password and only match username`(){
            entityManager.persist(humanUserEntity)

            val found = instance.findHumanByUserNameAndPassword(userName, "passwordTest2")
            assertNull(found)
        }

        @Test
        fun `Find human by username and password and only match pasword`(){
            entityManager.persist(humanUserEntity)

            val found = instance.findHumanByUserNameAndPassword("userNameTest2", password)
            assertNull(found)
        }

        @Test
        fun `Find human by username and password and find`(){
            entityManager.persist(humanUserEntity)

            val found = instance.findHumanByUserNameAndPassword(userName.toLowerCase(), password)
            assertEquals(humanUserEntity, found)
        }

    }

    @Nested
    inner class FindHumanByIdAndUserNameAndFullNameAndToken {

        @Test
        fun `Search user by id and find one`() {
            entityManager.persist(humanUserEntity)

            val usersFound = instance.findHumanByIdAndUserNameAndFullNameAndToken(humanUserEntity.id!!.toString())
            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(humanUserEntity))
        }

        @Test
        fun `Search user by id and can't find any`() {
            entityManager.persist(humanUserEntity)

            val usersFound = instance.findHumanByIdAndUserNameAndFullNameAndToken(id = "2")
            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by id and not users in system`() {
            val usersFound = instance.findHumanByIdAndUserNameAndFullNameAndToken(id = "1")

            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by userName and find one`() {
            entityManager.persist(humanUserEntity)

            val usersFound = instance.findHumanByIdAndUserNameAndFullNameAndToken(userName = "userNameTest")
            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(humanUserEntity))
        }

        @Test
        fun `Search user by userName ignoreCase and find one`() {
            entityManager.persist(humanUserEntity)

            val usersFound = instance.findHumanByIdAndUserNameAndFullNameAndToken(userName = "USERNAMETEST")
            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(humanUserEntity))
        }

        @Test
        fun `Search user by userName and can't find any`() {
            entityManager.persist(humanUserEntity)

            val usersFound = instance.findHumanByIdAndUserNameAndFullNameAndToken(userName = "userNameTest2")
            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by userName and not users in system`() {
            val usersFound = instance.findHumanByIdAndUserNameAndFullNameAndToken(userName = "userNameTest2")
            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by fullName and find one`() {
            entityManager.persist(humanUserEntity)

            val usersFound = instance.findHumanByIdAndUserNameAndFullNameAndToken(fullName = "fullNameTest")
            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(humanUserEntity))
        }

        @Test
        fun `Search user by fullName ignoreCase and find one`() {
            entityManager.persist(humanUserEntity)

            val usersFound = instance.findHumanByIdAndUserNameAndFullNameAndToken(fullName = "FULLNAMETEST")
            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(humanUserEntity))
        }

        @Test
        fun `Search user by fullName and can't find any`() {
            entityManager.persist(humanUserEntity)

            val usersFound = instance.findHumanByIdAndUserNameAndFullNameAndToken(fullName = "fullNameTest2")
            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by fullName and not users in system`() {
            val usersFound = instance.findHumanByIdAndUserNameAndFullNameAndToken(fullName = "fullNameTest2")
            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by token and find one`() {
            entityManager.persist(humanUserEntity)

            val usersFound = instance.findHumanByIdAndUserNameAndFullNameAndToken(token = token)
            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(humanUserEntity))
        }

        @Test
        fun `Search user by token and can't find any`() {
            entityManager.persist(humanUserEntity)

            val usersFound = instance.findHumanByIdAndUserNameAndFullNameAndToken(token = "token-test")
            assertTrue(usersFound.isEmpty())
        }

    }

    @Nested
    inner class FindIAByIdAndUserNameAndDifficulty {

        @Test
        fun `Search user by id and find one`() {
            entityManager.persist(iaUserEntity)

            val usersFound = instance.findIAByIdAndUserNameAndDifficulty(id = iaUserEntity.id!!.toString())
            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(iaUserEntity))
        }

        @Test
        fun `Search user by id and can't find any`() {
            entityManager.persist(iaUserEntity)

            val usersFound = instance.findIAByIdAndUserNameAndDifficulty(id = "1")
            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by id and not users in system`() {
            val usersFound = instance.findIAByIdAndUserNameAndDifficulty(id = "1")
            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by userName and find one`() {
            entityManager.persist(iaUserEntity)

            val usersFound = instance.findIAByIdAndUserNameAndDifficulty(userName = "userNameTest")
            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(iaUserEntity))
        }

        @Test
        fun `Search user by userName ignoreCase and find one`() {
            entityManager.persist(iaUserEntity)

            val usersFound = instance.findIAByIdAndUserNameAndDifficulty(userName = "USERNAMETEST")
            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(iaUserEntity))
        }

        @Test
        fun `Search user by userName and can't find any`() {
            entityManager.persist(iaUserEntity)

            val usersFound = instance.findIAByIdAndUserNameAndDifficulty(userName = "userNameTest2")
            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by userName and not users in system`() {
            val usersFound = instance.findIAByIdAndUserNameAndDifficulty(userName = "userNameTest2")
            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by difficulty and find one`() {
            entityManager.persist(iaUserEntity)

            val usersFound = instance.findIAByIdAndUserNameAndDifficulty(difficulty = "HARD")
            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(iaUserEntity))
        }

        @Test
        fun `Search user by difficulty ignoreCase and find one`() {
            entityManager.persist(iaUserEntity)

            val usersFound = instance.findIAByIdAndUserNameAndDifficulty(difficulty = "hard")
            assertEquals(1, usersFound.size)
            assertTrue(usersFound.contains(iaUserEntity))
        }

        @Test
        fun `Search user by difficulty and can't find any`() {
            entityManager.persist(iaUserEntity)

            val usersFound = instance.findIAByIdAndUserNameAndDifficulty(difficulty = "hards")
            assertTrue(usersFound.isEmpty())
        }

        @Test
        fun `Search user by difficulty and not users in system`() {
            val usersFound = instance.findIAByIdAndUserNameAndDifficulty(difficulty = "HARD")
            assertTrue(usersFound.isEmpty())
        }

    }

}