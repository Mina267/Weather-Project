package com.example.weather.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.databinding.CardDailyBinding
import com.example.weather.model.Daily
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MyDiffUtilDaily : DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem == newItem
    }
}

class ListAdapterDaily(private val myListener: (Daily) -> Unit) : ListAdapter<Daily, ListAdapterDaily.ViewHolder>(
    MyDiffUtilDaily()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardDailyBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.txtDayName.text = getDayName(item.dt.toLong())

        holder.binding.txtDailyPrecipitation.text = "${
            NumberFormat.getInstance(Locale.getDefault()).format((item.pop * 100).toInt())}%"

        val maxTemp = item.temp.max.toInt()
        val minTemp = item.temp.min.toInt()
        holder.binding.txtMaxMinaDegree.text = "${NumberFormat.getInstance(Locale.getDefault()).format(maxTemp)}°  ${NumberFormat.getInstance(Locale.getDefault()).format(minTemp)}°"

        if (item.weather.firstOrNull()?.icon == "01d")
        {
            holder.binding.imgWeatherIcon.layoutParams.width = 100;
            holder.binding.imgWeatherIcon.layoutParams.height = 100;
            holder.binding.imgWeatherIcon.setImageResource(R.drawable.sunweather)
        }
        else if (item.weather.firstOrNull()?.icon == "01n")
        {
            holder.binding.imgWeatherIcon.layoutParams.width = 100;
            holder.binding.imgWeatherIcon.layoutParams.height = 100;
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

    private fun getDayName(dt: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dt * 1000
        return SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
    }


    class ViewHolder(val binding: CardDailyBinding) : RecyclerView.ViewHolder(binding.root)
}
