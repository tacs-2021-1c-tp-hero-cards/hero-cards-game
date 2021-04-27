package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts

data class Authentication(val token: String)

data class User(val username: String, val fullName: String, val password: String, val token: String)