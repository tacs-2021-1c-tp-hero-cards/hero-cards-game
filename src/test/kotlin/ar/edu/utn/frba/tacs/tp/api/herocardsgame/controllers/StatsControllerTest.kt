package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import org.junit.jupiter.api.BeforeEach
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

internal class StatsControllerTest {

    lateinit var instance: StatsController

    @BeforeEach
    fun init() {
        val context = AnnotationConfigWebApplicationContext()
        context.register(StatsController::class.java)

        context.refresh()
        context.start()

        instance = context.getBean(StatsController::class.java)
    }
}