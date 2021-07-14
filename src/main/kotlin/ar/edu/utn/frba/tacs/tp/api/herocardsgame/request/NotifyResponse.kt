package ar.edu.utn.frba.tacs.tp.api.herocardsgame.request

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.User

data class NotifyResponse(
    val matchId: Long,
    val user: User
)
