package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidDifficultyException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidHumanUserException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidIAUserException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.HashService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import org.springframework.stereotype.Component

@Component
class UserIntegration(private val dao: Dao) {

    fun createUser(userName: String, fullName: String, password: String): Human {
        if (dao.getAllHuman().any { it.userName == userName && it.fullName == fullName }) {
            throw InvalidHumanUserException(userName, fullName)
        }

        return saveUser(Human(userName = userName, fullName = fullName, password = password))
    }

    fun createUser(userName: String, difficulty: String): IA {
        if (dao.getAllIA().any { it.userName == userName }) {
            throw InvalidIAUserException(userName, difficulty)
        }

        if (!IADifficulty.existDifficulty(difficulty)) {
            throw InvalidDifficultyException(difficulty)
        }

        return saveUser(IA(userName = userName, difficulty = IADifficulty.valueOf(difficulty)))
    }

    fun activateUserSession(userName: String, password: String): Human {
        val user = dao.getAllHuman().find { it.userName == userName && it.password == password }
            ?: throw ElementNotFoundException("user", "userName", userName)

        return saveUser(user.toModel().copy(token = HashService.calculateToken(user.id, userName, user.fullName)))
    }

    fun disableUserSession(token: String) {
        val user = dao.getAllHuman().find { it.token == token }
            ?: throw ElementNotFoundException("user", "token", token)

        saveUser(user.toModel().copy(token = null))
    }

    fun getHumanUserById(id: Long): Human =
        dao.getHumanById(id)?.toModel() ?: throw ElementNotFoundException("human user", "id", id.toString())

    fun getIAUserById(id: Long): IA =
        dao.getIAById(id)?.toModel() ?: throw ElementNotFoundException("ia user", "id", id.toString())

    fun getAllUser(): List<User> =
        dao.getAllHuman().map { it.toModel() }.plus(dao.getAllIA().map { it.toModel() })

    fun saveUser(human: Human): Human = dao.saveHuman(human).toModel()

    fun saveUser(ia: IA): IA = dao.saveIA(ia).toModel()

    fun searchHumanUserByIdUserNameFullNameOrToken(
        id: String? = null,
        userName: String? = null,
        fullName: String? = null,
        token: String? = null,
    ): List<Human> =
        dao.getAllHuman()
            .asSequence()
            .filter { userName.isNullOrBlank() || it.userName.equals(userName, true) }
            .filter { fullName.isNullOrBlank() || it.fullName.equals(fullName, true) }
            .filter { id.isNullOrBlank() || it.id.toString() == id }
            .filter { token.isNullOrBlank() || token == it.token }
            .map { it.toModel() }
            .toList()

    fun searchIAUserByIdUserNameFullNameOrToken(
        id: String? = null,
        userName: String? = null,
        difficulty: String? = null
    ): List<IA> =
        dao.getAllIA()
            .asSequence()
            .filter { userName.isNullOrBlank() || it.userName.equals(userName, true) }
            .filter { id.isNullOrBlank() || it.id.toString() == id }
            .filter { difficulty.isNullOrBlank() || it.duelDifficulty.equals(difficulty, true) }
            .map { it.toModel() }
            .toList()
}
