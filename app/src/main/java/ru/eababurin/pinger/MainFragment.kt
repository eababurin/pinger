package ru.eababurin.pinger

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
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

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.options_menu_change_theme -> changeTheme()
//        }
//        return super.onOptionsItemSelected(item)
//    }

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

        ui.outputTextInputEditText.keyListener = null
        ui.outputTextInputEditText.setOnLongClickListener {
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

        ui.pingButton.setOnClickListener {
            if (ui.hostnameTextInputEditText.text!!.isEmpty()) {
                Snackbar.make(
                    ui.layout,
                    resources.getString(R.string.hostname_not_filled),
                    Snackbar.ANIMATION_MODE_FADE
                ).show()
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

            //(requireActivity() as MainActivity).changeTheme()
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


