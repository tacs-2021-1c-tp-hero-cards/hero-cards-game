package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.*
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Authentication
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.ActivateUserSessionRequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateIARequest
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.CreateUserRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@CrossOrigin(origins = ["http://localhost:5000"], allowedHeaders = ["*"])
class UsersController(
    private val userIntegration: UserIntegration
) :
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
                createUserRequest.userName,
                createUserRequest.fullName,
                createUserRequest.isAdmin,
                createUserRequest.buildPasswordHash()
            )

            reportResponse(HttpStatus.OK, response.id!!)
        } catch (e: InvalidHumanUserException) {
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
    fun logOut(@RequestHeader(value = "x-user-token") token: String): ResponseEntity<Void> =
        try {
            reportRequest(
                method = RequestMethod.POST,
                path = "/logOut",
                requestHeader = hashMapOf("x-user-token" to token),
                body = null
            )
            userIntegration.disableUserSession(token)
            reportResponse(HttpStatus.OK)
        } catch (e: ElementNotFoundException) {
            reportError(e, HttpStatus.BAD_REQUEST)
        }

    /**
     * @param userId, userName, fullName
     * @return list of user
     */
    @GetMapping("/users/human")
    fun getHumanUserByIdUserNameFullNameOrToken(
        @RequestParam(value = "user-id") userId: String? = null,
        @RequestParam(value = "user-name") userName: String? = null,
        @RequestParam(value = "full-name") fullName: String? = null,
        @RequestParam(value = "token") token: String? = null
    ): ResponseEntity<List<User>> {
        reportRequest(
            method = RequestMethod.GET,
            path = "/users/human/search",
            body = null,
            requestParams = hashMapOf(
                "user-id" to userId,
                "user-name" to userName,
                "full-name" to fullName,
                "token" to token
            )
        )
        val response = userIntegration.searchHumanUserByIdUserNameFullNameOrToken(userId, userName, fullName, token)
        return reportResponse(HttpStatus.OK, response)
    }

    /**
     * @param createIARequest
     * @return IA
     */
    @PostMapping("/admin/users/ia")
    fun createIA(@RequestBody createIARequest: CreateIARequest): ResponseEntity<Long> =
        try {
            reportRequest(
                method = RequestMethod.POST,
                path = "/admin/users/ia",
                body = createIARequest
            )
            val response = userIntegration.createUser(createIARequest.userName, createIARequest.difficulty)
            reportResponse(HttpStatus.OK, response.id!!)
        } catch (e: InvalidIAUserException) {
            reportError(e, HttpStatus.BAD_REQUEST)
        } catch (e: InvalidDifficultyException) {
            reportError(e, HttpStatus.BAD_REQUEST)
        }

    /**
     * @param userId, userName, difficulty
     * @return list of user
     */
    @GetMapping("/users/ia")
    fun getIAUserByIdUserNameFullNameOrToken(
        @RequestParam(value = "user-id") userId: String? = null,
        @RequestParam(value = "user-name") userName: String? = null,
        @RequestParam(value = "difficulty") difficulty: String? = null
    ): ResponseEntity<List<User>> {
        reportRequest(
            method = RequestMethod.GET,
            path = "/users/ia/search",
            body = null,
            requestParams = hashMapOf(
                "user-id" to userId,
                "user-name" to userName,
                "difficulty" to difficulty
            )
        )
        val response = userIntegration.searchIAUserByIdUserNameFullNameOrToken(userId, userName, difficulty)
        return reportResponse(HttpStatus.OK, response)
    }
}
