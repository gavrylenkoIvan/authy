package me.iru.data.migrations

import me.iru.Authy
import net.md_5.bungee.api.ChatColor
import java.io.File

object Migration {
    val authy = Authy.instance
    val playerData = Authy.playerData

    fun logMigration(msg: String) {
        authy.server.consoleSender.sendMessage("${authy.prefix} ${ChatColor.DARK_GREEN}$msg")
    }

    fun updateSystem() {
        val userdata = File(authy.dataFolder, "userdata" + File.separator)
        if(userdata.exists()) {
            logMigration("Migrating userdata folder to player-data...")
            userdata.renameTo(File(authy.dataFolder, "player-data"))
        }

        val playerData = File(authy.dataFolder, "player-data")
        if(playerData.list()?.isEmpty() == true) {
            logMigration("Deleting empty player-data folder...")
            playerData.delete()
        }
    }
}