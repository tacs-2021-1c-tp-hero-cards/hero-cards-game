package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class PowerstatsTest{

    @Test
    fun validatePowers_allPowersAreOk() {
        val powerstats = Powerstats(1,2,3,4,5,6,7)
        val invalidPowerstats = powerstats.calculateInvalidPowers()
        assertTrue(invalidPowerstats.isEmpty())
    }

    @Test
    fun validatePowers_heightAnSpeedAreInvalid() {
        val powerstats = Powerstats(-1,2,3,-1,5,6,7)
        val invalidPowerstats = powerstats.calculateInvalidPowers()
        assertFalse(invalidPowerstats.isEmpty())
        assertTrue(invalidPowerstats.contains("height"))
        assertTrue(invalidPowerstats.contains("speed"))
    }

}