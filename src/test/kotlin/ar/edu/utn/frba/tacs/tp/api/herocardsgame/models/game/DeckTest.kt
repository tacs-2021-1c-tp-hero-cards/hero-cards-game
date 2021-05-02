package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.FileConstructorUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DeckTest{

    private val deckId = 0L
    private val deckName = "decktest"

    @Test
    fun addCard(){
        val deck = Deck(deckId, deckName, listOf(buildBatman()))
        deck.addCard(buildFlash())
        assertEquals(2, deck.cards.size)
    }

    @Test
    fun removeCard(){
        val flash = buildFlash()
        val deck = Deck(deckId, deckName, listOf(buildBatman(), flash))
        deck.removeCard(flash.id)
        assertEquals(1, deck.cards.size)
    }

    private fun buildBatman() = FileConstructorUtils.createFromFile(
        "src/test/kotlin/ar/edu/utn/frba/tacs/tp/api/herocardsgame/json/card/Batman.json",
        Card::class.java
    )

    private fun buildFlash() = FileConstructorUtils.createFromFile(
        "src/test/kotlin/ar/edu/utn/frba/tacs/tp/api/herocardsgame/json/card/Flash.json",
        Card::class.java
    )

}