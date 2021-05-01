package ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.api.AppearanceApi
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.api.CharacterApi
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.api.PowerstatsApi
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.FileConstructorUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class PowerstatsMapperTest {

    var instance = PowerstatsMapper()

    @Test
    fun mapPowerstatsFromPowerstatsApiAndAppearanceApi() {

        val appearanceApi =
            FileConstructorUtils.createFromFile(
                "src/test/kotlin/ar/edu/utn/frba/tacs/tp/api/herocardsgame/json/api/appearance.json",
                AppearanceApi::class.java
            )

        val powerstatsApi =
            FileConstructorUtils.createFromFile(
                "src/test/kotlin/ar/edu/utn/frba/tacs/tp/api/herocardsgame/json/api/powerstats.json",
                PowerstatsApi::class.java
            )

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