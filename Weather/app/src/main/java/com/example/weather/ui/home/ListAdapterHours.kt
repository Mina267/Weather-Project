package com.example.weather.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.databinding.CardHourlyBinding
import com.example.weather.model.Hourly
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class MyDiffUtil : DiffUtil.ItemCallback<Hourly>() {
    override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem == newItem
    }
}

class ListAdapterHours(private val myListener: (Hourly) -> Unit) : ListAdapter<Hourly, ListAdapterHours.ViewHolder>(
    MyDiffUtil()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardHourlyBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        val dateFormat = SimpleDateFormat("h a", Locale.getDefault())
        val time = dateFormat.format(Date(item.dt * 1000L))

        // Bind data to the views
        holder.binding.txtTime.text = time
        holder.binding.txtHourDegree.text = " ${NumberFormat.getInstance(Locale.getDefault()).format(item.temp.roundToInt())}°"
        holder.binding.txtHumidity.text = "${NumberFormat.getInstance(Locale.getDefault()).format(item.humidity)}%"


        if (item.weather.firstOrNull()?.icon == "01d")
        {
            holder.binding.imgWeatherIcon.layoutParams.width = 115;
            holder.binding.imgWeatherIcon.layoutParams.height = 140;
            holder.binding.imgWeatherIcon.setImageResource(R.drawable.sunweather)
        }
        else if (item.weather.firstOrNull()?.icon == "01n")
        {
            holder.binding.imgWeatherIcon.layoutParams.width = 115;
            holder.binding.imgWeatherIcon.layoutParams.height = 140;
            holder.binding.imgWeatherIcon.setImageResource(R.drawable.nightweather)
        } else {
            val iconUrl =
                "https://openweathermap.org/img/wn/${item.weather.firstOrNull()?.icon}@2x.png"
            Glide.with(holder.itemView.context).load(iconUrl).into(holder.binding.imgWeatherIcon)
        }
        holder.itemView.setOnClickListener {
            myListener(item)
        }
    }

    class ViewHolder(val binding: CardHourlyBinding) : RecyclerView.ViewHolder(binding.root)
}
