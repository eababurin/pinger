package ru.eababurin.pinger

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    companion object {
        const val KEY_THEME = "Theme"
    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(KEY_THEME, Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()

        if (Configuration.UI_MODE_NIGHT_YES == sharedPreferences.getInt(
                KEY_THEME, Configuration.UI_MODE_NIGHT_NO
            )
        ) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.fragmentLayout, MainFragment())
            .commit()
    }

    fun changeTheme() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPreferencesEditor.putInt(KEY_THEME, Configuration.UI_MODE_NIGHT_YES)
            }

            Configuration.UI_MODE_NIGHT_YES -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPreferencesEditor.putInt(KEY_THEME, Configuration.UI_MODE_NIGHT_NO)
            }
        }
        sharedPreferencesEditor.commit()
    }
}