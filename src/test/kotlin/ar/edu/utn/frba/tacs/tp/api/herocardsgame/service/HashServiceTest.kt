package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class HashServiceTest {

    @Test
    fun calculateToken(){
        val calculateToken = HashService.calculateToken(0L, "userName", "fullName")
        val calculateOtherToken = HashService.calculateToken(0L, "userName", "fullName")

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