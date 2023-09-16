package ru.eababurin.pinger.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import ru.eababurin.pinger.R
import ru.eababurin.pinger.ui.settings.favourites.FavouritesFragment
import ru.eababurin.pinger.utils.KEY_THEME
import ru.eababurin.pinger.utils.KEY_THEME_SUMMARY
import ru.eababurin.pinger.utils.THEME_DARK
import ru.eababurin.pinger.utils.THEME_LIGHT
import ru.eababurin.pinger.utils.THEME_SYSTEM

class Settings : PreferenceFragmentCompat() {

    private lateinit var themePreference: Preference
    private lateinit var favouritesPreference: Preference
    private lateinit var autoAddHost: Preference

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferencesEditor: SharedPreferences.Editor

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        preferencesEditor = sharedPreferences.edit()

        autoAddHost = findPreference<CheckBoxPreference>("settings_auto_add_host")!!.apply {

        }

        favouritesPreference = findPreference<Preference>("settings_favourites")!!.apply {
            setOnPreferenceClickListener {
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragmentLayout, FavouritesFragment())
                    .addToBackStack(null)
                    .commit()
                true
            }
        }

        themePreference = findPreference<ListPreference>("settings_theme")!!.apply {
            summary = sharedPreferences?.getString(
                KEY_THEME_SUMMARY,
                resources.getString(R.string.theme_select_system)
            )

            setOnPreferenceChangeListener { _, newValue ->
                applyTheme(newValue.toString()); true
            }
        }

    }

    private fun applyTheme(theme: String) {
        when (theme) {
            THEME_LIGHT -> {
                preferencesEditor.apply {
                    putString(KEY_THEME, THEME_LIGHT)
                    putString(KEY_THEME_SUMMARY, resources.getString(R.string.theme_select_light))
                }.commit()
                themePreference.summary = resources.getString(R.string.theme_select_light)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            THEME_DARK -> {
                preferencesEditor.apply {
                    putString(KEY_THEME, THEME_DARK)
                    putString(KEY_THEME_SUMMARY, resources.getString(R.string.theme_select_dark))
                }.commit()
                themePreference.summary = resources.getString(R.string.theme_select_dark)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            THEME_SYSTEM -> {
                preferencesEditor.apply {
                    putString(KEY_THEME, THEME_SYSTEM)
                    putString(KEY_THEME_SUMMARY, resources.getString(R.string.theme_select_system))
                }.commit()
                themePreference.summary = resources.getString(R.string.theme_select_system)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }
}