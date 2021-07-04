package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidUserException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Authentication
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.ActivateUserSessionRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateUserRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@CrossOrigin(origins = ["http://localhost:3000"], allowedHeaders = ["*"])
class UsersController(private val userIntegration: UserIntegration) :
    AbstractController<UsersController>(UsersController::class.java) {

    /**
     * @param createUserRequest
     * @return userId
     */
    @PostMapping("/signUp")
    fun signUp(@RequestBody createUserRequest: CreateUserRequest): ResponseEntity<Long> =
        try {
            reportRequest(method = RequestMethod.POST, path = "/signUp", body = createUserRequest)
            val response = userIntegration.createUser(
                createUserRequest.userName, createUserRequest.fullName, createUserRequest.buildPasswordHash()
            )
            reportResponse(HttpStatus.OK, response.id!!)
        } catch (e: InvalidUserException) {
            reportError(e, HttpStatus.BAD_REQUEST)
        }

    /**
     * @param activateUserSessionRequest
     * @return authentication
     */
    @PostMapping("/logIn")
    fun logIn(@RequestBody activateUserSessionRequest: ActivateUserSessionRequest): ResponseEntity<Authentication> =
        try {
            reportRequest(method = RequestMethod.POST, path = "/logIn", body = activateUserSessionRequest)
            val response =
                userIntegration.activateUserSession(
                    activateUserSessionRequest.userName,
                    activateUserSessionRequest.buildPasswordHash()
                )
            reportResponse(HttpStatus.OK, Authentication(response.token!!))
        } catch (e: ElementNotFoundException) {
            reportError(e, HttpStatus.BAD_REQUEST)
        }

    /**
     * @param deactivateUserSessionRequest
     * @return
     */
    @PostMapping("/logOut")
    fun logOut(@RequestBody tokenMap: HashMap<String, String>): ResponseEntity<Void> =
        try {
            reportRequest(method = RequestMethod.POST, path = "/logOut", body = tokenMap)
            userIntegration.disableUserSession(tokenMap["token"]!!)
            reportResponse(HttpStatus.OK)
        } catch (e: ElementNotFoundException) {
            reportError(e, HttpStatus.BAD_REQUEST)
        }

    /**
     * @param userId, userName, fullName
     * @return list of user
     */
    @GetMapping("/users/search")
    fun getUserByIdUserNameOrFullName(
        @RequestParam(value = "user-id") userId: String? = null,
        @RequestParam(value = "user-name") userName: String? = null,
        @RequestParam(value = "full-name") fullName: String? = null,
        @RequestParam(value = "token") token: String? = null
    ): ResponseEntity<List<User>> {
        reportRequest(
            method = RequestMethod.GET,
            path = "/users/search",
            body = null,
            requestParams = hashMapOf(
                "user-id" to userId,
                "user-name" to userName,
                "full-name" to fullName,
                "token" to token
            )
        )
        val response = userIntegration.searchUserByIdUserNameFullNameOrToken(userId, userName, fullName)
        return reportResponse(HttpStatus.OK, response)
    }
}
