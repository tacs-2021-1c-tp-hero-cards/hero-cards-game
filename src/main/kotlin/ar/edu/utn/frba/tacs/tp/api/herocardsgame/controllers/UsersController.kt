package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;


import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Authentication
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class UsersController {

    /**
     * TODO authentication with one of the following platforms
     * TODO We should validate username, password
     * (Google/FB/Github/LinkedIn)
     * @param user
     * @return
     */
    @PostMapping("/logIn")
    fun login(@RequestBody user: User): ResponseEntity<Authentication> {
        println("user: " + user.username);
        val token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjEifQ";
        val auth = Authentication(token);
        return ResponseEntity.ok().body(auth);
    }

    /**
     * TODO We should validate username, password and fullname
     * TODO We should validate if the username doesn't exists
     * TODO call the service to persist the new username
     * @param user
     * @return
     */
    @PostMapping("/signUp")
    fun signIn(@RequestBody user: User): ResponseEntity<Authentication> {
        println("user: " + user.username);
        val token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjEifQ";
        val auth = Authentication(token);
        return ResponseEntity.ok().body(auth);
    }

    @GetMapping("/admin/users/{userId}")
    fun getUsers(@PathVariable("userId") userId: String): ResponseEntity<List<User>> {
        println(userId);
        val user = User(userId, "fullname", "password", "token");
        val users = listOf(user);
        return ResponseEntity.ok().body(users);
    }

    @PostMapping("/users/logout")
    fun logout(): ResponseEntity<Void> {
        println("logout");
        return ResponseEntity.ok().build();
    }

}
