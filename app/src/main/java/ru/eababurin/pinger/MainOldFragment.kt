package ru.eababurin.pinger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import ru.eababurin.pinger.databinding.FragmentMainBinding
import ru.eababurin.pinger.databinding.FragmentMainOldBinding
import java.net.InetAddress

class MainOldFragment : Fragment() {

    private var _binding: FragmentMainOldBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainOldBinding.inflate(inflater, container, false)
//        val view = binding.root
//        return view
        return binding.root
//        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCheck.setOnClickListener {
            if (binding.editextAddress.text.isEmpty()) {
                Toast.makeText(requireContext(), "Введите адрес", Toast.LENGTH_LONG).show()
            } else {
                checkAddress(binding.editextAddress.text.toString())
            }
        }

        binding.buttonKvmos2.setOnClickListener {
            binding.editextAddress.setText("os2.kvm.smailru.net", TextView.BufferType.EDITABLE)
        }

        binding.buttonKvmos3.setOnClickListener {
            binding.editextAddress.setText("os3.kvm.smailru.net", TextView.BufferType.EDITABLE)
        }

        binding.buttonYandex.setOnClickListener {
            binding.editextAddress.setText("yandex.ru", TextView.BufferType.EDITABLE)
        }

        binding.editextAddress.addTextChangedListener {
            if (binding.textviewResult.isVisible) binding.textviewResult.visibility = View.INVISIBLE
        }

        binding.buttonClear.setOnClickListener {
            binding.editextAddress.text.clear()
            if (binding.textviewResult.isVisible) binding.textviewResult.visibility = View.INVISIBLE
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

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}