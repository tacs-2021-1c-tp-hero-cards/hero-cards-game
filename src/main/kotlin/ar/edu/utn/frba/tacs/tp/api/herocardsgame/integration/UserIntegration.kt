package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.HashService
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.security.MessageDigest

@Component
class UserIntegration(
    private val userMap: HashMap<Long, User> = hashMapOf(),
    private val userSessionMap: HashMap<String, Long> = hashMapOf()
) {

    fun getAllUser(): List<User> = userMap.values.toList()

    fun saveUser(user: User): User {
        val id = calculateId()
        user.updateId(id)
        userMap[id] = user
        return user
    }

    fun calculateId(): Long = userMap.size.toLong()

    fun getAllUserSession(): HashMap<String, Long> = userSessionMap

    fun addUserSession(user: User): String {
        val token = HashService.calculateToken(user)
        user.updateToken(token)
        userSessionMap[token] = user.id!!
        return token
    }

    fun deleteUserSession(user: User){
        userSessionMap.remove(user.token)
        user.deleteToken()
        userMap[user.id!!] = user
    }
}
