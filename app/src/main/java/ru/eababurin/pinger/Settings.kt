package ru.eababurin.pinger

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class Settings : PreferenceFragmentCompat() {

    private lateinit var themePreference: Preference

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferencesEditor: SharedPreferences.Editor

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        preferencesEditor = sharedPreferences.edit()

        themePreference = findPreference<ListPreference>("theme")!!.apply {
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