package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelStrategy
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType

data class Card(
    val id: Long,
    val name: String,
    val powerstats: Powerstats
) {
    fun duel(otherCard: Card, duelType: DuelType): DuelResult =
        DuelStrategy().getDuelStrategy(duelType).invoke(this, otherCard)
}