package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.ActivateUserSessionRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateIARequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateUserRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.HashService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

internal class UsersControllerTest {

    lateinit var instance: UsersController

    @BeforeEach
    fun init() {
        val context = AnnotationConfigWebApplicationContext()
        context.register(UsersController::class.java)
        context.register(UserIntegration::class.java)
        context.register(Dao::class.java)

        context.refresh()
        context.start()

        instance = context.getBean(UsersController::class.java)
    }

    @Nested
    inner class SignUp {

        @Test
        fun `Sign up with a new user`() {
            val response = instance.signUp(CreateUserRequest("userName", "fullName", "password"))
            assertEquals(200, response.statusCodeValue)
            assertEquals(response.body!!, 0L)

            val users = instance.getHumanUserByIdUserNameFullNameOrToken(userId = "0")
            assertTrue(
                users.body!!.contains(
                    Human(
                        0L,
                        "userName",
                        "fullName",
                        HashService.calculatePasswordHash("userName", "password"),
                        isAdmin = false
                    )
                )
            )
        }

        @Test
        fun `Sign up with a new admin`() {
            val response = instance.signUp(CreateUserRequest("userName", "fullName", "password", true))
            assertEquals(200, response.statusCodeValue)
            assertEquals(response.body!!, 0L)

            val users = instance.getHumanUserByIdUserNameFullNameOrToken(userId = "0")
            assertTrue(
                users.body!!.contains(
                    Human(
                        0L,
                        "userName",
                        "fullName",
                        HashService.calculatePasswordHash("userName", "password"),
                        isAdmin = true
                    )
                )
            )
        }

        @Test
        fun `Not sign up with a exist user`() {
            val response = instance.signUp(CreateUserRequest("userName", "fullName", "password"))
            assertEquals(200, response.statusCodeValue)
            assertEquals(response.body!!, 0L)

            val users = instance.getHumanUserByIdUserNameFullNameOrToken(userId = "0")
            assertTrue(
                users.body!!.contains(
                    Human(
                        0L,
                        "userName",
                        "fullName",
                        HashService.calculatePasswordHash("userName", "password")
                    )
                )
            )

            val otherResponse = instance.signUp(CreateUserRequest("userName", "fullName", "password"))
            assertEquals(400, otherResponse.statusCodeValue)
            assertNull(otherResponse.body)
        }

    }

    @Nested
    inner class LogIn {

        @Test
        fun `Log in with a exist user`() {
            instance.signUp(CreateUserRequest("userName", "fullName", "password"))

            val response = instance.logIn(ActivateUserSessionRequest("userName", "password"))
            assertEquals(200, response.statusCodeValue)
            val token = response.body!!.token
            assertTrue(token.isNotBlank())

            val users = instance.getHumanUserByIdUserNameFullNameOrToken(userId = "0")
            assertTrue(
                users.body!!.contains(
                    Human(
                        0L,
                        "userName",
                        "fullName",
                        HashService.calculatePasswordHash("userName", "password"),
                        token
                    )
                )
            )
        }

        @Test
        fun `Not Log in with a not exist user`() {
            val response = instance.logIn(ActivateUserSessionRequest("userName", "password"))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }
    }

    @Nested
    inner class LogOut {

        @Test
        fun `Log out with a exist user`() {
            instance.signUp(CreateUserRequest("userName", "fullName", "password"))
            val token = instance.logIn(ActivateUserSessionRequest("userName", "password")).body!!.token

            val response = instance.logOut(token)
            assertEquals(200, response.statusCodeValue)

            val users = instance.getHumanUserByIdUserNameFullNameOrToken(userId = "0")
            assertTrue(
                users.body!!.contains(
                    Human(
                        0L,
                        "userName",
                        "fullName",
                        HashService.calculatePasswordHash("userName", "password")
                    )
                )
            )
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
            instance.signUp(CreateUserRequest("userName", "fullName", "password"))
            instance.signUp(CreateUserRequest("userName", "fullName2", "password2"))
            instance.signUp(CreateUserRequest("userName2", "fullName2", "password3"))

            val response = instance.getHumanUserByIdUserNameFullNameOrToken(userName = "userName", fullName = "fullName")
            assertEquals(200, response.statusCodeValue)
            assertTrue(
                response.body!!.contains(
                    Human(
                        0L,
                        "userName",
                        "fullName",
                        HashService.calculatePasswordHash("userName", "password")
                    )
                )
            )
        }
    }

    @Nested
    inner class CreateIA {

        @Test
        fun `Create IA with exist difficulty`() {
            val response = instance.createIA(CreateIARequest("userName", IADifficulty.HARD.name))
            assertEquals(200, response.statusCodeValue)
            assertEquals(response.body!!, 0L)

            val users = instance.getIAUserByIdUserNameFullNameOrToken(userId = "0")
            assertTrue(
                users.body!!.contains(
                    IA(id = 0L, userName = "userName", difficulty = IADifficulty.HARD)
                )
            )
        }

        @Test
        fun `Not create ia with a exist ia`() {
            val response = instance.createIA(CreateIARequest("userName", IADifficulty.HARD.name))
            assertEquals(200, response.statusCodeValue)
            assertEquals(response.body!!, 0L)

            val users = instance.getIAUserByIdUserNameFullNameOrToken(userId = "0")
            assertTrue(
                users.body!!.contains(
                    IA(id = 0L, userName = "userName", difficulty = IADifficulty.HARD)
                )
            )

            val otherResponse = instance.createIA(CreateIARequest("userName", IADifficulty.RANDOM.name))
            assertEquals(400, otherResponse.statusCodeValue)
            assertNull(otherResponse.body)
        }

        @Test
        fun `Not create ia with a non exist difficulty`() {
            val response = instance.createIA(CreateIARequest("userName", "HARDY"))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }

    }

    @Nested
    inner class GetIAUserByIdUserNameOrFullName {

        @Test
        fun `Search by user userName and difficulty`() {
            instance.createIA(CreateIARequest("userName", "HARD"))
            instance.createIA(CreateIARequest("userName", "EASY"))
            instance.createIA(CreateIARequest("userName2", "EASY"))

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