package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import org.springframework.stereotype.Component

@Component
class MatchIntegration(
    private val matchesMap: HashMap<Long, Match> = hashMapOf()
) {

    fun getAllMatches(): List<Match> = matchesMap.values.toList()

    fun saveMatch(match: Match): Match {
        val id = calculateId()
        match.updateId(id)
        matchesMap[id] = match
        return match
    }

    fun calculateId(): Long = matchesMap.size.toLong()

    fun updateStatus(id: Long, newStatus: MatchStatus): Match{
        val match = matchesMap[id]!!
        match.updateStatus(newStatus)
        return match
    }
}
