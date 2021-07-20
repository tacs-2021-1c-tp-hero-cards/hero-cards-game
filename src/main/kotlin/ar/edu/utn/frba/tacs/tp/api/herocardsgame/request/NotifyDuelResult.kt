package ar.edu.utn.frba.tacs.tp.api.herocardsgame.request

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.PlayerHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType

data class NotifyDuelResult(
    val matchId: Long?,
    val player: PlayerHistory,
    val opponent: PlayerHistory,
    val duelType: DuelType,
    val duelResult: DuelResult
)