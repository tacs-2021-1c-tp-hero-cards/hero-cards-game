package ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.api.AppearanceApi
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.api.PowerstatsApi
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Powerstats
import org.springframework.stereotype.Component

@Component
class PowerstatsMapper {

    fun map(powerstats: PowerstatsApi, appearance: AppearanceApi): Powerstats =
        Powerstats(
            height = mapHeight(appearance.height),
            weight = mapWeight(appearance.weight),
            intelligence = powerstats.intelligence.toInt(),
            speed = powerstats.speed.toInt(),
            power = powerstats.power.toInt(),
            combat = powerstats.combat.toInt(),
            strength = powerstats.strength.toInt()
        )

    private fun mapHeight(height: List<String>): Int =
        height
            .filter { it.contains("cm") }
            .map { extractInt(it) }
            .first()

    private fun mapWeight(weight: List<String>): Int =
        weight
            .filter { it.contains("kg") }
            .map { extractInt(it) }
            .first()

    private fun extractInt(string: String): Int = string.filter { it.isDigit() }.toInt()

}