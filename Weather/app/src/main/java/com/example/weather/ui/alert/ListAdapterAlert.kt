package com.example.weather.ui.alert

import android.location.Geocoder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.CardFavBinding
import com.example.weather.model.AlertsData
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyDiffUtilDaily : DiffUtil.ItemCallback<AlertsData>() {
    override fun areItemsTheSame(oldItem: AlertsData, newItem: AlertsData): Boolean {
        // Check if items represent the same location by lat and lon
        return oldItem.time == newItem.time
    }

    override fun areContentsTheSame(oldItem: AlertsData, newItem: AlertsData): Boolean {
        return oldItem == newItem
    }
}

class ListAdapterAlert(
    private val myListener: (AlertsData) -> Unit,
    private val removeListener: (AlertsData) -> Unit
) : ListAdapter<AlertsData, ListAdapterAlert.ViewHolder>(MyDiffUtilDaily()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardFavBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        val formattedTime = formatAlertTime(item.time)

        // Convert latitude and longitude to address and set it
        val address = getAddress(holder.binding.root.context, item.latitude, item.longitude)
        holder.binding.txtLocationName.text = address + "\n" + formattedTime

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
                addresses[0].subAdminArea ?: ""
            } else {
                "Location not found"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            "Location not available"
        }
    }


    private fun formatAlertTime(timeInMillis: Long): String {
        val date = Date(timeInMillis)
        val formatter = SimpleDateFormat("E, MMM d - h:mm a", Locale.getDefault())
        return formatter.format(date)
    }


    class ViewHolder(val binding: CardFavBinding) : RecyclerView.ViewHolder(binding.root)
}
