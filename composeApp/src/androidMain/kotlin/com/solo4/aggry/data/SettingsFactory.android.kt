package com.solo4.aggry.data

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

private lateinit var appContext: Context

fun initSettingsFactory(context: Context) {
    appContext = context
}

actual fun createSettings(): Settings {
    return SharedPreferencesSettings(
        appContext.getSharedPreferences("aggry_prefs", Context.MODE_PRIVATE)
    )
}
