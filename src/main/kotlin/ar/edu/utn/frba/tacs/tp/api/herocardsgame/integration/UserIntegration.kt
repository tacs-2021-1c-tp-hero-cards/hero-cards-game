package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.HashService
import org.springframework.stereotype.Component

@Component
class UserIntegration(
    private val userMap: HashMap<Long, User> = hashMapOf(),
    private val userSessionMap: HashMap<String, Long> = hashMapOf()

) {

    fun getAllUser(): List<User> = userMap.values.toList()

    fun saveUser(user: User): User {
        val userId = user.id ?: calculateId()
        val newUser = user.copy(id = userId)
        userMap[userId] = newUser
        return newUser
    }

    fun calculateId(): Long = userMap.size.toLong()

    fun getAllUserSession(): HashMap<String, Long> = userSessionMap

    fun addUserSession(user: User): User {
        val token = HashService.calculateToken(user)
        val newUser = user.copy(token = token)
        userSessionMap[token] = newUser.id!!
        return saveUser(newUser)
    }

    fun deleteUserSession(user: User): User {
        userSessionMap.remove(user.token)
        val newUser = user.copy(token = null)
        userMap[user.id!!] = newUser
        return saveUser(newUser)
    }
}
