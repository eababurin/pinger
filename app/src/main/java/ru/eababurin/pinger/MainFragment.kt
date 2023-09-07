package ru.eababurin.pinger

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _ui = FragmentMainBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).setSupportActionBar(ui.topAppBar)
        setHasOptionsMenu(true)

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

        sharedPreferences = requireActivity().getSharedPreferences(KEY_THEME, Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()

        if (Configuration.UI_MODE_NIGHT_YES == sharedPreferences.getInt(
                KEY_THEME,
                Configuration.UI_MODE_NIGHT_NO
            )
        ) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onViewCreated(view, savedInstanceState)

        ui.outputTextInputEditText.keyListener = null

        ui.pingButton.setOnClickListener {
            if (ui.hostnameTextInputEditText.text!!.isEmpty()) {
                Toast.makeText(requireActivity(), "Напиши адрес хоста!", Toast.LENGTH_SHORT).show()
            } else {
                sendRequest(
                    ui.hostnameTextInputEditText.text.toString(),
                    ui.countRequestsAutoCompleteTextView.text.toString(),
                    ui.intervalRequestsAutoCompleteTextView.text.toString()
                )
            }
        }

        ui.clearButton.setOnClickListener {
            ui.outputTextInputEditText.text!!.clear()
            ui.hostnameTextInputEditText.text!!.clear()
        }
    }

    private fun sendRequest(hostname: String, counts: String, interval: String) {
        val inputCommand = mutableListOf("ping", "-c", counts, "-i", interval, hostname)

        Thread {
            val process = ProcessBuilder().command(inputCommand).start()
            val stdOutput = process.inputStream.bufferedReader()
            val stdErrOuput = process.errorStream.bufferedReader()

            try {
                while (true) {
                    val stdLine = stdOutput.readLine()
                    if (stdLine != null) {
                        requireActivity().runOnUiThread {
                            ui.outputTextInputEditText.text?.appendLine(stdLine)
                        }
                    } else {
                        val errLine = stdErrOuput.readLine()
                        if (errLine != null) {
                            requireActivity().runOnUiThread {
                                ui.outputTextInputEditText.text?.appendLine(errLine)
                            }
                            break
                        }
                    }
                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    Snackbar.make(ui.layout, R.string.unknown_error, Snackbar.LENGTH_SHORT).show()
                }
            } finally {
                process.destroy()
            }
        }.start()
    }
}


