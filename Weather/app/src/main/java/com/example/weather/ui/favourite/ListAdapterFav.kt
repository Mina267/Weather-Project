package com.example.weather.ui.favourite

import android.location.Geocoder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.CardFavBinding
import com.example.weather.model.Favourites
import java.io.IOException
import java.util.Locale

class MyDiffUtilDaily : DiffUtil.ItemCallback<Favourites>() {
    override fun areItemsTheSame(oldItem: Favourites, newItem: Favourites): Boolean {
        // Check if items represent the same location by lat and lon
        return oldItem.lat == newItem.lat && oldItem.lon == newItem.lon
    }

    override fun areContentsTheSame(oldItem: Favourites, newItem: Favourites): Boolean {
        return oldItem == newItem
    }
}

class ListAdapterFav(
    private val myListener: (Favourites) -> Unit,
    private val removeListener: (Favourites) -> Unit
) : ListAdapter<Favourites, ListAdapterFav.ViewHolder>(MyDiffUtilDaily()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardFavBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        // Convert latitude and longitude to address and set it
        val address = getAddress(holder.binding.root.context, item.lat, item.lon)
        holder.binding.txtLocationName.text = address

        // Handle click for remove button
        holder.binding.fabRemoveFavorite.setOnClickListener {
            removeListener(item)
        }

        // Handle click on the item
        holder.itemView.setOnClickListener {
            myListener(item)
        }
    }

    /* Convert latitude and longitude to address */
    private fun getAddress(context: android.content.Context, lat: Double, lon: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].subAdminArea ?: "Location not available"
            } else {
                "Location not found"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            "Location not available"
        }
    }

    class ViewHolder(val binding: CardFavBinding) : RecyclerView.ViewHolder(binding.root)
}
