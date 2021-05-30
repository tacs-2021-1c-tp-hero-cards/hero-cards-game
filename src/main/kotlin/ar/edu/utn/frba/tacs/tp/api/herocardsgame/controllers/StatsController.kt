package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.UserStatsResponse
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.StatsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/stats")
class StatsController(val statsService: StatsService) {

    @GetMapping("/scoreboards")
    fun getScoreBoards(): ResponseEntity<List<UserStatsResponse>> =
        ResponseEntity.status(HttpStatus.OK).body(statsService.buildAllUserStats())

    @GetMapping("/user/{user-id}")
    fun getStatsByUser(@PathVariable("user-id") userId: String): ResponseEntity<UserStatsResponse> =
        try {
            ResponseEntity.status(HttpStatus.OK).body(statsService.buildUserStats(userId))
        } catch (e: ElementNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

}
