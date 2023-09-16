package ru.eababurin.pinger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.eababurin.pinger.databinding.FragmentFavouritesBinding


class FavouritesFragment : Fragment() {

    private var _ui: FragmentFavouritesBinding? = null
    private val ui get() = _ui!!

    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _ui = FragmentFavouritesBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).setSupportActionBar(ui.topAppBar)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        return ui.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ui.topAppBar.setNavigationOnClickListener { requireActivity().onBackPressed() }

    }

    override fun onDestroy() {
        _ui = null
        super.onDestroy()
    }
}