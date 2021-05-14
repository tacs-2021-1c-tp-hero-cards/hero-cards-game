package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.SuperHeroClient
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.CardMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.ImageMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.PowerstatsMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

internal class SuperHeroIntegrationTest {

    private val clientMock = mock(SuperHeroClient::class.java)
    private val mapper = CardMapper(PowerstatsMapper(), ImageMapper())
    private val instance = SuperHeroIntegration(clientMock, mapper)

    private val cardId = "0"
    private val characterName = "characterName"

    @Test
    fun mapCardFromCharacterApi() {
        `when`(clientMock.getCharacter(cardId)).thenReturn(BuilderContextUtils.buildCharacterApi())
        assertEquals(BuilderContextUtils.buildBatmanII(), instance.getCard(cardId))
    }

    @Test
    fun mapCardsFromCharactersSearchApi() {
        `when`(clientMock.getCharacterByName(characterName)).thenReturn(BuilderContextUtils.buildCharactersSearchApi())

        val cards = instance.searchCardByName(characterName)

        assertEquals(3, cards.size)
        assertTrue(cards.contains(BuilderContextUtils.buildBatman()))
        assertTrue(cards.contains(BuilderContextUtils.buildBatmanII()))
        assertTrue(cards.contains(BuilderContextUtils.buildBatmanIII()))
    }
}