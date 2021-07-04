package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.character

data class Biography(
    val fullName: String?,
    val alterEgos: String?,
    val aliases: List<String>,
    val placeOfBirth: String?,
    val firstAppearance: String?,
    val publisher: String?,
    val alignment: String?
)