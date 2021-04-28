package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class Match(val id: Long, val players: List<Player>, val deck: Deck)

data class Deck(val id: Long, val name: String, val cards: List<Card>)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Powerstats(val durability: Int, val intelligence: Int, val speed: Int,
                      val power: Int, val combat: Int, val strength: Int)

data class Card(val id: Long, val name: String, val powerstats: Powerstats)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Image(val url: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Character(val id: String, val name: String, val powerstats: Powerstats, val image: Image)

data class Player(val username: String, val availableCards: List<Card>, val prizeCards: List<Card> = emptyList())