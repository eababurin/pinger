package ru.eababurin.pinger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import ru.eababurin.pinger.ui.main.MainFragment
import ru.eababurin.pinger.utils.KEY_THEME
import ru.eababurin.pinger.utils.THEME_DARK
import ru.eababurin.pinger.utils.THEME_LIGHT
import ru.eababurin.pinger.utils.THEME_SYSTEM

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTheme(PreferenceManager.getDefaultSharedPreferences(this).getString(KEY_THEME, THEME_SYSTEM)!!)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentLayout, MainFragment())
            .commit()
    }

    private fun setTheme(theme: String) {
        when (theme) {
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            THEME_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}