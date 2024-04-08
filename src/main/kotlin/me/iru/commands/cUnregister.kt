package me.iru.commands

import me.iru.Authy
import me.iru.PrefixType
import me.iru.interfaces.ICommand
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import java.lang.Exception

class cUnregister(override var name: String = "unregister") : ICommand {
    val authy = Authy.instance
    val translations = Authy.translations
    val playerData = Authy.playerData

    private val loginProcess = Authy.loginProcess

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender is Player) {
            val p : Player = sender
            playerData.deleteUser(p.uniqueId)
            p.sendMessage("${translations.getPrefix(PrefixType.UNREGISTER)} ${translations.get("unregister_success")}")
            loginProcess.effectRunner.runUnregister(p)
            authy.server.scheduler.runTaskLater(authy, Runnable {
                p.kickPlayer("${translations.getPrefix(PrefixType.UNREGISTER)} ${translations.get("command_unregister_successkick")}")
            }, 40L)
            return true

        }
        else if(sender is ConsoleCommandSender) {
            if(args.size != 1) {
                sender.sendMessage("${ChatColor.DARK_GRAY}[${ChatColor.GOLD}${authy.description.name}${ChatColor.DARK_GRAY}] ${ChatColor.RED}Usage: unregister [player name]")
                return true
            }
            try {
                val username = args[0]

                @Suppress("DEPRECATION")
                val p = Bukkit.getOfflinePlayer(username)
                playerData.deleteUser(p.uniqueId)
                sender.sendMessage("${ChatColor.DARK_GRAY}[${ChatColor.GOLD}${authy.description.name}${ChatColor.DARK_GRAY}] ${ChatColor.GREEN}Unregistered!")
            } catch (e : Exception) {
                if(e.message != null) sender.sendMessage(e.message!!)
                sender.sendMessage("${ChatColor.DARK_GRAY}[${ChatColor.GOLD}${authy.description.name}${ChatColor.DARK_GRAY}] ${ChatColor.DARK_RED}There has been an error!")
            }
            return true
        }
        return true
    }
}