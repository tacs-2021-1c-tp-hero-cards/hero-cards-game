package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.UserStatsResponse
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.StatsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/stats")
@CrossOrigin(origins = ["http://localhost:3000"], allowedHeaders = ["*"])
class StatsController(val statsService: StatsService) :
    AbstractController<StatsController>(StatsController::class.java) {

    @GetMapping("/scoreboards")
    fun getScoreBoards(): ResponseEntity<List<UserStatsResponse>> {
        reportRequest(
            method = RequestMethod.GET,
            path = "/admin/stats/scoreboards",
            body = null
        )
        val response = statsService.buildAllUserStats()
        return reportResponse(HttpStatus.OK, response)
    }

    @GetMapping("/user/{user-id}/{user-type}")
    fun getStatsByUser(
        @PathVariable("user-id") userId: String,
        @PathVariable("user-type") userType: String
    ): ResponseEntity<UserStatsResponse> =
        try {
            reportRequest(
                method = RequestMethod.GET,
                path = "/admin/stats/user/{user-id}/{user-type}",
                body = null,
                pathVariables = hashMapOf("user-id" to userId, "user-type" to userType)
            )

            val response = statsService.buildUserStats(userId)
            reportResponse(HttpStatus.OK, response)
        } catch (e: ElementNotFoundException) {
            reportError(e, HttpStatus.NOT_FOUND)
        }

}
