package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import org.springframework.stereotype.Component

@Component
class DuelStrategy {

    fun getDuelStrategy(duelType: DuelType): (Card, Card) -> DuelResult =
        when (duelType) {
            DuelType.HEIGHT -> duelHeight
            DuelType.WEIGHT -> duelWeight
            DuelType.INTELLIGENCE -> duelIntelligence
            DuelType.SPEED -> duelSpeed
            DuelType.POWER -> duelPower
            DuelType.COMBAT -> duelCombat
            DuelType.STRENGTH -> duelStrength
        }

    fun duelMax(powerstat: Int, otherPowerstat: Int): DuelResult =
        when {
            powerstat > otherPowerstat -> DuelResult.WIN
            powerstat < otherPowerstat -> DuelResult.LOSE
            else -> DuelResult.TIE
        }

    fun duelMin(powerstat: Int, otherPowerstat: Int): DuelResult =
        when {
            powerstat < otherPowerstat -> DuelResult.WIN
            powerstat > otherPowerstat -> DuelResult.LOSE
            else -> DuelResult.TIE
        }

    val duelHeight: (Card, Card) -> DuelResult = { card, otherCard ->
        duelMax(card.powerstats.height, otherCard.powerstats.height)
    }

    val duelWeight: (Card, Card) -> DuelResult = { card, otherCard ->
        duelMin(card.powerstats.weight, otherCard.powerstats.weight)
    }

    val duelIntelligence: (Card, Card) -> DuelResult = { card, otherCard ->
        duelMax(card.powerstats.intelligence, otherCard.powerstats.intelligence)
    }

    val duelSpeed: (Card, Card) -> DuelResult = { card, otherCard ->
        duelMax(card.powerstats.speed, otherCard.powerstats.speed)
    }

    val duelPower: (Card, Card) -> DuelResult = { card, otherCard ->
        duelMax(card.powerstats.power, otherCard.powerstats.power)
    }

    val duelCombat: (Card, Card) -> DuelResult = { card, otherCard ->
        duelMax(card.powerstats.combat, otherCard.powerstats.combat)
    }

    val duelStrength: (Card, Card) -> DuelResult = { card, otherCard ->
        duelMax(card.powerstats.strength, otherCard.powerstats.strength)
    }

}