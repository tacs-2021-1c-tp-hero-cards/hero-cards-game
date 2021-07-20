package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck.DeckEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface DeckRepository : CrudRepository<DeckEntity, Long> {

    fun getById(id: Long): DeckEntity?

    @Query(
        "SELECT d FROM DeckEntity d " +
                "WHERE (:id is null or :id = '' or d.id = :id) and " +
                "(:name is null or :name = '' or upper(d.name) = upper(:name))"
    )
    fun findDeckByIdAndName(@Param("id") id: String? = null, @Param("name") name: String? = null): List<DeckEntity>
}