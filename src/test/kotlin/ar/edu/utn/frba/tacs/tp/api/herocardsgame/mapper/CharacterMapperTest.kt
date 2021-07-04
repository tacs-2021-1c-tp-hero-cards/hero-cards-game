package ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class CharacterMapperTest{

    val instance = CharacterMapper()

    @Test
    fun mapCharacterFromCharacterApi() {
        val characterApi = BuilderContextUtils.buildCharacterApi()
        assertEquals(BuilderContextUtils.buildBatmanCharacter(), instance.map(characterApi))
    }

}