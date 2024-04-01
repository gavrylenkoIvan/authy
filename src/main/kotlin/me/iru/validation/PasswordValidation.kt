package me.iru.validation

import me.iru.utils.HashUtil

object PasswordValidation {
    fun check(raw: String, hash: String): Boolean {
        return HashUtil.toSHA256(raw) == hash
    }

    fun matchesRules(pass: String): Boolean {
        val rule = getPasswordRule()
        if(pass.length < rule.minLength) return false
        if(pass.length > rule.maxLength) return false
        if(!pass.contains(Regex("[A-Z]{${rule.minUppercase}}"))) return false
        if(!pass.contains(Regex("[0-9]{${rule.minNumbers}}"))) return false
        return true
    }
}