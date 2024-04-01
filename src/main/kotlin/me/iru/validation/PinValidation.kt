package me.iru.validation

import me.iru.utils.HashUtil

object PinValidation {
    fun check(raw: String, hashPin: String): Boolean {
        return HashUtil.toSHA256(raw) == hashPin
    }

    fun matchesRules(pin: String): Boolean {
        val rule = getPinRule()
        if(pin.length < rule.minLength) return false
        if(pin.length > rule.maxLength) return false
        if(!pin.matches(Regex("^[0-9]*\$"))) return false
        return true
    }
}