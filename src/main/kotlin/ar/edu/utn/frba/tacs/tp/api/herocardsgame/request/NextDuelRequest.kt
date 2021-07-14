package ar.edu.utn.frba.tacs.tp.api.herocardsgame.request

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import com.fasterxml.jackson.annotation.JsonProperty

data class NextDuelRequest(
    @JsonProperty("duelType")
    val duelType: DuelType?
)