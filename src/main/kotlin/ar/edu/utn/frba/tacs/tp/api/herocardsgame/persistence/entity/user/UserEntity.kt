package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "USER")
data class UserEntity(
    @Id
    @GeneratedValue
    val id: Long? = null,

    val userName: String,
    val userType: String,
    val winCount: Int,
    val tieCount: Int,
    val loseCount: Int,
    val inProgressCount: Int,

    val fullName: String? = null,
    val password: String? = null,
    val token: String? = null,
    val isAdmin: Boolean? = null,

    val difficulty: String? = null
){
    fun toModel() = UserFactory().toModel(this)

    fun toHumanModel() = UserFactory().toModel(this) as Human
}