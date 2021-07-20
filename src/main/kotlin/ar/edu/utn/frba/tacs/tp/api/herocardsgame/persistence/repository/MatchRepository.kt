package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match.MatchEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MatchRepository : CrudRepository<MatchEntity, Long> {
    fun getById(id: Long): MatchEntity?
}