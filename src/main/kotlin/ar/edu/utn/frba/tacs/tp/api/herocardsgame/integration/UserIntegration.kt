package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidUserException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.HashService
import org.springframework.stereotype.Component

@Component
class UserIntegration(private val dao: Dao) {

    fun createUser(userName: String, fullName: String, password: String): User {
        if (dao.getAllUser().any { it.userName == userName && it.fullName == fullName }) {
            throw InvalidUserException(fullName, userName)
        }

        return saveUser(User(userName = userName, fullName = fullName, password = password))
    }

    fun activateUserSession(userName: String, password: String): User {
        val user = dao.getAllUser().find { it.userName == userName && it.password == password }
            ?: throw ElementNotFoundException("user", userName)

        return saveUser(user.toModel().copy(token = HashService.calculateToken(user.id, userName, user.fullName)))
    }

    fun disableUserSession(token: String) {
        val user = dao.getAllUser().find { it.token == token }
            ?: throw ElementNotFoundException("token", token)

        saveUser(user.toModel().copy(token = null))
    }

    fun getUserById(id: Long): User =
        dao.getUserById(id)?.toModel() ?: throw ElementNotFoundException("id", id.toString())

    fun getAllUser(): List<User> = dao.getAllUser().map { it.toModel() }

    fun saveUser(user: User): User = dao.saveUser(user).toModel()

}
