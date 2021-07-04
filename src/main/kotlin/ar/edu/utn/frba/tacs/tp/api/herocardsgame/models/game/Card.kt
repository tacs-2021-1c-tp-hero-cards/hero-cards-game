package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelStrategy
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class Card(
    val id: Long,
    val name: String,
    val powerstats: Powerstats,
    val imageUrl: String
) {

    @Transient
    private val log: Logger = LoggerFactory.getLogger(Card::class.java)

    fun duel(otherCard: Card, duelType: DuelType): DuelResult =
        DuelStrategy().getDuelStrategy(duelType).invoke(this, otherCard)

    fun isInvalidPowerstats(): Boolean {
        val invalidPowers = powerstats.calculateInvalidPowers()
        val notEmpty = invalidPowers.isNotEmpty()

        if (notEmpty) {
            val invalidPowersString = invalidPowers.joinToString(
                prefix = "[",
                separator = ", ",
                postfix = "]",
            )
            log.warn("Card with id: $id has invalid $invalidPowersString attributes")
        }

        return notEmpty
    }
}