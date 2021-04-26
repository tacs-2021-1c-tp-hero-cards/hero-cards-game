package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Authentication
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

internal class UsersControllerTest {

    private val usersController: UsersController = UsersController()

    @Test
    fun login() {
        val user = User("user1", "", "password", "")

        val responseEntity: ResponseEntity<Authentication> = usersController.login(user)

        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        val body: Authentication = responseEntity.body!!
        assertThat(body.token).isEqualTo("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjEifQ")
    }

    @Test
    fun sigIn() {
        val user = User("user1", "fullname", "password", "token");

        val responseEntity = usersController.signIn(user)

        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        val body: Authentication = responseEntity.body!!
        assertThat(body.token).isEqualTo("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjEifQ")
    }

    @Test
    fun getUsers() {
        val responseEntity = usersController.getUsers("user1")

        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        val body = responseEntity.body!!
        assertThat(body).hasSize(1)
        assertThat(body[0].fullName).isEqualTo("fullname")
        assertThat(body[0].token).isEqualTo("token")
        assertThat(body[0].username).isEqualTo("user1")
    }

    @Test
    fun logout() {
    }
}