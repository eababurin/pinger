package ru.eababurin.pinger

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.eababurin.pinger.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    companion object {
        const val KEY_THEME = "Theme"
    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    private var _ui: FragmentMainBinding? = null
    private val ui get() = _ui!!

    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _ui = FragmentMainBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).setSupportActionBar(ui.topAppBar)
        setHasOptionsMenu(true)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        return ui.root
    }

    override fun onDestroy() {
        _ui = null
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.options_menu_change_theme -> changeTheme()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeTheme() {
        when (requireActivity().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.run {
            mutableOutput.observe(viewLifecycleOwner) {
                val list = it.joinToString("\n")
                ui.outputTextInputEditText.text = Editable.Factory.getInstance().newEditable(list)
            }
            pingError.observe(viewLifecycleOwner) {
                Snackbar.make(ui.layout, it, Snackbar.LENGTH_SHORT).show()
            }
            requestHostname.observe(viewLifecycleOwner) {
                ui.hostnameTextInputEditText.setText(it)
            }
            requestCount.observe(viewLifecycleOwner) {
                ui.countRequestsAutoCompleteTextView.setText(it)
            }
            requestInterval.observe(viewLifecycleOwner) {
                ui.intervalRequestsAutoCompleteTextView.setText(it)
            }
        }

        sharedPreferences = requireActivity().getSharedPreferences(KEY_THEME, Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()

        if (Configuration.UI_MODE_NIGHT_YES == sharedPreferences.getInt(
                KEY_THEME,
                Configuration.UI_MODE_NIGHT_NO
            )
        ) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        ui.outputTextInputEditText.keyListener = null

        ui.pingButton.setOnClickListener {
            if (ui.hostnameTextInputEditText.text!!.isEmpty()) {
                Snackbar.make(ui.layout, resources.getString(R.string.hostname_not_filled), Snackbar.ANIMATION_MODE_FADE).show()
            } else {
                mainViewModel.ping(
                    ui.hostnameTextInputEditText.text.toString(),
                    ui.countRequestsAutoCompleteTextView.text.toString(),
                    ui.intervalRequestsAutoCompleteTextView.text.toString()
                )
            }
        }

        ui.clearButton.setOnClickListener {
            ui.outputTextInputEditText.text!!.clear()
            ui.hostnameTextInputEditText.text!!.clear()
            ui.countRequestsAutoCompleteTextView.text!!.clear()
            ui.intervalRequestsAutoCompleteTextView.text!!.clear()

            mainViewModel.listOfOutput.clear()
        }
    }

    override fun onPause() {
        super.onPause()

        mainViewModel.run {
            requestHostname.value = ui.hostnameTextInputEditText.text.toString()
            requestCount.value = ui.countRequestsAutoCompleteTextView.text.toString()
            requestInterval.value = ui.intervalRequestsAutoCompleteTextView.text.toString()
        }
    }
}


