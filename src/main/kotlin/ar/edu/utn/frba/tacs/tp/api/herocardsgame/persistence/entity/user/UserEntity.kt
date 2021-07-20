package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import javax.persistence.*

@Entity
@Table(name = "USER")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val userName: String,
    @Enumerated(value = EnumType.STRING)
    val userType: UserType,
    val winCount: Int,
    val tieCount: Int,
    val loseCount: Int,
    val inProgressCount: Int,

    val fullName: String? = null,
    val password: String? = null,
    val token: String? = null,
    val isAdmin: Boolean? = null,

    @Enumerated(value = EnumType.STRING)
    val difficulty: IADifficulty? = null
){
    fun toModel() = UserFactory().toModel(this)

    fun toHumanModel() = UserFactory().toModel(this) as Human
}