package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user.UserEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<UserEntity, Long> {

    fun getById(id: Long): UserEntity?

    @Query("SELECT u FROM UserEntity u WHERE u.userType = 'HUMAN' and u.token = :token")
    fun findHumanByToken(@Param("token") token: String): UserEntity?

    @Query(
        "SELECT u FROM UserEntity u WHERE u.userType = 'HUMAN' and " +
                "upper(u.userName) = upper(:userName) and " +
                "u.password = :password"
    )
    fun findHumanByUserNameAndPassword(
        @Param("userName") userName: String,
        @Param("password") password: String
    ): UserEntity?

    @Query(
        "SELECT u FROM UserEntity u " +
                "WHERE u.userType = 'HUMAN' and " +
                "(:id is null or :id = '' or u.id = :id) and " +
                "(:userName is null or :userName = '' or upper(u.userName) = upper(:userName)) and" +
                "(:fullName is null or :fullName = '' or upper(u.fullName) = upper(:fullName)) and" +
                "(:token is null or :token = ''or u.token = :token)"
    )
    fun findHumanByIdAndUserNameAndFullNameAndToken(
        @Param("id") id: String? = null,
        @Param("userName") userName: String? = null,
        @Param("fullName") fullName: String? = null,
        @Param("token") token: String? = null
    ): List<UserEntity>

    @Query(
        "SELECT u FROM UserEntity u " +
                "WHERE u.userType = 'IA' and " +
                "(:id is null or :id = '' or u.id = :id) and " +
                "(:userName is null or :userName = ''or upper(u.userName) = upper(:userName)) and" +
                "(:difficulty is null or :difficulty = ''or upper(u.difficulty) = upper(:difficulty))"
    )
    fun findIAByIdAndUserNameAndDifficulty(
        @Param("id") id: String? = null,
        @Param("userName") userName: String? = null,
        @Param("difficulty") difficulty: String? = null
    ): List<UserEntity>

}