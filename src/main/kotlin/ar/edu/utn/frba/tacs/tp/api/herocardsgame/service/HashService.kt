package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import java.math.BigInteger
import java.security.MessageDigest
import java.time.Instant.now

class HashService {
    companion object {
        fun calculateToken(user: User): String {
            return calculateHash(user.id.toString() + user.userName + user.fullName + now())
        }

        fun calculatePasswordHash(userName: String, password: String): String {
            return calculateHash(userName + password)
        }

        fun calculateHash(input: String): String {
            val md = MessageDigest.getInstance("MD5")
            return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
        }
    }
}

