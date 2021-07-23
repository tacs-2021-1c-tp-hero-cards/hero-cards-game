package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match.MatchEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MatchRepository : CrudRepository<MatchEntity, Long> {
    fun getById(id: Long): MatchEntity?

    @Query("SELECT m FROM MatchEntity m WHERE m.playerIdCreatedMatch = :userId")
    fun findMatchByCreatedUserId(@Param("userId") id: Long): List<MatchEntity>

    @Query("SELECT m FROM MatchEntity m JOIN m.player p WHERE :userId = p.id")
    fun findMatchByUserId(@Param("userId") id: Long): List<MatchEntity>

}