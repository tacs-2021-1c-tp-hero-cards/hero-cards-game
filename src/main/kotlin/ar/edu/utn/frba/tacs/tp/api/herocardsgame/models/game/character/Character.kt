package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.character

data class Character(
    val id: String,
    val name: String,
    val powerstats: Powerstats,
    val biography: Biography,
    val appearance: Appearance,
    val work: Work,
    val connections: Connections,
)