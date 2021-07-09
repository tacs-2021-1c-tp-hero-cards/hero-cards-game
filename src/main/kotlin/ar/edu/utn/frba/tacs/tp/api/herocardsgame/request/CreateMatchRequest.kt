package ar.edu.utn.frba.tacs.tp.api.herocardsgame.request;

data class CreateMatchRequest(
        val humanUserIds: List<String>,
        val iaUserIds: List<String>,
        val deckId: String
)