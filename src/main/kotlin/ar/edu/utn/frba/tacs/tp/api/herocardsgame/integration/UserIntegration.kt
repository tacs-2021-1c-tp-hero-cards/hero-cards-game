package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidDifficultyException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidHumanUserException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidIAUserException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user.UserFactory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository.UserRepository
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.HashService
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import org.springframework.stereotype.Component

@Component
class UserIntegration(private val factory: UserFactory, private val repository: UserRepository) {

    fun createUser(userName: String, fullName: String, isAdmin: Boolean, password: String): User {
        if (repository.findHumanByIdAndUserNameAndFullNameAndToken(userName = userName, fullName = fullName)
                .isNotEmpty()
        ) {
            throw InvalidHumanUserException(userName, fullName)
        }

        return saveUser(Human(userName = userName, fullName = fullName, password = password, isAdmin = isAdmin))
    }

    fun createUser(userName: String, difficulty: String): User {
        if (repository.findIAByIdAndUserNameAndDifficulty(userName = userName, difficulty = difficulty).isNotEmpty()) {
            throw InvalidIAUserException(userName, difficulty)
        }

        if (!IADifficulty.existDifficulty(difficulty)) {
            throw InvalidDifficultyException(difficulty)
        }

        return saveUser(IA(userName = userName, difficulty = IADifficulty.valueOf(difficulty)))
    }

    fun activateUserSession(userName: String, password: String): Human {
        val user = repository.findHumanByUserNameAndPassword(userName, password)?.toHumanModel()
            ?: throw ElementNotFoundException("user", "userName", userName)
        return saveUser(user.copy(token = HashService.calculateToken(user.id!!, userName, user.fullName))) as Human
    }

    fun disableUserSession(token: String) {
        val user =
            repository.findHumanByToken(token)?.toHumanModel() ?: throw ElementNotFoundException("user", "token", token)
        saveUser(user.copy(token = null))
    }

    fun getUserById(id: Long): User =
        repository.getById(id)?.toModel() ?: throw ElementNotFoundException("user", "id", id.toString())

    fun getAllUser(): List<User> = repository.findAll().map { it.toModel() }

    fun saveUser(user: User) =
        repository.save(factory.toEntity(user)).toModel()

    fun searchHumanUserByIdUserNameFullNameOrToken(
        id: String? = null,
        userName: String? = null,
        fullName: String? = null,
        token: String? = null,
    ): List<Human> =
        repository.findHumanByIdAndUserNameAndFullNameAndToken(id, userName, fullName, token)
            .map { it.toHumanModel() }

    fun searchIAUserByIdUserNameFullNameOrToken(
        id: String? = null,
        userName: String? = null,
        difficulty: String? = null
    ): List<User> = repository.findIAByIdAndUserNameAndDifficulty(id, userName, difficulty).map { it.toModel() }
}
