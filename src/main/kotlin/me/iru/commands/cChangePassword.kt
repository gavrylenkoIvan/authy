package me.iru.commands

import me.iru.Authy
import me.iru.PrefixType
import me.iru.interfaces.ICommand
import me.iru.validation.PasswordValidation
import me.iru.validation.getPasswordRule
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.havry.entities.UpdateUserDTO

class cChangePassword(override var name: String = "changepassword") : ICommand {

    val translations = Authy.translations
    val playerData = Authy.playerData

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender is Player) {
            val p: Player = sender
            if(args.size != 3) {
                p.sendMessage("${translations.getPrefix(PrefixType.ERROR)} ${translations.get("command_changepassword_usage")}")
                return true
            }

            val user = playerData.getUser(p.uniqueId).get()
            if(!PasswordValidation.check(args[0], user.password)) {
                p.sendMessage("${translations.getPrefix(PrefixType.ERROR)} ${translations.get("command_login_wrongpassword")}")
                return true
            }

            if(args[1] != args[2]) {
                p.sendMessage("${translations.getPrefix(PrefixType.ERROR)} ${translations.get("command_register_notidentical")}")
                return true
            }

            if(args[0] == args[1]) {
                p.sendMessage("${translations.getPrefix(PrefixType.ERROR)} ${translations.get("command_changepassword_samepassword")}")
                return true
            }

            val password = args[1]
            if(!PasswordValidation.matchesRules(password)) {
                val rule = getPasswordRule()
                p.sendMessage("${translations.getPrefix(PrefixType.ERROR)} ${translations.get("command_register_breaksrules").format(rule.minLength, rule.maxLength, rule.minUppercase, rule.minNumbers)}")
                return true
            }

            playerData.updateUser(UpdateUserDTO(p.uniqueId, password=password))

            p.sendMessage("${translations.getPrefix(PrefixType.REGISTER)} ${translations.get("command_changepassword_success")}")
        }
        return true
    }
}