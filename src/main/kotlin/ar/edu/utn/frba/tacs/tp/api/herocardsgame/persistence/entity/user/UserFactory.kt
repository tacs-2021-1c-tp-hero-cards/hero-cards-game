package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Stats
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import org.springframework.stereotype.Component

@Component
class UserFactory {

    fun toEntity(user: User): UserEntity {
        val userEntity = UserEntity(
            id = user.id,
            userName = user.userName,
            userType = user.userType.name,
            winCount = user.stats.winCount,
            tieCount = user.stats.tieCount,
            loseCount = user.stats.loseCount,
            inProgressCount = user.stats.inProgressCount
        )

        return if (user.userType == UserType.HUMAN) {
            toHumanEntity(userEntity, user as Human)
        } else {
            toIAEntity(userEntity, user as IA)
        }
    }

    private fun toHumanEntity(userEntity: UserEntity, human: Human): UserEntity =
        userEntity.copy(
            fullName = human.fullName,
            password = human.password,
            token = human.token,
            isAdmin = human.isAdmin
        )

    private fun toIAEntity(userEntity: UserEntity, ia: IA): UserEntity =
        userEntity.copy(difficulty = ia.difficulty.name)

    fun toModel(userEntity: UserEntity): User =
        if (userEntity.userType == UserType.HUMAN.name) {
            toHumanModel(userEntity)
        } else {
            toIAModel(userEntity)
        }

    private fun toIAModel(userEntity: UserEntity): IA =
        IA(userEntity.id, userEntity.userName, toStatsModel(userEntity), IADifficulty.valueOf(userEntity.difficulty!!))

    private fun toHumanModel(userEntity: UserEntity): Human =
        Human(
            userEntity.id,
            userEntity.userName,
            userEntity.fullName!!,
            userEntity.password!!,
            userEntity.token,
            toStatsModel(userEntity),
            userEntity.isAdmin!!
        )

    private fun toStatsModel(userEntity: UserEntity): Stats =
        Stats(userEntity.winCount, userEntity.tieCount, userEntity.loseCount, userEntity.inProgressCount)
}