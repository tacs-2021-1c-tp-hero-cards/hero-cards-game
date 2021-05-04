package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

data class Match(
    var id: Long? = null,
    val players: List<Player>,
    val deck: Deck,
    var status: MatchStatus
){
    fun updateId(newId: Long){
        id = newId
    }

    fun updateStatus(newStatus: MatchStatus){
        status = newStatus
    }
}