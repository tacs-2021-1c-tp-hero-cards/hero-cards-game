package ar.edu.utn.frba.tacs.tp.api.herocardsgame.request

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player

class MatchUserResponse(match: Match, opponent: Player) {
    val matchId: Long = match.id!!
    val matchStatus: MatchStatus = match.status
    val userOpponent: User = opponent.user
    val isMatchCreatedByUser: Boolean = !opponent.createdMatch
}