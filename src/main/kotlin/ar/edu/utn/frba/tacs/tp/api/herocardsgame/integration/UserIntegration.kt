package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
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
        val token = calculateToken(user)
        user.updateToken(token)
        userSessionMap[token] = user.id!!
        return token
    }

    fun deleteUserSession(token: String){
        userSessionMap.remove(token)
    }

    fun calculateToken(user: User): String {
        val input = user.id.toString() + user.userName + user.fullName
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

}
