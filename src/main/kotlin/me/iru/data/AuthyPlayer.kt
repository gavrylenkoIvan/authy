package me.iru.data

import com.google.gson.annotations.SerializedName
import java.util.*

data class AuthyPlayer(val uuid: UUID,
                       var username: String,
                       var ip: String,
                       var password: String,
                       @SerializedName("is_pin_enabled")
                       var isPinEnabled: Boolean = false,
                       var pin: String? = null,
                       var session: Long = 0L,
                       var roles: Array<String>? = arrayOf("player"),
    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AuthyPlayer

        if (uuid != other.uuid) return false
        if (username != other.username) return false
        if (ip != other.ip) return false
        if (password != other.password) return false
        if (isPinEnabled != other.isPinEnabled) return false
        if (pin != other.pin) return false
        if (session != other.session) return false
        if (!roles.contentEquals(other.roles)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + ip.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + isPinEnabled.hashCode()
        result = 31 * result + (pin?.hashCode() ?: 0)
        result = 31 * result + session.hashCode()
        result = 31 * result + roles.contentHashCode()
        return result
    }
}

data class UpdatePlayerDTO(val uuid: UUID,
                           var ip: String? = null,
                           var password: String? = null,
                           @SerializedName("is_pin_enabled")
                           var isPinEnabled: Boolean? = null,
                           var pin: String? = null,
                           var session: Long? = null)