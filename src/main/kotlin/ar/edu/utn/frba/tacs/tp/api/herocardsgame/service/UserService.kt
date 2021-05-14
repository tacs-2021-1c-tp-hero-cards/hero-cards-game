package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidUserException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userIntegration: UserIntegration
) {

    fun createUser(userName: String, fullName: String, password: String): User {
        val isExistUser = searchUser(userName = userName, fullName = fullName).isNotEmpty()

        if (isExistUser) {
            throw InvalidUserException(fullName, userName)
        }

        return userIntegration.saveUser(User(userName = userName, fullName = fullName, password = password))
    }

    fun activateUserSession(userName: String, password: String): User {
        val users = searchUser(userName = userName, password = password)

        if (users.isEmpty()) {
            throw ElementNotFoundException("user", userName)
        }

        return userIntegration.addUserSession(users.first())
    }

    fun deactivateUserSession(userToken: String) {
        val users = searchUser(token = userToken)

        if (users.isEmpty()) {
            throw ElementNotFoundException("token", userToken)
        }

        userIntegration.deleteUserSession(users.first())
    }

    //Search

    fun searchUser(
        id: Long? = null,
        userName: String? = null,
        fullName: String? = null,
        password: String? = null,
        token: String? = null
    ): List<User> {
        var usersResult = userIntegration.getAllUser()

        id?.let { usersResult = usersResult.filter { id == it.id } }
        userName?.let { usersResult = usersResult.filter { userName == it.userName } }
        fullName?.let { usersResult = usersResult.filter { fullName == it.fullName } }
        password?.let { usersResult = usersResult.filter { password == it.password } }
        token?.let { usersResult = usersResult.filter { token == it.token } }

        return usersResult
    }

    fun searchUserById(userId: String): User{
        val user = searchUser(id = userId.toLong())
        user.ifEmpty { throw ElementNotFoundException("user", userId) }
        return user.first()
    }
}


