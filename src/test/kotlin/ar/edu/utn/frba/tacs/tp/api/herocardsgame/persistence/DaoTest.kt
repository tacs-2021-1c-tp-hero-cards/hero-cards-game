package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.CardEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DaoTest {

    lateinit var instance: Dao

    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()
    private val batmanII = BuilderContextUtils.buildBatmanII()

    @Test
    fun calculateId() {
        instance = Dao(cardMap = hashMapOf())

        val calculateId = instance.calculateId(batman)
        assertEquals(0, calculateId)
    }

    @Nested
    inner class CardEntityTest {

        @Nested
        inner class GetAllCard {

            @Test
            fun `Get all cards if non exist users in the database`() {
                instance = Dao(cardMap = hashMapOf())

                val allCard = instance.getAllCard()
                assertTrue(allCard.isEmpty())
            }

            @Test
            fun `Get all cards if exist users in the database`() {
                val cardEntity = CardEntity(batman)
                instance = Dao(cardMap = hashMapOf(0L to cardEntity))

                val allCard = instance.getAllCard()
                assertEquals(1, allCard.size)

                val found = allCard.first()
                assertEquals(cardEntity, found)
            }

        }

        @Test
        fun saveCard() {
            instance = Dao()
            instance.saveCard(batman)

            val allCard = instance.getAllCard()
            assertEquals(1, allCard.size)

            val foundCard = allCard.first()
            assertEquals(batman.id, foundCard.id)
            assertEquals(batman.name, foundCard.name)
            assertEquals(batman.imageUrl, foundCard.imageUrl)
            assertEquals(batman.powerstats.height, foundCard.height)
            assertEquals(batman.powerstats.weight, foundCard.weight)
            assertEquals(batman.powerstats.intelligence, foundCard.intelligence)
            assertEquals(batman.powerstats.speed, foundCard.speed)
            assertEquals(batman.powerstats.power, foundCard.power)
            assertEquals(batman.powerstats.combat, foundCard.combat)
            assertEquals(batman.powerstats.strength, foundCard.strength)
            assertNotNull(foundCard.lastUse)
        }

        @Nested
        inner class RemoveLastUse {

            @Test
            fun `Remove last card used`() {
                val batmanEntity = CardEntity(batman)
                val flashEntity = CardEntity(flash)
                val batmanEntityII = CardEntity(batmanII)
                instance = Dao(
                    cardMap = hashMapOf(
                        batman.id to batmanEntity,
                        flash.id to flashEntity,
                        batmanII.id to batmanEntityII
                    )
                )

                instance.removeLastUse()

                val allCard = instance.getAllCard()
                assertEquals(2, allCard.size)
                assertTrue(allCard.contains(flashEntity))
                assertTrue(allCard.contains(batmanEntityII))
            }

            @Test
            fun `Non remove last card if it's empty`() {
                instance = Dao()

                instance.removeLastUse()

                val allCard = instance.getAllCard()
                assertEquals(0, allCard.size)
            }

        }
    }

}