package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck.DeckHistoryEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match.DuelHistoryEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match.MatchEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user.UserEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class MatchRepositoryTest @Autowired constructor(
    val entityManager: TestEntityManager,
    val instance: MatchRepository
) {

    private val id = 0L

    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    private val userName: String = "userName"
    private val fullName: String = "fullName"
    private val password: String = "password"
    private val token: String = "token"
    private val winCount: Int = 1
    private val tieCount: Int = 0
    private val loseCount: Int = 2
    private val inProgressCount: Int = 4
    private val iADifficulty = IADifficulty.HARD

    private val deckName = "deckName"
    private val matchStatus = MatchStatus.IN_PROGRESS

    private val duelType = DuelType.SPEED
    private val duelResult = DuelResult.TIE

    private val iaEntity =
        UserEntity(
            id,
            userName,
            UserType.IA,
            winCount,
            tieCount,
            loseCount,
            inProgressCount,
            difficulty = iADifficulty
        )

    private val humanEntity =
        UserEntity(
            id + 1L,
            userName,
            UserType.HUMAN,
            winCount, tieCount, loseCount, inProgressCount,
            fullName,
            password,
            token,
            false
        )

    private val deckHistory =
        DeckHistoryEntity(name = deckName, cardIds = batman.id.toString() + "," + flash.id.toString())

    private val duelHistory = DuelHistoryEntity(
        playerAvailableCardIds = batman.id.toString(),
        playerPrizeCardIds = flash.id.toString(),
        opponentAvailableCardIds = flash.id.toString(),
        opponentPrizeCardIds = batman.id.toString(),
        duelType = duelType,
        duelResult = duelResult
    )
    private val matchEntity =
        MatchEntity(
            player = listOf(humanEntity, iaEntity),
            playerIdTurn = id + 1L,
            playerAvailableCardIds = batman.id.toString(),
            playerPrizeCardIds = flash.id.toString(),
            opponentAvailableCardIds = flash.id.toString(),
            opponentPrizeCardIds = batman.id.toString(),
            deckId = 0L,
            deckHistory = deckHistory,
            status = matchStatus,
            duelHistory = listOf(duelHistory)
        )

    @Nested
    inner class GetById {

//        @Test
//        TODO: Persistir relaciones anteriores como usuarios
//        fun `Search match by id`() {
//            val newMatch = matchEntity.copy(player = listOf(humanEntity, iaEntity))
//            entityManager.persist(newMatch)
//
//            val found = instance.getById(matchEntity.id!!)
//            assertEquals(newMatch, found)
//        }

        @Test
        fun `Search match by id and can't find`() {
            val found = instance.getById(1L)
            assertNull(found)
        }

    }

}