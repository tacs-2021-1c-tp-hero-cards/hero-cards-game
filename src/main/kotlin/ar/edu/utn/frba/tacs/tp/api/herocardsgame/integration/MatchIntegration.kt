package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import org.springframework.stereotype.Component

@Component
class MatchIntegration(
    private val matchesInProgressMap: HashMap<Long, Match> = hashMapOf()
) {

    fun getAllMatchesInProgress(): List<Match> = matchesInProgressMap.values.toList()

    fun saveMatch(match: Match): Match {
        val id = calculateId()
        match.updateId(id)
        matchesInProgressMap[id] = match
        return match
    }

    fun calculateId(): Long = matchesInProgressMap.size.toLong()

    fun updateStatus(id: Long, newStatus: MatchStatus): Match{
        val match = matchesInProgressMap[id]!!
        match.updateStatus(newStatus)
        return match
    }
}
