package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class HashServiceTest {

    @Test
    fun calculateToken(){
        val user = User(id = 0L, userName = "userName", password = "password", fullName = "fullName")

        val calculateToken = HashService.calculateToken(user)
        val calculateOtherToken = HashService.calculateToken(user)

        assertTrue(calculateToken != calculateOtherToken)
    }

    @Test
    fun calculatePasswordHash(){
        val calculateToken = HashService.calculatePasswordHash("userName", "password")
        val calculateOtherToken = HashService.calculatePasswordHash("userName", "password")

        assertTrue(calculateToken == calculateOtherToken)
    }

    @Test
    fun calculatePasswordHash_differentUserName(){
        val calculateToken = HashService.calculatePasswordHash("userName", "password")
        val calculateOtherToken = HashService.calculatePasswordHash("userName2", "password")

        assertTrue(calculateToken != calculateOtherToken)
    }

    @Test
    fun calculatePasswordHash_differentPassWord(){
        val calculateToken = HashService.calculatePasswordHash("userName", "password")
        val calculateOtherToken = HashService.calculatePasswordHash("userName", "password2")

        assertTrue(calculateToken != calculateOtherToken)
    }

    @Test
    fun calculateHash(){
        val calculateHash = HashService.calculateHash("token")
        assertEquals(32, calculateHash.length)
    }
}