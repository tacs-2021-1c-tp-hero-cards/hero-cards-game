package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import org.springframework.stereotype.Component

@Component
class MatchIntegration(
    private val matchesMap: HashMap<Long, Match> = hashMapOf()
) {

    fun getAllMatches(): List<Match> = matchesMap.values.toList()

    fun saveMatch(id: Long = calculateId(), match: Match): Match {
        val newMatch = match.copy(id = id)
        matchesMap[id] = newMatch
        return newMatch
    }

    fun calculateId(): Long = matchesMap.size.toLong()

}
