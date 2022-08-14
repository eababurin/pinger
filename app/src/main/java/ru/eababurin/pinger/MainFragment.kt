package ru.eababurin.pinger

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import ru.eababurin.pinger.databinding.FragmentMainBinding
import java.net.InetAddress

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var isFirstRun = true
    private lateinit var array: Array<out String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        array = resources.getStringArray(R.array.kvms)


        binding.spinnerFavoritesList.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (isFirstRun) {
                        isFirstRun = false
                        binding.edittextAddress.text.clear()
                        binding.spinnerFavoritesList.setSelection(array.size-1, false)
                    } else {
                        binding.edittextAddress.setText(
                            array[position],
                            TextView.BufferType.EDITABLE
                        )
                        Toast.makeText(requireActivity(), position.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }

        binding.imagebuttonAddToFavorites.setOnClickListener {
            Toast.makeText(requireActivity(), "Функция пока недоступна", Toast.LENGTH_LONG).show()
        }

        binding.imagebuttonChangeTheme.setOnClickListener {
            Toast.makeText(requireActivity(), "Функция пока недоступна", Toast.LENGTH_LONG).show()
        }

        binding.buttonCheckAvailability.setOnClickListener {
            if (binding.edittextAddress.text.isEmpty()) {
                Toast.makeText(requireContext(), "Введите адрес", Toast.LENGTH_LONG).show()
            } else {
                checkAddress(binding.edittextAddress.text.toString())
            }
        }

        binding.edittextAddress.addTextChangedListener {
            if (binding.textviewResult.isVisible) binding.textviewResult.visibility = View.INVISIBLE
        }

        binding.buttonClear.setOnClickListener {
            if (binding.textviewResult.isVisible) binding.textviewResult.visibility = View.INVISIBLE
            binding.edittextAddress.text.clear()
            binding.spinnerFavoritesList.setSelection(array.size-1, false)

        }
    }

    private fun checkAddress(host: String) {
        if (binding.textviewResult.isVisible) binding.textviewResult.visibility = View.INVISIBLE

        Thread {
            try {
                val address: InetAddress = InetAddress.getByName(host)
                val isReachable = address.isReachable(1000)

                if (isReachable) {
                    requireActivity().runOnUiThread {
                        binding.textviewResult.apply {
                            text = "Работает :D"
                            visibility = View.VISIBLE
                        }
                    }
                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    binding.textviewResult.apply {
                        text = "Не работает >_<"
                        visibility = View.VISIBLE
                    }
                }
            }
        }.start()
    }
}