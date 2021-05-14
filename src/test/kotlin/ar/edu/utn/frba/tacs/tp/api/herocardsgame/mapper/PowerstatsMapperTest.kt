package ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PowerstatsMapperTest {

    var instance = PowerstatsMapper()

    @Test
    fun mapPowerstatsFromPowerstatsApiAndAppearanceApi() {
        val appearanceApi = BuilderContextUtils.buildAppearanceApi()
        val powerstatsApi = BuilderContextUtils.buildPowerstatsApi()

        val powerstats = instance.map(powerstatsApi, appearanceApi)

        assertEquals(188, powerstats.height)
        assertEquals(95, powerstats.weight)
        assertEquals(100, powerstats.intelligence)
        assertEquals(27, powerstats.speed)
        assertEquals(47, powerstats.power)
        assertEquals(100, powerstats.combat)
        assertEquals(26, powerstats.strength)
    }
}