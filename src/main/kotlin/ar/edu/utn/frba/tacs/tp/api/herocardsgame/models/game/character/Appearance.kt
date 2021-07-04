package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.character

data class Appearance(
    val gender: String?,
    val race: String?,
    val height: List<String>,
    val weight: List<String>,
    val eyeColor: String?,
    val hairColor: String?
)