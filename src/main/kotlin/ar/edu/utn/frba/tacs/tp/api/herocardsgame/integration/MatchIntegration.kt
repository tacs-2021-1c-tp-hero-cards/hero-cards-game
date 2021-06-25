package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.DuelHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import org.springframework.stereotype.Component

@Component
class MatchIntegration(
    private val dao: Dao,
    private val deckIntegration: DeckIntegration,
    private val playerIntegration: PlayerIntegration
) {

    fun getMatchById(id: Long): Match {
        val matchEntity = dao.getMatchById(id) ?: throw ElementNotFoundException("match", id.toString())
        val players = matchEntity.playerIds.map { playerIntegration.getPlayerById(it) }
        val deck = deckIntegration.getDeckById(id).searchDeckVersion(matchEntity.deckVersion)
        val duelHistoryList = matchEntity.duelHistoryIds.map { getDuelHistoryById(it) }

        return matchEntity.toModel(players, deck, duelHistoryList)
    }

    private fun getDuelHistoryById(id: Long): DuelHistory {
        val duelHistoryEntity =
            dao.getDuelHistoryById(id) ?: throw ElementNotFoundException("duelHistory", id.toString())
        val playerHistory = playerIntegration.getPlayerHistoryById(duelHistoryEntity.playerId)
        val opponentHistory = playerIntegration.getPlayerHistoryById(duelHistoryEntity.opponentId)

        return duelHistoryEntity.toModel(playerHistory, opponentHistory)
    }

    fun saveMatch(match: Match): Match {
        val savedPlayers = match.players.map { playerIntegration.savePlayer(it) }
        val savedDuelHistoryList = match.duelHistoryList.map { saveDuelHistory(it) }

        return dao.saveMatch(match.copy(players = savedPlayers, duelHistoryList = savedDuelHistoryList))
            .toModel(savedPlayers, match.deck, savedDuelHistoryList)
    }

    private fun saveDuelHistory(duelHistory: DuelHistory): DuelHistory {
        val savedPlayerHistory = playerIntegration.savePlayerHistory(duelHistory.player)
        val savedOpponentHistory = playerIntegration.savePlayerHistory(duelHistory.opponent)

        return dao.saveDuelHistory(duelHistory.copy(player = savedPlayerHistory, opponent = savedOpponentHistory))
            .toModel(savedPlayerHistory, savedOpponentHistory)
    }

}
