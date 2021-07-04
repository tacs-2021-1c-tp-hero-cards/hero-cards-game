package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.SuperHeroClient
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.CardMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.CharacterMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.ImageMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.PowerstatsMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class SuperHeroIntegrationTest {

    private val clientMock = mock(SuperHeroClient::class.java)
    private val cardMapper = CardMapper(PowerstatsMapper(), ImageMapper())
    private val characterMapper = CharacterMapper()

    private val instance = SuperHeroIntegration(clientMock, cardMapper, characterMapper)

    private val cardId = "0"
    private val characterName = "characterName"

    private val batmanCharacter = BuilderContextUtils.buildBatmanCharacter()
    private val batman = BuilderContextUtils.buildBatman()
    private val batmanII = BuilderContextUtils.buildBatmanII()
    private val batmanIII = BuilderContextUtils.buildBatmanIII()

    @Test
    fun getCard() {
        `when`(clientMock.getCharacter(cardId)).thenReturn(BuilderContextUtils.buildCharacterApi())
        val card = instance.getCard(cardId)
        assertEquals(batmanII, card)
    }

    @Test
    fun searchCardByName() {
        `when`(clientMock.getCharacterByName(characterName)).thenReturn(BuilderContextUtils.buildCharactersSearchApi())

        val cards = instance.searchCardByName(characterName)

        assertEquals(3, cards.size)
        assertTrue(cards.contains(batman))
        assertTrue(cards.contains(batmanII))
        assertTrue(cards.contains(batmanIII))
    }

    @Test
    fun getRandomCards(){
        `when`(clientMock.getCharacter(anyString())).thenReturn(BuilderContextUtils.buildCharacterApi())
        instance.totalCard=3

        val cards = instance.getRandomCards(3)

        assertEquals(3, cards.size)
        assertTrue(cards.contains(batmanII))

        verify(clientMock, times(1)).getCharacter("1")
        verify(clientMock, times(1)).getCharacter("2")
        verify(clientMock, times(1)).getCharacter("3")
    }

    @Test
    fun getCharacter() {
        `when`(clientMock.getCharacter(cardId)).thenReturn(BuilderContextUtils.buildCharacterApi())
        val character = instance.getCharacter(cardId)
        assertEquals(batmanCharacter, character)
    }
}