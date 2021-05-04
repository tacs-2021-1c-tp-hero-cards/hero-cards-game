package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import java.math.BigInteger
import java.security.MessageDigest

class HashService {
    companion object {
        fun calculateToken(user: User): String {
            return calculateHash(user.id.toString() + user.userName + user.fullName)
        }

        fun calculatePasswordHash(userName: String, password: String): String {
            return calculateHash(userName + password)
        }

        private fun calculateHash(input: String): String {
            val md = MessageDigest.getInstance("MD5")
            return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
        }
    }
}

