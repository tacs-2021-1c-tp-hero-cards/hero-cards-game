package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck.DeckEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck.DeckHistoryEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class DeckRepositoryTest @Autowired constructor(
    val entityManager: TestEntityManager,
    val instance: DeckRepository
) {

    private val name = "nameTest"
    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()
    private val batmanII = BuilderContextUtils.buildBatmanII()

    private val deckHistoryEntity = DeckHistoryEntity(
        name = name,
        cardIds = listOf(batman, batmanII).joinToString(separator = ",") { it.id.toString() })

    private val deckEntity =
        DeckEntity(
            name = name,
            cardIds = listOf(batman, batmanII, flash).joinToString(separator = ",") { it.id.toString() },
            deckHistory = listOf(deckHistoryEntity)
        )

    @Nested
    inner class GetById {

        @Test
        fun `Search deck by id`() {
            entityManager.persist(deckEntity)

            val found = instance.getById(deckEntity.id!!)
            assertEquals(deckEntity, found)
        }

        @Test
        fun `Search user by id and can't find`() {
            val found = instance.getById(1L)
            assertNull(found)
        }

    }

    @Nested
    inner class FindDeckByIdAndName {

        @Test
        fun `Find deck by id and name and id and name don't match`() {
            entityManager.persist(deckEntity)

            val found = instance.findDeckByIdAndName((deckEntity.id!! + 1).toString(), "nameTest2")
            assertNull(found)
        }

        @Test
        fun `Find deck by id and name and only match id`() {
            entityManager.persist(deckEntity)

            val found = instance.findDeckByIdAndName(deckEntity.id.toString(), "nameTest2")
            assertNull(found)
        }

        @Test
        fun `Find deck by id and name and only match name`() {
            entityManager.persist(deckEntity)

            val found = instance.findDeckByIdAndName((deckEntity.id!! +1).toString(), name)
            assertNull(found)
        }

        @Test
        fun `Find human by username and password and find`() {
            entityManager.persist(deckEntity)

            val found = instance.findDeckByIdAndName(deckEntity.id.toString(), name)
            assertEquals(deckEntity, found)
        }

    }

}