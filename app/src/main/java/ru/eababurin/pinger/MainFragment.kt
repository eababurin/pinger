package ru.eababurin.pinger

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import ru.eababurin.pinger.databinding.FragmentMainNewBinding

class MainFragment : Fragment() {

    companion object {
        const val FAVOURITES_KEY = "Favourites"
        const val THEME_KEY = "Theme"
    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    private var _binding: FragmentMainNewBinding? = null
    private val binding get() = _binding!!

    private lateinit var array : MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainNewBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).setSupportActionBar(binding.topAppBar)

        setHasOptionsMenu(true)

        array = mutableListOf()

        return binding.root
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.options_menu_change_theme -> {
                changeTheme()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeTheme() {
        when (requireActivity().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPreferencesEditor.putInt(THEME_KEY, Configuration.UI_MODE_NIGHT_YES)
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPreferencesEditor.putInt(THEME_KEY, Configuration.UI_MODE_NIGHT_NO)
            }
        }
        sharedPreferencesEditor.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        sharedPreferences = requireActivity().getSharedPreferences(THEME_KEY, Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()

        if (Configuration.UI_MODE_NIGHT_YES == sharedPreferences.getInt(
                THEME_KEY,
                Configuration.UI_MODE_NIGHT_NO
            )
        ) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onViewCreated(view, savedInstanceState)

        binding.outputTextInputEditText.inputType = InputType.TYPE_NULL

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(FAVOURITES_KEY)) {
                array.addAll(savedInstanceState.getStringArray(FAVOURITES_KEY) as Array<out String>)

//                val spinnerAdapter : SpinnerAdapter = ArrayAdapter.createFromResource(requireContext(), array, android.R.layout.simple_spinner_item)
            }
        }

//        binding.spinnerFavoritesList.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//
//                override fun onNothingSelected(parent: AdapterView<*>?) {}
//
//                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                    if (array.isEmpty()) {
//                        binding.edittextAddress.text.clear()
//                    } else {
//                        binding.edittextAddress.setText(
//                            array[position],
//                            TextView.BufferType.EDITABLE
//                        )
//                    }
//                }
//            }

//        binding.imagebuttonAddToFavorites.setOnClickListener {
//            if (binding.edittextAddress.text.isEmpty()) {
//                Toast.makeText(requireActivity(), "Введите адрес сервера", Toast.LENGTH_LONG).show()
//                return@setOnClickListener
//            }
//            if (array.contains(binding.edittextAddress.text.toString())) {
//                Toast.makeText(requireActivity(), "Такой элемент уже есть", Toast.LENGTH_LONG).show()
//            } else {
//                array.add(binding.edittextAddress.text.toString())
//                Toast.makeText(requireActivity(), "Добавлено в избранное", Toast.LENGTH_LONG).show()
//            }
//        }
//
//        binding.buttonCheckAvailability.setOnClickListener {
//            if (binding.edittextAddress.text.isEmpty()) {
//                Toast.makeText(requireContext(), "Введите адрес", Toast.LENGTH_LONG).show()
//            } else {
//                checkAddress(binding.edittextAddress.text.toString())
//            }
//        }
//
//        binding.edittextAddress.addTextChangedListener {
//            if (binding.textviewResult.isVisible) binding.textviewResult.visibility = View.INVISIBLE
//        }
//
//        binding.buttonClear.setOnClickListener {
//            if (binding.textviewResult.isVisible) binding.textviewResult.visibility = View.INVISIBLE
//            binding.edittextAddress.text.clear()
//            binding.spinnerFavoritesList.setSelection(array.size - 1, false)
//
//        }
    }

//    private fun checkAddress(host: String) {
//        if (binding.textviewResult.isVisible) binding.textviewResult.visibility = View.INVISIBLE
//
//        Thread {
//            try {
//                val address: InetAddress = InetAddress.getByName(host)
//                val isReachable = address.isReachable(1000)
//
//                if (isReachable) {
//                    requireActivity().runOnUiThread {
//                        binding.textviewResult.apply {
//                            text = "Сервер доступен"
//                            setTextColor(resources.getColor(R.color.server_is_available, null))
//                            visibility = View.VISIBLE
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                requireActivity().runOnUiThread {
//                    binding.textviewResult.apply {
//                        text = "Сервер недоступен"
//                        setTextColor(resources.getColor(R.color.server_is_unavailable, null))
//                        visibility = View.VISIBLE
//                    }
//                }
//            }
//        }.start()
//    }
}