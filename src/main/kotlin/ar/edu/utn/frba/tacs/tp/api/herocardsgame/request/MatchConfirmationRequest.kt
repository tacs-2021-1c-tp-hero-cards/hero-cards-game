package ar.edu.utn.frba.tacs.tp.api.herocardsgame.request

import com.fasterxml.jackson.annotation.JsonProperty

data class MatchConfirmationRequest(
    @JsonProperty("confirm")
    val confirm: Boolean
)