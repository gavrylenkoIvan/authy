package me.iru.commands

import me.iru.Authy
import me.iru.PrefixType
import me.iru.interfaces.ICommand
import me.iru.process.LoginProcess
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import me.iru.validation.PasswordValidation
import me.iru.validation.PinValidation
import me.iru.validation.getPasswordRule
import me.iru.validation.getPinRule
import org.bukkit.Bukkit

class cRegister(override var name: String = "register") : ICommand {
    val authy = Authy.instance
    val translations = Authy.translations
    val playerData = Authy.playerData
    private val loginProcess : LoginProcess = Authy.loginProcess
    private val authManager = Authy.authManager

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        Bukkit.getScheduler().runTaskAsynchronously(authy, Runnable {
            if (sender is Player) {
                val p: Player = sender
                if (!loginProcess.contains(p)) {
                    p.sendMessage("${translations.getPrefix(PrefixType.ERROR)} ${translations.get("already_authed")}!")
                }
                val requirePin = authy.config.getBoolean("requirePin")
                if (args.size != (if (requirePin) 3 else 2)) {
                    p.sendMessage("${translations.getPrefix(PrefixType.ERROR)} ${translations.get("command_register_usage${if (requirePin) "pin" else ""}")}")
                }
                if(args[0] != args[1]) {
                    p.sendMessage("${translations.getPrefix(PrefixType.ERROR)} ${translations.get("command_register_notidentical")}")
                }
                val password = args[0]
                if(!PasswordValidation.matchesRules(password)) {
                    val rule = getPasswordRule()
                    p.sendMessage("${translations.getPrefix(PrefixType.ERROR)} ${translations.get("command_register_breaksrules").format(rule.minLength, rule.maxLength, rule.minUppercase, rule.minNumbers)}")
                }
                val pin = args.getOrNull(2)
                if(requirePin && !PinValidation.matchesRules(pin!!)) {
                    val rule = getPinRule()
                    p.sendMessage("${translations.getPrefix(PrefixType.ERROR)} ${translations.get("command_pin_breaksrules").format(rule.minLength, rule.maxLength)}")
                }
                if(!playerData.exists(p.uniqueId)) {
                    authManager.register(p, password, pin)
                } else {
                    p.sendMessage("${translations.getPrefix(PrefixType.ERROR)} ${translations.get("command_register_alreadyregistered")}")
                }
            }
        })

        return true
    }
}