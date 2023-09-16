package ru.eababurin.pinger

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FavouritesAdapter(private val favouritesList: MutableList<String>) :
    RecyclerView.Adapter<FavouritesAdapter.ViewHolder>() {

    val checkedItems = mutableListOf<String>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(viewGroup.context)
                .inflate(R.layout.favourites_item_recycler_view, viewGroup, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.hostname.text = favouritesList[position]

        holder.checkbox.setOnClickListener {
            if (holder.checkbox.isChecked) {
                checkedItems.add(holder.hostname.text.toString())
            } else {
                checkedItems.remove(holder.hostname.text.toString())
            }
            Log.d("TEST", checkedItems.toString())
        }
    }

    override fun getItemCount() = favouritesList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hostname: TextView
        val checkbox: CheckBox

        init {
            hostname = view.findViewById(R.id.hostname)
            checkbox = view.findViewById(R.id.checkbox)
        }
    }

    fun removeItems(): MutableList<String> {
        favouritesList.removeAll(checkedItems)
        return favouritesList
    }

    fun addItem(item: String): MutableList<String> {
        favouritesList.add(item)
        return favouritesList
    }
}