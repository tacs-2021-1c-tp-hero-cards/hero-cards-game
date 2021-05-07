package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class MatchIntegrationTest{

    private val matchesInProgressMapMock: HashMap<Long, Match> = hashMapOf()
    private val instance = MatchIntegration(matchesInProgressMapMock)

    private val matchId = 0L
    private val deckTest = Deck(0L, "deckNameTest", listOf(BuilderContextUtils.buildBatman()))

    @Test
    fun getAllMatchesInProgress() {
        val match = Match(matchId, emptyList(), deckTest, MatchStatus.IN_PROGRESS)
        matchesInProgressMapMock[0L] = match

        val allMatchesInProgress = instance.getAllMatches()
        assertEquals(1, allMatchesInProgress.size)

        val found = allMatchesInProgress.first()
        assertEquals(matchId, found.id)
        assertEquals(deckTest, found.deck)
        assertTrue(found.players.isEmpty())
        assertEquals(MatchStatus.IN_PROGRESS, found.status)
    }

    @Test
    fun saveMatch() {
        instance.saveMatch(match = Match(matchId, emptyList(), deckTest, MatchStatus.IN_PROGRESS))

        val allMatchesInProgress = matchesInProgressMapMock.values.toList()

        assertEquals(1, allMatchesInProgress.size)

        val found = allMatchesInProgress.first()
        assertEquals(matchId, found.id)
        assertEquals(deckTest, found.deck)
        assertTrue(found.players.isEmpty())
        assertEquals(MatchStatus.IN_PROGRESS, found.status)
    }

}