package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Stats
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.*
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.DuelHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.PlayerHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.*
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck.DeckEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck.DeckHistoryEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match.DuelHistoryEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match.MatchEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.player.PlayerEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.player.PlayerHistoryEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DaoTest {

    lateinit var instance: Dao

    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()
    private val batmanII = BuilderContextUtils.buildBatmanII()

    private val deck = Deck(0L, 1L, "deckNewName", listOf(batman, batmanII, flash))
    private val deckHistory = DeckHistory(0L, 0L, "deckName", listOf(batman, batmanII, flash))

    private val user = User(0L, "userName", "fullName", "password", "token", Stats())
    private val player = Player(0L, user, listOf(batman), listOf(flash))
    private val playerHistory = PlayerHistory(0L, 0L, batman, listOf(batman), listOf(flash))
    private val match = Match(0L, listOf(player), deckHistory, MatchStatus.IN_PROGRESS)
    private val duelHistory =
        DuelHistory(0L, playerHistory, playerHistory.copy(id = 1L, version = 1), DuelType.SPEED, DuelResult.WIN)

    @Nested
    inner class CalculateId {

        @Test
        fun calculateIdToUser() {
            instance = Dao(userMap = hashMapOf(0L to UserEntity(user = user), 1L to UserEntity(user = user)))

            val calculateId = instance.calculateId(user)
            assertEquals(2, calculateId)
        }

        @Test
        fun calculateIdToDeck() {
            instance = Dao(deckMap = hashMapOf(0L to DeckEntity(deck = deck)))

            val calculateId = instance.calculateId(deck)
            assertEquals(1, calculateId)
        }

        @Test
        fun calculateIdToMatch() {
            instance = Dao(
                matchMap = hashMapOf(
                    0L to MatchEntity(match = match),
                    1L to MatchEntity(match = match),
                    2L to MatchEntity(match = match)
                )
            )

            val calculateId = instance.calculateId(match)
            assertEquals(3, calculateId)
        }

        @Test
        fun calculateIdToCard() {
            instance = Dao(cardMap = hashMapOf())

            val calculateId = instance.calculateId(batman)
            assertEquals(0, calculateId)
        }

        @Test
        fun calculateIdToPlayer() {
            instance = Dao(
                playerMap = hashMapOf(
                    0L to PlayerEntity(player = player),
                    1L to PlayerEntity(player = player),
                    2L to PlayerEntity(player = player),
                    3L to PlayerEntity(player = player)
                )
            )

            val calculateId = instance.calculateId(player)
            assertEquals(4, calculateId)
        }

    }

    @Nested
    inner class CalculateVersion {

        @Test
        fun calculateVersionToDeck() {
            instance = Dao(deckHistoryMap = hashMapOf(0L to DeckHistoryEntity(deckHistory)))

            val calculateId = instance.calculateVersion(deckHistory)
            assertEquals(1, calculateId)
        }

        @Test
        fun calculateVersionToPlayerHistory() {
            instance = Dao(
                playerHistoryMap = hashMapOf(
                    0L to PlayerHistoryEntity(0L, playerHistory),
                    1L to PlayerHistoryEntity(1L, playerHistory),
                    2L to PlayerHistoryEntity(2L, playerHistory)
                )
            )

            val calculateId = instance.calculateVersion(playerHistory)
            assertEquals(3, calculateId)
        }

    }

    @Nested
    inner class UserEntityTest {

        @Nested
        inner class GetAllUser {

            @Test
            fun `Get all users if non exist users in the database`() {
                instance = Dao(userMap = hashMapOf())

                val allUser = instance.getAllUser()
                assertTrue(allUser.isEmpty())
            }

            @Test
            fun `Get all users if exist users in the database`() {
                val userEntity = UserEntity(user = user)
                instance = Dao(userMap = hashMapOf(0L to userEntity))

                val allUser = instance.getAllUser()
                assertEquals(1, allUser.size)

                val found = allUser.first()
                assertEquals(userEntity, found)
            }

        }

        @Nested
        inner class GetUserById {

            @Test
            fun `Search user by id and exists`() {
                val userEntity = UserEntity(user = user)
                instance = Dao(userMap = hashMapOf(0L to userEntity))

                val found = instance.getUserById(0L)
                assertEquals(userEntity, found)
            }

            @Test
            fun `Search user by id and non exists`() {
                instance = Dao(userMap = hashMapOf())
                assertNull(instance.getUserById(0L))
            }

        }

        @Nested
        inner class SaveUser {

            @Test
            fun `Save user with all fields defined`() {
                instance = Dao()
                instance.saveUser(user)

                val allUsers = instance.getAllUser()
                assertEquals(1, allUsers.size)

                val foundUser = allUsers.first()
                assertEquals(user.id, foundUser.id)
                assertEquals(user.userName, foundUser.userName)
                assertEquals(user.fullName, foundUser.fullName)
                assertEquals(user.password, foundUser.password)
                assertEquals(user.token, foundUser.token)
                assertEquals(user.stats.winCount, foundUser.winCount)
                assertEquals(user.stats.tieCount, foundUser.tieCount)
                assertEquals(user.stats.loseCount, foundUser.loseCount)
                assertEquals(user.stats.inProgressCount, foundUser.inProgressCount)
            }

            @Test
            fun `Save user without defined token`() {
                instance = Dao()
                instance.saveUser(user.copy(token = null))

                val allUsers = instance.getAllUser()
                assertEquals(1, allUsers.size)

                val foundUser = allUsers.first()
                assertEquals(user.id, foundUser.id)
                assertEquals(user.userName, foundUser.userName)
                assertEquals(user.fullName, foundUser.fullName)
                assertEquals(user.password, foundUser.password)
                assertNull(foundUser.token)
                assertEquals(user.stats.winCount, foundUser.winCount)
                assertEquals(user.stats.tieCount, foundUser.tieCount)
                assertEquals(user.stats.loseCount, foundUser.loseCount)
                assertEquals(user.stats.inProgressCount, foundUser.inProgressCount)
            }

        }

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

    @Nested
    inner class DeckEntityTest {

        @Nested
        inner class GetAllDeck {

            @Test
            fun `Get all decks if non exist decks in the database`() {
                instance = Dao(deckMap = hashMapOf())

                val allDeck = instance.getAllDeck()
                assertTrue(allDeck.isEmpty())
            }

            @Test
            fun `Get all decks if exist decks in the database`() {
                val deckEntity = DeckEntity(deck = deck)
                instance = Dao(deckMap = hashMapOf(0L to deckEntity))

                val allDeck = instance.getAllDeck()
                assertEquals(1, allDeck.size)

                val found = allDeck.first()
                assertEquals(deckEntity, found)
            }

        }

        @Nested
        inner class GetAllDeckHistory {

            @Test
            fun `Get all deck history if non exist deck history in the database`() {
                instance = Dao(deckHistoryMap = hashMapOf())

                val allDeckHistory = instance.getAllDeckHistory()
                assertTrue(allDeckHistory.isEmpty())
            }

            @Test
            fun `Get all deck history if exist deck history in the database`() {
                val deckHistoryEntity = DeckHistoryEntity(deckHistory)
                instance = Dao(deckHistoryMap = hashMapOf(0L to deckHistoryEntity))

                val allDeckHistory = instance.getAllDeckHistory()
                assertEquals(1, allDeckHistory.size)

                val found = allDeckHistory.first()
                assertEquals(deckHistoryEntity, found)
            }

        }

        @Nested
        inner class GetDeckById {

            @Test
            fun `Search deck by id and exists`() {
                val deckEntity = DeckEntity(deck = deck)
                instance = Dao(deckMap = hashMapOf(0L to deckEntity))

                val found = instance.getDeckById(0L)
                assertEquals(deckEntity, found)
            }

            @Test
            fun `Search deck by id and non exists`() {
                instance = Dao(deckMap = hashMapOf())
                assertNull(instance.getDeckById(0L))
            }

        }

        @Nested
        inner class GetDeckHistoryById {

            @Test
            fun `Search deck history by id and exists`() {
                val deckHistoryEntity = DeckHistoryEntity(deckHistory)
                instance = Dao(deckHistoryMap = hashMapOf(0L to deckHistoryEntity))

                val found = instance.getDeckHistoryById(0L)
                assertTrue(found.contains(deckHistoryEntity))
            }

            @Test
            fun `Search deck history by id and non exists`() {
                instance = Dao(deckHistoryMap = hashMapOf())
                assertTrue(instance.getDeckHistoryById(0L).isEmpty())
            }

        }

        @Nested
        inner class GetDeckHistoryByVersion {

            @Test
            fun `Search deck history by version and exists`() {
                val deckHistoryEntity = DeckHistoryEntity(deckHistory)
                instance = Dao(deckHistoryMap = hashMapOf(0L to deckHistoryEntity))

                val found = instance.getDeckHistoryByVersion(0L)
                assertEquals(deckHistoryEntity, found)
            }

            @Test
            fun `Search deck history by version and non exists`() {
                instance = Dao(deckHistoryMap = hashMapOf())
                assertNull(instance.getDeckHistoryByVersion(0L))
            }

        }

        @Test
        fun saveDeck() {
            instance = Dao()
            instance.saveDeck(deck)

            val allDecks = instance.getAllDeck()
            assertEquals(1, allDecks.size)

            val foundDeck = allDecks.first()
            assertEquals(deck.id, foundDeck.id)
            assertEquals(deck.version, foundDeck.version)
            assertEquals(deck.name, foundDeck.name)

            val cards = foundDeck.cardIds
            assertTrue(cards.contains(batman.id))
            assertTrue(cards.contains(batmanII.id))
            assertTrue(cards.contains(flash.id))
        }

        @Nested
        inner class SaveDeckHistory {

            @Test
            fun `Save history and it already exists`() {
                instance = Dao(deckHistoryMap = hashMapOf(0L to DeckHistoryEntity(deckHistory)))
                instance.saveDeckHistory(deckHistory)

                val allDeckHistory = instance.getAllDeckHistory()
                assertEquals(1, allDeckHistory.size)

                val foundDeckHistory = allDeckHistory.first()
                assertEquals(deckHistory.id, foundDeckHistory.id)
                assertEquals(deckHistory.name, foundDeckHistory.name)

                val cards = foundDeckHistory.cardIds
                assertTrue(cards.contains(batman.id))
                assertTrue(cards.contains(batmanII.id))
                assertTrue(cards.contains(flash.id))
            }

            @Test
            fun `Save history that did not exist`() {
                instance = Dao(deckHistoryMap = hashMapOf())
                instance.saveDeckHistory(deckHistory)

                val allDeckHistory = instance.getAllDeckHistory()
                assertEquals(1, allDeckHistory.size)

                val foundDeckHistory = allDeckHistory.first()
                assertEquals(deckHistory.id, foundDeckHistory.id)
                assertEquals(deckHistory.name, foundDeckHistory.name)

                val cards = foundDeckHistory.cardIds
                assertTrue(cards.contains(batman.id))
                assertTrue(cards.contains(batmanII.id))
                assertTrue(cards.contains(flash.id))
            }

        }

        @Test
        fun deleteDeck() {
            instance = Dao()
            instance.saveDeck(deck)
            instance.deleteDeck(deck.id!!)

            val allDecks = instance.getAllDeck()
            assertTrue(allDecks.isEmpty())
        }
    }

    @Nested
    inner class PlayerEntityTest {

        @Nested
        inner class GetPlayerById {

            @Test
            fun `Search player by id and exists`() {
                val playerEntity = PlayerEntity(player = player)
                instance = Dao(playerMap = hashMapOf(0L to playerEntity))

                val found = instance.getPlayerById(0L)
                assertEquals(playerEntity, found)
            }

            @Test
            fun `Search player by id and non exists`() {
                instance = Dao(playerMap = hashMapOf())
                assertNull(instance.getPlayerById(0L))
            }

        }

        @Test
        fun savePlayer() {
            instance = Dao()
            instance.savePLayer(player)

            val foundPlayer = instance.getPlayerById(player.id!!)!!
            assertEquals(player.id, foundPlayer.id)
            assertEquals(player.user.id, foundPlayer.userId)
            assertTrue(foundPlayer.availableCardIds.contains(batman.id))
            assertTrue(foundPlayer.prizeCardIds.contains(flash.id))
        }

        @Nested
        inner class GetPlayerHistoryById {

            @Test
            fun `Search player history by id and exists`() {
                val playerHistoryEntity = PlayerHistoryEntity(playerHistory = playerHistory)
                instance = Dao(playerHistoryMap = hashMapOf(0L to playerHistoryEntity))

                val found = instance.getPlayerHistoryByVersion(0L)
                assertEquals(playerHistoryEntity, found)
            }

            @Test
            fun `Search player history by id and non exists`() {
                instance = Dao(playerHistoryMap = hashMapOf())
                assertNull(instance.getPlayerHistoryByVersion(0L))
            }

        }

        @Test
        fun savePlayerHistory() {
            instance = Dao()
            instance.savePlayerHistory(playerHistory)

            val foundPlayerHistory = instance.getPlayerHistoryByVersion(playerHistory.id)!!
            assertEquals(playerHistory.id, foundPlayerHistory.id)
            assertTrue(foundPlayerHistory.availableCardIds.contains(batman.id))
            assertTrue(foundPlayerHistory.prizeCardIds.contains(flash.id))
        }

    }

    @Nested
    inner class MatchEntityTest {

        @Nested
        inner class GetMatchById {

            @Test
            fun `Search match by id and exists`() {
                val matchEntity = MatchEntity(match = match)
                instance = Dao(matchMap = hashMapOf(0L to matchEntity))

                val found = instance.getMatchById(0L)
                assertEquals(matchEntity, found)
            }

            @Test
            fun `Search match by id and non exists`() {
                instance = Dao(matchMap = hashMapOf())
                assertNull(instance.getMatchById(0L))
            }

        }

        @Test
        fun saveMatch() {
            instance = Dao()
            instance.saveMatch(match)

            val foundMatch = instance.getMatchById(match.id!!)!!
            assertEquals(match.id, foundMatch.id)
            assertEquals(match.status.name, foundMatch.status)
            assertEquals(match.deck.id, foundMatch.deckId)

            val players = foundMatch.playerIds
            assertTrue(players.contains(player.id))
        }

        @Nested
        inner class GetDuelHistoryById {

            @Test
            fun `Search duel history by id and exists`() {
                val duelHistoryEntity = DuelHistoryEntity(duelHistory = duelHistory)
                instance = Dao(duelHistoryMap = hashMapOf(0L to duelHistoryEntity))

                val found = instance.getDuelHistoryById(0L)
                assertEquals(duelHistoryEntity, found)
            }

            @Test
            fun `Search duel history by id and non exists`() {
                instance = Dao(duelHistoryMap = hashMapOf())
                assertNull(instance.getDuelHistoryById(0L))
            }

        }

        @Test
        fun saveDuelHistory() {
            instance = Dao()
            instance.saveDuelHistory(duelHistory)

            val foundDuelHistory = instance.getDuelHistoryById(duelHistory.id!!)!!
            assertEquals(duelHistory.id, foundDuelHistory.id)
            assertEquals(0L, foundDuelHistory.playerVersion)
            assertEquals(1L, foundDuelHistory.opponentVersion)
            assertEquals(duelHistory.duelType.name, foundDuelHistory.duelType)
            assertEquals(duelHistory.duelResult.name, foundDuelHistory.duelResult)
        }
    }
}