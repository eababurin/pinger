package ru.eababurin.pinger.ui.settings.favourites

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import ru.eababurin.pinger.viewmodels.MainViewModel
import ru.eababurin.pinger.R
import ru.eababurin.pinger.databinding.FragmentFavouritesBinding
import ru.eababurin.pinger.utils.KEY_FAVOURITES

class FavouritesFragment : Fragment() {

    private var _ui: FragmentFavouritesBinding? = null
    private val ui get() = _ui!!

    private lateinit var mainViewModel: MainViewModel

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    private lateinit var adapter: FavouritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _ui = FragmentFavouritesBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).setSupportActionBar(ui.topAppBar)
        setHasOptionsMenu(true)

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        return ui.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPreferencesEditor = sharedPreferences.edit()

        adapter = FavouritesAdapter(
            PreferenceManager
                .getDefaultSharedPreferences(requireContext())
                .getStringSet(KEY_FAVOURITES, emptySet())!!
                .toMutableList()
        )

        ui.topAppBar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        ui.recyclerView.adapter = adapter

        ui.addHostButton.setOnClickListener {
            if (ui.addHostEditText.text.isNullOrEmpty())
                Snackbar.make(
                    ui.layout,
                    requireActivity().resources.getString(R.string.error_input_address),
                    Snackbar.LENGTH_SHORT
                ).show()
            else {
                sharedPreferencesEditor
                    .putStringSet(
                        KEY_FAVOURITES,
                        adapter.addItem(ui.addHostEditText.text.toString()).toSet()
                    )
                    .commit()
                ui.addHostEditText.text.clear()
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroy() {
        _ui = null
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.appbarmenu, menu)

        menu.findItem(R.id.menuOpenSettings).isVisible = false
        menu.findItem(R.id.menuDeleteItems).isVisible = true

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuDeleteItems -> {
                if (adapter.checkedCount() > 0) {
                    sharedPreferencesEditor
                        .putStringSet(KEY_FAVOURITES, adapter.removeItems().toSet())
                        .commit()
                    adapter.notifyDataSetChanged()

                    for (i in 0..ui.recyclerView.childCount) {
                        val view = ui.recyclerView.getChildAt(i)
                        if (view is CheckBox) {
                            view.findViewById<CheckBox>(R.id.checkbox).isChecked = false
                        }

                    }
                } else {
                    Snackbar.make(
                        ui.layout,
                        resources.getString(R.string.selected_items),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}