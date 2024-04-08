package me.iru.data

import me.iru.Authy
import me.iru.LoginType
import org.bukkit.entity.Player
import org.havry.entities.UpdateUserDTO
import org.havry.entities.User
import java.sql.Timestamp

class Session {
    val authy = Authy.instance
    val playerData = Authy.playerData

    private val authManager = Authy.authManager

    fun remember(p: Player) {
        playerData.updateUser(UpdateUserDTO(
            p.uniqueId,
            session=Timestamp(System.currentTimeMillis()).time,
            ip=p.address?.address?.hostAddress!!
        ))
    }

    fun tryAutoLogin(p: Player, authyPlayer: User?) : Boolean {
        authyPlayer ?: return false

        val now = Timestamp(System.currentTimeMillis()).time
        val hours = authy.config.getInt("sessionExpiresIn")
        val passes = if(hours > 500 || hours < 0) true
        else authyPlayer.session + (hours * 3600000L) > now
        if(passes && p.address?.address?.hostAddress == authyPlayer.ip) {
            authManager.login(authyPlayer, p, LoginType.Session)
            return true
        }

        return false
    }
}