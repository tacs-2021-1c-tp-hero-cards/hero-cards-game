package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;


import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidUserException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Authentication
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.ActivateUserSessionRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateUserRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class UsersController(
    private val userService: UserService
) {

    /**
     * @param createUserRequest
     * @return userId
     */
    @PostMapping("/signUp")
    fun signUp(@RequestBody createUserRequest: CreateUserRequest): ResponseEntity<Long> =
        try {
            val newUser =
                userService.createUser(
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
            val token =
                userService.activateUserSession(
                    activateUserSessionRequest.userName,
                    activateUserSessionRequest.buildPasswordHash()
                )
            ResponseEntity.ok().body(Authentication(token))
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
            userService.deactivateUserSession(tokenMap["token"]!!)
            ResponseEntity.ok().build()
        } catch (e: ElementNotFoundException) {
            ResponseEntity.badRequest().build()
        }

    /**
     * @param userId
     * @return list of users
     */
    @GetMapping("/admin/users/{user-id}")
    fun getUsers(@PathVariable("user-id") userId: String): ResponseEntity<List<User>> =
        ResponseEntity.ok().body(userService.searchUser(id = userId.toLong()))

}
