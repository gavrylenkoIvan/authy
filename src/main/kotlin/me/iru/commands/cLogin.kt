package me.iru.commands

import me.iru.Authy
import me.iru.PrefixType
import me.iru.interfaces.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import me.iru.validation.PasswordValidation
import me.iru.validation.PinValidation
import org.bukkit.Bukkit

class cLogin(override var name: String = "login") : ICommand {
    val authy = Authy.instance
    val translations = Authy.translations
    val playerData = Authy.playerData
    val loginProcess = Authy.loginProcess
    val authManager = Authy.authManager

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        Bukkit.getScheduler().runTaskAsynchronously(authy, Runnable {
            if(sender is Player) {
                val p : Player = sender
                val authyPlayer = playerData.get(p.uniqueId)
                println(authyPlayer.toString())
                if(authyPlayer == null) {
                    p.sendMessage("${translations.getPrefix(PrefixType.ERROR)} ${translations.get("command_login_notregistered")}")
                    return@Runnable
                }
                if(!loginProcess.contains(p)) {
                    p.sendMessage("${translations.getPrefix(PrefixType.ERROR)} ${translations.get("already_authed")}")
                    return@Runnable
                }
                if(authyPlayer.isPinEnabled) {
                    if(args.size != 2) {
                        p.sendMessage("${translations.getPrefix(PrefixType.ERROR)} ${translations.get("command_login_usagepin")}")
                        return@Runnable
                    }
                } else {
                    if(args.size != 1) {
                        p.sendMessage("${translations.getPrefix(PrefixType.ERROR)} ${translations.get("command_login_usage")}")
                        return@Runnable
                    }
                }

                if(!PasswordValidation.check(args[0], authyPlayer.password)) {
                    p.sendMessage("${translations.getPrefix(PrefixType.ERROR)} ${translations.get("command_login_wrongpassword")}")
                    return@Runnable
                } else {
                    if(authyPlayer.isPinEnabled) {
                        if(!PinValidation.check(args[1], authyPlayer.pin!!)) {
                            p.sendMessage("${translations.getPrefix(PrefixType.ERROR)} ${translations.get("command_login_wrongpin")}")
                            return@Runnable
                        }
                    }
                    authManager.login(authyPlayer, p)
                }

            }
        })

        return true
    }
}