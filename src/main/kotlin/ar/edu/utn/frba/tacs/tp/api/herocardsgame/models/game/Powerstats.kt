package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import kotlin.collections.HashMap
import kotlin.reflect.full.memberProperties

data class Powerstats(
    val height: Int,
    val weight: Int,
    val intelligence: Int,
    val speed: Int,
    val power: Int,
    val combat: Int,
    val strength: Int,
) {
    fun calculateInvalidPowers(): List<DuelType> =
        getPowerstatsMap()
            .filter { it.value == -1 }
            .map { it.key }
            .toList()

    fun calculateBetterPowerstats(): DuelType = sortedPowerstats().first().first

    fun calculateWorstPowerstats(): DuelType = sortedPowerstats().last().first

    fun calculateMediumPowerstats(): DuelType  = sortedPowerstats()[3].first

    fun calculateRandomPowerstats(): DuelType = getPowerstatsMap().entries.shuffled().first().key

    private fun getPowerstatsMap(): HashMap<DuelType, Int> {
        val powerMap = hashMapOf<DuelType, Int>()

        val propertiesByName = Powerstats::class.memberProperties.associateBy { it.name }
        propertiesByName.entries.forEach {
            val duelType = DuelType.valueOf(it.key.toUpperCase())
            powerMap[duelType] = propertiesByName[it.key]?.get(this) as Int
        }

        return powerMap
    }

    private fun sortedPowerstats(): List<Pair<DuelType, Int>> =
        getPowerstatsMap()
            .filterNot { DuelType.WEIGHT == it.key }
            .toList()
            .sortedByDescending { (_, value) -> value }

}