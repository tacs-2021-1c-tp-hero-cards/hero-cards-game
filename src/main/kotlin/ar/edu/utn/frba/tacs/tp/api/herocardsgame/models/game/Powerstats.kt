package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

data class Powerstats(
    val height: Int,
    val weight: Int,
    val intelligence: Int,
    val speed: Int,
    val power: Int,
    val combat: Int,
    val strength: Int,
){
    fun calculateInvalidPowers(): List<String> {
        val invalidPowers = mutableListOf<String>()

        if(height == -1) invalidPowers.add("height")
        if(weight == -1) invalidPowers.add("weight")
        if(intelligence == -1) invalidPowers.add("intelligence")
        if(speed == -1) invalidPowers.add("speed")
        if(power == -1) invalidPowers.add("power")
        if(combat == -1) invalidPowers.add("combat")
        if(strength == -1) invalidPowers.add("strength")

        return invalidPowers.toList()
    }
}