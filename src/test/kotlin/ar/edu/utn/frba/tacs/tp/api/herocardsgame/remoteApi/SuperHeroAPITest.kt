package ar.edu.utn.frba.tacs.tp.api.herocardsgame.remoteApi

import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

internal class SuperHeroAPITest {

    private val client = SuperHeroAPI()

    @Test
    fun getCharacter() {
        val character = client.getCharacter("2")
        assertNotNull(character)
        if (character != null){
            assertEquals("2", character.id)
            assertEquals("Abe Sapien", character.name)
            assertEquals(85, character.powerstats.combat)
            assertEquals(65, character.powerstats.durability)
            assertEquals(88, character.powerstats.intelligence)
            assertEquals(100, character.powerstats.power)
            assertEquals(35, character.powerstats.speed)
            assertEquals(28, character.powerstats.strength)
        }
    }

    @Test
    fun getImage() {
        val image = client.getImage("2")
        assertNotNull(image)
        if (image != null){
            assertEquals("https://www.superherodb.com/pictures2/portraits/10/100/956.jpg", image.url)
        }
    }
}