package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.DuelHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.PlayerHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType

class DuelHistoryEntity(id: Long? = null, duelHistory: DuelHistory) {

    val id: Long = id ?: duelHistory.id!!
    val playerVersion: Long = duelHistory.player.version!!
    val opponentVersion: Long = duelHistory.opponent.version!!
    val duelType: String = duelHistory.duelType.name
    val duelResult: String = duelHistory.duelResult.name

    fun toModel(player: PlayerHistory, opponent: PlayerHistory): DuelHistory =
        DuelHistory(id, player, opponent, DuelType.valueOf(duelType), DuelResult.valueOf(duelResult))
}
