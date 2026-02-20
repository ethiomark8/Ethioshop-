package com.ethio.shop.util

object PhoneUtils {
    
    fun formatEthiopianPhone(phone: String): String {
        var formatted = phone.replace(Regex("[^0-9+]"), "")
        
        // Add country code if not present
        if (!formatted.startsWith("+251")) {
            if (formatted.startsWith("251")) {
                formatted = "+$formatted"
            } else if (formatted.startsWith("0")) {
                formatted = "+251${formatted.substring(1)}"
            } else if (formatted.length == 9) {
                formatted = "+251$formatted"
            }
        }
        
        return formatted
    }
    
    fun isValidEthiopianPhone(phone: String): Boolean {
        val formatted = formatEthiopianPhone(phone)
        return formatted.matches(Regex("^\\+251[9][0-9]{8}$"))
    }
    
    fun stripCountryCode(phone: String): String {
        var formatted = phone.replace(Regex("[^0-9+]"), "")
        
        if (formatted.startsWith("+251")) {
            formatted = "0${formatted.substring(4)}"
        } else if (formatted.startsWith("251")) {
            formatted = "0${formatted.substring(3)}"
        }
        
        return formatted
    }
    
    fun maskPhoneNumber(phone: String): String {
        val formatted = stripCountryCode(phone)
        if (formatted.length == 10) {
            return "${formatted.substring(0, 4)}***${formatted.substring(7)}"
        }
        return phone
    }
}