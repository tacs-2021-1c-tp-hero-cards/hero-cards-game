package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidUserException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Authentication
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.ActivateUserSessionRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateUserRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@CrossOrigin(origins =["http://localhost:3000"], allowedHeaders = ["*"])
class UsersController(
    private val userIntegration: UserIntegration
) {

    /**
     * @param createUserRequest
     * @return userId
     */
    @PostMapping("/signUp")
    fun signUp(@RequestBody createUserRequest: CreateUserRequest): ResponseEntity<Long> =
        try {
            val newUser =
                userIntegration.createUser(
                    createUserRequest.userName,
                    createUserRequest.fullName,
                    createUserRequest.buildPasswordHash()
                )
            ResponseEntity.ok().body(newUser.id!!)
        } catch (e: InvalidUserException) {
            ResponseEntity.badRequest().build()
        }

    /**
     * @param activateUserSessionRequest
     * @return authentication
     */
    @PostMapping("/logIn")
    fun logIn(@RequestBody activateUserSessionRequest: ActivateUserSessionRequest): ResponseEntity<Authentication> =
        try {
            val user =
                userIntegration.activateUserSession(
                    activateUserSessionRequest.userName,
                    activateUserSessionRequest.buildPasswordHash()
                )
            ResponseEntity.ok().body(Authentication(user.token!!))
        } catch (e: ElementNotFoundException) {
            ResponseEntity.badRequest().build()
        }

    /**
     * @param deactivateUserSessionRequest
     * @return
     */
    @PostMapping("/logOut")
    fun logOut(@RequestBody tokenMap: Map<String, String>): ResponseEntity<Void> =
        try {
            userIntegration.disableUserSession(tokenMap["token"]!!)
            ResponseEntity.ok().build()
        } catch (e: ElementNotFoundException) {
            ResponseEntity.badRequest().build()
        }

    /**
     * @param userId
     * @return user
     */
    @GetMapping("/admin/users/{user-id}")
    fun getUser(@PathVariable("user-id") userId: String): ResponseEntity<User> =
        try {
            ResponseEntity.ok().body(userIntegration.getUserById(userId.toLong()))
        } catch (e: ElementNotFoundException) {
            ResponseEntity.badRequest().build()
        }

}
