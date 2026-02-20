package com.ethio.shop.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleManager {
    
    private const val PREF_NAME = "EthioShopPrefs"
    private const val KEY_LANGUAGE = "language"
    
    val supportedLanguages = listOf("en", "am")
    
    fun setLocale(context: Context): Context {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val language = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
        return updateResources(context, language)
    }
    
    fun setNewLocale(context: Context, language: String): Context {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
        return updateResources(context, language)
    }
    
    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        
        val configuration = Configuration(context.resources.configuration)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            configuration.locale = locale
        }
        
        return context.createConfigurationContext(configuration)
    }
    
    fun getCurrentLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    }
    
    fun isRTL(context: Context): Boolean {
        val language = getCurrentLanguage(context)
        return language == "am"
    }
}