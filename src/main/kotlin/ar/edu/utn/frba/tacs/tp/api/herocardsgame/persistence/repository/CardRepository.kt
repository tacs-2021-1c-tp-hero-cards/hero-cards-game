package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.CardEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CardRepository : CrudRepository<CardEntity, String>