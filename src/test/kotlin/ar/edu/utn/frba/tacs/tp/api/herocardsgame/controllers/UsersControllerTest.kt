package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user.UserEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user.UserFactory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository.UserRepository
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.ActivateUserSessionRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateIARequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateUserRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.HashService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.context.annotation.Bean
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

internal class UsersControllerTest {

    lateinit var repositoryMock: UserRepository
    lateinit var instance: UsersController

    private fun userEntity(userType: UserType) =
        UserEntity(
            userName = "userName",
            userType = userType,
            winCount = 0,
            tieCount = 0,
            loseCount = 0,
            inProgressCount = 0
        )

    private val passwordHash = HashService.calculatePasswordHash("userName", "password")

    @BeforeEach
    fun init() {
        val context = AnnotationConfigWebApplicationContext()
        context.register(UsersControllerTest::class.java)
        context.register(UsersController::class.java)
        context.register(UserIntegration::class.java)
        context.register(UserFactory::class.java)

        context.refresh()
        context.start()

        repositoryMock = context.getBean(UserRepository::class.java)
        instance = context.getBean(UsersController::class.java)
    }

    @Bean
    fun getUserRepository(): UserRepository = mock(UserRepository::class.java)

    @Nested
    inner class SignUp {

        @Test
        fun `Sign up with a new user`() {
            `when`(
                repositoryMock.save(
                    userEntity(UserType.HUMAN).copy(fullName = "fullName", password = passwordHash, isAdmin = false)
                )
            )
                .thenReturn(
                    userEntity(UserType.HUMAN).copy(
                        id = 0L, fullName = "fullName", password = passwordHash, isAdmin = false
                    )
                )

            val response = instance.signUp(CreateUserRequest("userName", "fullName", "password"))
            assertEquals(200, response.statusCodeValue)
            assertEquals(response.body!!, 0L)
        }

        @Test
        fun `Sign up with a new admin`() {
            `when`(
                repositoryMock.save(
                    userEntity(UserType.HUMAN).copy(fullName = "fullName", password = passwordHash, isAdmin = true)
                )
            )
                .thenReturn(
                    userEntity(UserType.HUMAN).copy(
                        id = 0L,
                        fullName = "fullName",
                        password = passwordHash,
                        isAdmin = true
                    )
                )

            val response = instance.signUp(CreateUserRequest("userName", "fullName", "password", true))
            assertEquals(200, response.statusCodeValue)
            assertEquals(response.body!!, 0L)
        }

        @Test
        fun `Not sign up with a exist user`() {
            `when`(
                repositoryMock.findHumanByIdAndUserNameAndFullNameAndToken(
                    userName = "userName",
                    fullName = "fullName"
                )
            )
                .thenReturn(
                    listOf(
                        userEntity(UserType.HUMAN).copy(fullName = "fullName", password = passwordHash, isAdmin = false)
                    )
                )

            val response = instance.signUp(CreateUserRequest("userName", "fullName", "password"))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

    }

    @Nested
    inner class LogIn {

//        @Test
//        fun `Log in with a exist user`() {
//            TODO: ver como mockear static
//            `when`(repositoryMock.findHumanByUserNameAndPassword("userName", passwordHash))
//                .thenReturn(
//                    userEntity(UserType.HUMAN).copy(
//                        id = 0L,
//                        fullName = "fullName",
//                        password = passwordHash,
//                        isAdmin = false
//                    )
//                )
//
//            `when`(
//                repositoryMock.save(
//                    userEntity(UserType.HUMAN).copy(
//                        id = 0L,
//                        fullName = "fullName",
//                        password = passwordHash,
//                        isAdmin = true
//                    )
//                )
//            ).thenReturn(
//                userEntity(UserType.HUMAN).copy(
//                    id = 0L,
//                    fullName = "fullName",
//                    password = passwordHash,
//                    isAdmin = false
//                )
//            )
//
//            val response = instance.logIn(ActivateUserSessionRequest("userName", "password"))
//            assertEquals(200, response.statusCodeValue)
//            val token = response.body!!.token
//            assertTrue(token.isNotBlank())
//        }

        @Test
        fun `Not Log in with a not exist user`() {
            `when`(repositoryMock.findHumanByUserNameAndPassword("userName", passwordHash)).thenReturn(null)

            val response = instance.logIn(ActivateUserSessionRequest("userName", "password"))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }
    }

    @Nested
    inner class LogOut {

        @Test
        fun `Log out with a exist user`() {
            val token = "token"

            `when`(repositoryMock.findHumanByToken(token)).thenReturn(
                userEntity(UserType.HUMAN).copy(
                    fullName = "fullName",
                    password = passwordHash,
                    isAdmin = false,
                    token = token
                )
            )

            `when`(
                repositoryMock.save(
                    userEntity(UserType.HUMAN).copy(
                        fullName = "fullName",
                        password = passwordHash,
                        isAdmin = false,
                        token = null
                    )
                )
            ).thenReturn(
                userEntity(UserType.HUMAN).copy(
                    fullName = "fullName",
                    password = passwordHash,
                    isAdmin = false,
                    token = null
                )
            )

            val response = instance.logOut(token)
            assertEquals(200, response.statusCodeValue)
        }

        @Test
        fun `Not Log out with a not exist user`() {
            val response = instance.logOut("tokenTest")
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }
    }

    @Nested
    inner class GetHumanUserByIdUserNameOrFullName {

        @Test
        fun `Search by user userName and fullName`() {
            `when`(
                repositoryMock.findHumanByIdAndUserNameAndFullNameAndToken(
                    userName = "userName",
                    fullName = "fullName"
                )
            ).thenReturn(
                listOf(
                    userEntity(UserType.HUMAN).copy(
                        id = 0L,
                        fullName = "fullName",
                        password = passwordHash,
                        isAdmin = false
                    )
                )
            )

            val response =
                instance.getHumanUserByIdUserNameFullNameOrToken(userName = "userName", fullName = "fullName")
            assertEquals(200, response.statusCodeValue)
            assertTrue(response.body!!.contains(Human(0L, "userName", "fullName", passwordHash)))
        }
    }

    @Nested
    inner class CreateIA {

        @Test
        fun `Create IA with exist difficulty`() {
            `when`(repositoryMock.findIAByIdAndUserNameAndDifficulty(userName = "userName", difficulty = "HARD"))
                .thenReturn(emptyList())
            `when`(repositoryMock.save(userEntity(UserType.IA).copy(difficulty = IADifficulty.HARD)))
                .thenReturn(userEntity(UserType.IA).copy(id = 0L, difficulty = IADifficulty.HARD))

            val response = instance.createIA(CreateIARequest("userName", IADifficulty.HARD.name))
            assertEquals(200, response.statusCodeValue)
            assertEquals(response.body!!, 0L)
        }

        @Test
        fun `Not create ia with a exist ia`() {
            `when`(repositoryMock.findIAByIdAndUserNameAndDifficulty(userName = "userName", difficulty = "HARD"))
                .thenReturn(listOf(userEntity(UserType.IA).copy(difficulty = IADifficulty.HARD)))

            val response = instance.createIA(CreateIARequest("userName", IADifficulty.HARD.name))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

        @Test
        fun `Not create ia with a non exist difficulty`() {
            `when`(repositoryMock.findIAByIdAndUserNameAndDifficulty(userName = "userName", difficulty = "HARD"))
                .thenReturn(emptyList())

            val response = instance.createIA(CreateIARequest("userName", "HARDY"))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

    }

    @Nested
    inner class GetIAUserByIdUserNameOrFullName {

        @Test
        fun `Search by user userName and difficulty`() {
            `when`(
                repositoryMock.findIAByIdAndUserNameAndDifficulty(
                    userName = "userName",
                    difficulty = "HARD"
                )
            ).thenReturn(
                listOf(
                    userEntity(UserType.IA).copy(
                        id = 0L,
                        difficulty = IADifficulty.HARD
                    )
                )
            )
            val response = instance.getIAUserByIdUserNameFullNameOrToken(userName = "userName", difficulty = "HARD")
            assertEquals(200, response.statusCodeValue)
            assertTrue(
                response.body!!.contains(
                    IA(
                        id = 0L,
                        userName = "userName",
                        difficulty = IADifficulty.HARD
                    )
                )
            )
        }
    }
}