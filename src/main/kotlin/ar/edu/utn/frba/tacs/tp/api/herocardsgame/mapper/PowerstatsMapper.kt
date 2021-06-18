package ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.api.AppearanceApi
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.api.PowerstatsApi
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Powerstats
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PowerstatsMapper {

    fun map(powerstats: PowerstatsApi, appearance: AppearanceApi): Powerstats =
        Powerstats(
            height = mapAppearance(appearance.height, "cm"),
            weight = mapAppearance(appearance.weight, "kg"),
            intelligence = powerstats.intelligence.parseInt(),
            speed = powerstats.speed.parseInt(),
            power = powerstats.power.parseInt(),
            combat = powerstats.combat.parseInt(),
            strength = powerstats.strength.parseInt()
        )

    private fun mapAppearance(appearance: List<String>, appearanceType: String) =
        appearance
            .filter { it.contains(appearanceType) }
            .map { extractInt(it) }
            .firstOrNull() ?: -1

    private fun extractInt(string: String): Int = string.filter { it.isDigit() }.toInt()

    private fun String.parseInt(): Int =
        try {
            this.toInt()
        } catch (e: NumberFormatException) {
            -1
        }

}