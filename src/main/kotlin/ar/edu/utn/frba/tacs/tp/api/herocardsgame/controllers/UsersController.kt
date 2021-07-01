package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidUserException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Authentication
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.ActivateUserSessionRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateUserRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@CrossOrigin(origins = ["http://localhost:3000"], allowedHeaders = ["*"])
class UsersController(
    private val userIntegration: UserIntegration
) {

    private val log: Logger = LoggerFactory.getLogger(UsersController::class.java)

    /**
     * @param createUserRequest
     * @return userId
     */
    @PostMapping("/signUp")
    fun signUp(@RequestBody createUserRequest: CreateUserRequest): ResponseEntity<Long> =
        try {
            log.info("Post /signUp requestBody: [$createUserRequest]")
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
            log.info("Post /logIn requestBody: [$activateUserSessionRequest]")
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
            log.info("Post /logOut requestBody: [$tokenMap]")
            userIntegration.disableUserSession(tokenMap["token"]!!)
            ResponseEntity.ok().build()
        } catch (e: ElementNotFoundException) {
            ResponseEntity.badRequest().build()
        }

    /**
     * @param userId, userName, fullName
     * @return list of user
     */
    @GetMapping("/users/search")
    fun getUserByIdUserNameOrFullName(
        @RequestParam(value = "user-id") userId: String? = null,
        @RequestParam(value = "user-name") userName: String? = null,
        @RequestParam(value = "full-name") fullName: String? = null
    ): ResponseEntity<List<User>> {
        log.info("Get /users/search requestParam: [user-id=$userId | user-name=$userName | full-name=$fullName]")
        return ResponseEntity.status(HttpStatus.OK)
            .body(userIntegration.searchUserByIdUserNameOrFullName(userId, userName, fullName))
    }
}
