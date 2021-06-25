package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.PlayerHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType

data class DuelHistory(
    val id: Long? = null,
    val player: PlayerHistory,
    val opponent: PlayerHistory,
    val duelType: DuelType,
    val duelResult: DuelResult
) {
    constructor(
        player: Player,
        opponent: Player,
        duelType: DuelType,
        duelResult: DuelResult
    ) : this(
        player = PlayerHistory(player),
        opponent = PlayerHistory(opponent),
        duelType = duelType,
        duelResult = duelResult
    )
}
