package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts

class User(
    var id: Long? = null,
    val userName: String,
    val fullName: String,
    val password: String,
    var token: String? = null
) {

    fun updateId(newId: Long) {
        id = newId
    }

    fun updateToken(newToken: String){
        token = newToken
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (userName != other.userName) return false
        if (fullName != other.fullName) return false
        if (password != other.password) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userName.hashCode()
        result = 31 * result + fullName.hashCode()
        result = 31 * result + password.hashCode()
        return result
    }


}