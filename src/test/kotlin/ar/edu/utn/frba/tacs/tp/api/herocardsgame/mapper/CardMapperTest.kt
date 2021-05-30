package ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class CardMapperTest {

    val instance = CardMapper(PowerstatsMapper(), ImageMapper())

    @Test
    fun mapCardFromCharacterApi() {
        val characterApi = BuilderContextUtils.buildCharacterApi()
        assertEquals(BuilderContextUtils.buildBatmanII(), instance.map(characterApi))
    }

    @Test
    fun mapCardsFromCharactersSearchApi() {
        val charactersSearchApi = BuilderContextUtils.buildCharactersSearchApi()

        val cards = instance.mapCharactersSearch(charactersSearchApi)

        assertEquals(3, cards.size)
        assertTrue(cards.contains(BuilderContextUtils.buildBatman()))
        assertTrue(cards.contains(BuilderContextUtils.buildBatmanII()))
        assertTrue(cards.contains(BuilderContextUtils.buildBatmanIII()))
    }

}