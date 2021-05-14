package ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ImageMapperTest {

    val instance = ImageMapper()

    @Test
    fun mapImageUrlFromCharacterApi() {
        val imageApi = BuilderContextUtils.buildImageApi()
        assertEquals("https://www.superherodb.com/pictures2/portraits/10/100/639.jpg", instance.map(imageApi))
    }
}