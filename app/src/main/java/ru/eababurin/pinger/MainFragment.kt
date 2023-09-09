package ru.eababurin.pinger

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.eababurin.pinger.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    private var _ui: FragmentMainBinding? = null
    private val ui get() = _ui!!

    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _ui = FragmentMainBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).setSupportActionBar(ui.topAppBar)
        mainViewModel =
            ViewModelProvider(requireActivity())[MainViewModel::class.java]/*setHasOptionsMenu(true)*/

        return ui.root
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
            isExecute.observe(viewLifecycleOwner) {
                ui.pingAborted.isEnabled = it
            }
            isEmptyOutput.observe(viewLifecycleOwner) {
                ui.clearButton.isEnabled = !it
            }
        }

        ui.hostnameTextInputEditText.apply {
            onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    if (ui.hostnameTextInputEditText.text.isNullOrBlank()) {
                        ui.hostnameTextInputEditText.error =
                            requireActivity().resources.getString(R.string.error_field_cannot_be_empty)
                        ui.hostnameTextInputLayout.endIconMode = TextInputLayout.END_ICON_NONE
                        Log.d("TEST", "Меняем в onFocusChangeListener -> hasFocus")
                    } else {
                        ui.hostnameTextInputEditText.error = null
                        ui.hostnameTextInputLayout.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                    }
                }
            }
        }

        ui.countRequestsAutoCompleteTextView.apply {
            onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    if (ui.countRequestsAutoCompleteTextView.text.contains(
                            requireActivity().resources.getString(
                                R.string.infinity
                            )
                        )
                    ) ui.countRequestsAutoCompleteTextView.text.clear()

                    if (ui.countRequestsAutoCompleteTextView.text.isNullOrBlank()) ui.countRequestsAutoCompleteTextView.error =
                        requireActivity().resources.getString(R.string.error_less_than_zero)
                    else ui.countRequestsAutoCompleteTextView.error = null
                }

                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun onTextChanged(
                        s: CharSequence?, start: Int, before: Int, count: Int
                    ) {
                        if ((s.isNullOrBlank()) || (s.toString() == "0")) {
                            ui.countRequestsAutoCompleteTextView.error =
                                requireActivity().resources.getString(R.string.error_less_than_zero)
                        } else {
                            ui.countRequestsAutoCompleteTextView.error = null
                        }
                    }
                })
            }
        }

        ui.intervalRequestsAutoCompleteTextView.apply {
            onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    if (ui.intervalRequestsAutoCompleteTextView.text.isNullOrBlank())
                        ui.intervalRequestsAutoCompleteTextView.error =
                            requireActivity().resources.getString(R.string.error_less_than_zero)
                    else ui.intervalRequestsAutoCompleteTextView.error = null
                }
            }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if ((s.isNullOrBlank()) || (s.toString() == "0")) {
                        ui.intervalRequestsAutoCompleteTextView.error =
                            requireActivity().resources.getString(R.string.error_less_than_zero)
                    } else {
                        ui.intervalRequestsAutoCompleteTextView.error = null
                    }
                }
            })
        }

        ui.outputTextInputEditText.apply {
            keyListener = null
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    ui.outputTextInputEditText.setSelection(p0!!.length)
                }
            })
            setOnLongClickListener {
                if (ui.outputTextInputEditText.text!!.isEmpty()) false
                else {
                    val clipboardManager =
                        requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData =
                        ClipData.newPlainText(null, (it as TextInputEditText).text.toString())
                    clipboardManager.setPrimaryClip(clipData)

                    Snackbar.make(
                        ui.layout,
                        requireActivity().resources.getString(R.string.output_was_copied),
                        Snackbar.ANIMATION_MODE_FADE
                    ).show()

                    true
                }
            }
        }

        ui.pingButton.apply {
            setOnClickListener {
                if ((ui.hostnameTextInputEditText.error == null) && (ui.countRequestsAutoCompleteTextView.error == null) && (ui.intervalRequestsAutoCompleteTextView.error == null)) {
                    mainViewModel.isExecute.value = true
                    mainViewModel.ping(
                        ui.hostnameTextInputEditText.text.toString(),
                        ui.countRequestsAutoCompleteTextView.text.toString(),
                        ui.intervalRequestsAutoCompleteTextView.text.toString()
                    )
                } else Snackbar.make(
                    ui.layout,
                    resources.getString(R.string.check_fields_are_filled_in),
                    Snackbar.ANIMATION_MODE_FADE
                ).show()
            }
        }

        ui.clearButton.apply {
            setOnClickListener {
                ui.outputTextInputEditText.text!!.clear()

                mainViewModel.listOfOutput.clear()
                mainViewModel.isEmptyOutput.value = true
            }
        }

        ui.pingAborted.apply {
            setOnClickListener {
                mainViewModel.isExecute.postValue(false)
            }
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

    override fun onDestroy() {
        _ui = null
        super.onDestroy()
    }
}

/*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.options_menu, menu)
    super.onCreateOptionsMenu(menu, inflater)
}

override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
        R.id.options_menu_change_theme -> changeTheme()
    }
    return super.onOptionsItemSelected(item)
}*/