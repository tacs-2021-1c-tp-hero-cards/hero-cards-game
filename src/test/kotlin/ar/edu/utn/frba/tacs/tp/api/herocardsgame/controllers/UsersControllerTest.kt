package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.ActivateUserSessionRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateUserRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.HashService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.UserService
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
        context.register(UserService::class.java)
        context.register(UserIntegration::class.java)

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

            val users = instance.getUsers("0")
            assertTrue(
                users.body!!.contains(
                    User(
                        0L,
                        "userName",
                        "fullName",
                        HashService.calculatePasswordHash("userName", "password")
                    )
                )
            )
        }

        @Test
        fun `Not sign up with a exist user`() {
            val response = instance.signUp(CreateUserRequest("userName", "fullName", "password"))
            assertEquals(200, response.statusCodeValue)
            assertEquals(response.body!!, 0L)

            val users = instance.getUsers("0")
            assertTrue(
                users.body!!.contains(
                    User(
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

            val users = instance.getUsers("0")
            assertTrue(
                users.body!!.contains(
                    User(
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

            val response = instance.logOut(hashMapOf("token" to token))
            assertEquals(200, response.statusCodeValue)

            val users = instance.getUsers("0")
            assertTrue(
                users.body!!.contains(
                    User(
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
            val response = instance.logOut(hashMapOf("token" to "tokenTest"))
            assertEquals(400, response.statusCodeValue)
            assertNull(response.body)
        }
    }

    @Nested
    inner class GetUsers {

        @Test
        fun `Search by user id`() {
            instance.signUp(CreateUserRequest("userName", "fullName", "password"))
            instance.signUp(CreateUserRequest("userName2", "fullName2", "password2"))

            val response = instance.getUsers("1")
            assertEquals(200, response.statusCodeValue)
            assertTrue(
                response.body!!.contains(
                    User(
                        1L,
                        "userName2",
                        "fullName2",
                        HashService.calculatePasswordHash("userName2", "password2")
                    )
                )
            )
        }

        @Test
        fun `Search by invalid user id, returns NOT_FOUND`() {
            instance.signUp(CreateUserRequest("userName", "fullName", "password"))

            val response = instance.getUsers("1")
            assertEquals(200, response.statusCodeValue)
            assertTrue(response.body!!.isEmpty())
        }
    }
}