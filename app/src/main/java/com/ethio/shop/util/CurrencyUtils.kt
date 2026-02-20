package com.ethio.shop.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    
    private val ethiopianFormat = DecimalFormat("#,##0.00")
    
    fun formatETB(amount: Double): String {
        return "$currencySymbol ${ethiopianFormat.format(amount)}"
    }
    
    fun formatETB(amount: Int): String {
        return "$currencySymbol ${ethiopianFormat.format(amount.toDouble())}"
    }
    
    fun formatETB(amount: String): String {
        return try {
            formatETB(amount.toDouble())
        } catch (e: NumberFormatException) {
            "$currencySymbol 0.00"
        }
    }
    
    fun parseETB(formatted: String): Double {
        return try {
            val cleaned = formatted.replace(Regex("[^0-9.]"), "")
            cleaned.toDoubleOrNull() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }
    
    val currencySymbol: String
        get() = "ETB"
    
    fun isValidAmount(amount: Double): Boolean {
        return amount > 0 && amount < 10000000 // Reasonable limits
    }
}