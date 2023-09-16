package ru.eababurin.pinger.ui.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import ru.eababurin.pinger.viewmodels.MainViewModel
import ru.eababurin.pinger.R
import ru.eababurin.pinger.databinding.FragmentMainBinding
import ru.eababurin.pinger.ui.settings.SettingsFragment
import ru.eababurin.pinger.utils.KEY_FAVOURITES


class MainFragment : Fragment() {

    private var _ui: FragmentMainBinding? = null
    private val ui get() = _ui!!

    private lateinit var mainViewModel: MainViewModel

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    private var favouritesList = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _ui = FragmentMainBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).setSupportActionBar(ui.topAppBar)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setHasOptionsMenu(true)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPreferencesEditor = sharedPreferences.edit()

        return ui.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.run {
            mutableOutput.observe(viewLifecycleOwner) {
                val list = it.joinToString("\n")
                ui.outputEditText.text = Editable.Factory.getInstance().newEditable(list)
            }
            pingError.observe(viewLifecycleOwner) {
                Snackbar.make(ui.layout, it, Snackbar.LENGTH_SHORT).show()
            }
            requestHostname.observe(viewLifecycleOwner) {
                ui.hostnameEditText.setText(it)
            }
            requestCount.observe(viewLifecycleOwner) {
                ui.countEditText.setText(it)
            }
            requestInterval.observe(viewLifecycleOwner) {
                ui.intervalEditText.setText(it)
            }
            isExecute.observe(viewLifecycleOwner) {
                ui.abortedButton.isEnabled = it
                ui.executeButton.isEnabled = !it
            }
            isEmptyOutput.observe(viewLifecycleOwner) {
                ui.clearButton.isEnabled = !it

                if (mainViewModel.isEmptyOutput.value == true) {
                    ui.outputEditText.typeface = Typeface.DEFAULT
                } else {
                    ui.outputEditText.typeface = Typeface.MONOSPACE
                }
            }
        }

        ui.hostnameEditText.apply {

            favouritesList = sharedPreferences.getStringSet(KEY_FAVOURITES, mutableSetOf(""))!!.toMutableSet()
            if (!favouritesList.contains(""))
                setAdapter(ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, favouritesList.toList()))
        }

        ui.countEditText.apply {
            val count = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("default_count", "")
            if (count == "0") {
                setText(resources.getString(R.string.infinity))
                mainViewModel.requestCount.value = resources.getString(R.string.infinity)
            } else if (count != "") {
                setText(count)
                mainViewModel.requestCount.value = count
            }
        }

        ui.intervalEditText.apply {
            val interval = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("default_interval", "")
            if (interval != "")  {
                setText(interval)
                mainViewModel.requestInterval.value = interval
            }
        }

        ui.outputEditText.apply {
            textSize = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getInt("output_font_size", 14).toFloat()
            keyListener = null
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    ui.outputEditText.isEnabled =
                        !ui.outputEditText.text.isNullOrEmpty()
                    ui.outputEditText.setSelection(p0!!.length)

                }
            })
            setOnLongClickListener {
                if (ui.outputEditText.text!!.isEmpty()) false
                else {
                    val clipboardManager =
                        requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData =
                        ClipData.newPlainText(null, (it as TextInputEditText).text.toString())
                    clipboardManager.setPrimaryClip(clipData)

                    Snackbar.make(
                        ui.layout,
                        requireActivity().resources.getString(R.string.output_was_copied),
                        Snackbar.LENGTH_SHORT
                    ).show()

                    true
                }
            }
        }

        ui.executeButton.apply {
            setOnClickListener {
                if ((!ui.hostnameEditText.text.isNullOrBlank())) {
                    mainViewModel.isExecute.value = true
                    mainViewModel.ping(
                        ui.hostnameEditText.text.toString(),
                        ui.countEditText.text.toString(),
                        ui.intervalEditText.text.toString()
                    )

                    if (!ui.hostnameEditText.text.isNullOrBlank()) {

                        if (sharedPreferences.getBoolean("auto_add_hosts", true)) {
                            sharedPreferencesEditor.apply {
                                remove(KEY_FAVOURITES)
                                favouritesList.add(ui.hostnameEditText.text.toString())
                                if (favouritesList.contains("")) favouritesList.remove("")
                                putStringSet(KEY_FAVOURITES, favouritesList.toSet())
                            }.apply()
                        }

                        ui.hostnameEditText.setAdapter(
                            ArrayAdapter(
                                requireActivity(),
                                android.R.layout.simple_list_item_1,
                                favouritesList.toList()
                            )
                        )
                    }
                } else {



                    Snackbar.make(
                        ui.layout,
                        resources.getString(R.string.check_address_field),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        ui.clearButton.apply {
            setOnClickListener {
                ui.outputEditText.text!!.clear()

                mainViewModel.listOfOutput.clear()
                mainViewModel.isEmptyOutput.value = true
            }
        }

        ui.abortedButton.apply {
            setOnClickListener {
                mainViewModel.isExecute.postValue(false)
            }
        }
    }

    override fun onPause() {
        super.onPause()

        mainViewModel.run {
            requestHostname.value = ui.hostnameEditText.text.toString()
            requestCount.value = ui.countEditText.text.toString()
            requestInterval.value = ui.intervalEditText.text.toString()
        }
    }

    override fun onDestroy() {
        _ui = null
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.appbarmenu, menu)

        menu.findItem(R.id.menuSettings).isVisible = true
        menu.findItem(R.id.deleteItems).isVisible = false

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSettings -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentLayout, SettingsFragment()).addToBackStack(null).commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}