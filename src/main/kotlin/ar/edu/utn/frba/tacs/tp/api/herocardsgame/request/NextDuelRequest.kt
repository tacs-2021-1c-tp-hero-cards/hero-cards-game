package ar.edu.utn.frba.tacs.tp.api.herocardsgame.request

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType

data class NextDuelRequest(
    val token: String?,
    val duelType: DuelType?
)