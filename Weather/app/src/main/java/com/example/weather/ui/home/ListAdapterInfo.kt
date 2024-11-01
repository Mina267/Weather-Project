package com.example.weather.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.databinding.CardInfoBinding
import com.example.weather.model.Current
import java.text.NumberFormat
import java.util.Locale


class MyDiffUtilInfo : DiffUtil.ItemCallback<WeatherInfo>() {
    override fun areItemsTheSame(oldItem: WeatherInfo, newItem: WeatherInfo): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: WeatherInfo, newItem: WeatherInfo): Boolean {
        return oldItem == newItem
    }
}

sealed class WeatherInfo {
    data class UVIndex(val value: Double) : WeatherInfo()
    data class Humidity(val value: Int) : WeatherInfo()
    data class Wind(var speed: Double, val unit: String) : WeatherInfo()
    data class Pressure(val value: Int) : WeatherInfo()
    data class DewPoint(val value: Double) : WeatherInfo()
    data class Visibility(val value: Int) : WeatherInfo()
}



class ListAdapterInfo(private val myListener: (WeatherInfo) -> Unit) :
    ListAdapter<WeatherInfo, ListAdapterInfo.ViewHolder>(MyDiffUtilInfo()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardInfoBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val context = holder.binding.root.context

        when (item) {
            is WeatherInfo.UVIndex -> {
                holder.binding.txtType.text = context.getString(R.string.uv_index)
                holder.binding.txtData.text = NumberFormat.getInstance(Locale.getDefault()).format(item.value)
                if (context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                    holder.binding.txtType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sunny_24px, 0)
                } else {
                    holder.binding.txtType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sunny_24px, 0, 0, 0)
                }
            }
            is WeatherInfo.Humidity -> {
                holder.binding.txtType.text = context.getString(R.string.humidity)
                holder.binding.txtData.text = "${NumberFormat.getInstance(Locale.getDefault()).format(item.value)}%"
                if (context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                    holder.binding.txtType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.humidity_percentage_24px, 0)
                } else {
                    holder.binding.txtType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.humidity_percentage_24px, 0, 0, 0)
                }
            }
            is WeatherInfo.Wind -> {
                holder.binding.txtType.text = context.getString(R.string.wind)
                item.speed = roundToDecimal(item.speed, 1)

                if (item.unit == "imperial") {
                    holder.binding.txtData.text = "${NumberFormat.getInstance(Locale.getDefault()).format(item.speed)} ${context.getString(R.string.unit_mph)}"
                }else if (item.unit == "metric") {
                    holder.binding.txtData.text = "${NumberFormat.getInstance(Locale.getDefault()).format(item.speed)} ${context.getString(R.string.unit_ms)}"
                }

                if (context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                    holder.binding.txtType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.air_24px, 0)
                } else {
                    holder.binding.txtType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.air_24px, 0, 0, 0)
                }

            }
            is WeatherInfo.Pressure -> {
                holder.binding.txtType.text = context.getString(R.string.pressure)
                holder.binding.txtData.text = "${NumberFormat.getInstance(Locale.getDefault()).format(item.value)} ${context.getString(R.string.unit_mb)}"
                if (context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                    holder.binding.txtType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.compress_24px, 0)
                } else {
                    holder.binding.txtType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.compress_24px, 0, 0, 0)
                }
            }
            is WeatherInfo.DewPoint -> {
                holder.binding.txtType.text = context.getString(R.string.dew_point)
                holder.binding.txtData.text = "${NumberFormat.getInstance(Locale.getDefault()).format(item.value.toInt())}°"
                if (context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                    holder.binding.txtType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.dew_point_24px, 0)
                } else {
                    holder.binding.txtType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dew_point_24px, 0, 0, 0)
                }
            }
            is WeatherInfo.Visibility -> {
                holder.binding.txtType.text = context.getString(R.string.visibility)
                holder.binding.txtData.text = "${NumberFormat.getInstance(Locale.getDefault()).format(item.value)} ${context.getString(R.string.unit_kmh)}"
                if (context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                    holder.binding.txtType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visibility_24px, 0)
                } else {
                    holder.binding.txtType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.visibility_24px, 0, 0, 0)
                }
            }
        }

        holder.itemView.setOnClickListener {
            myListener(item)
        }
    }
    private fun roundToDecimal(value: Double, places: Int): Double {
        val normalizedValue = String.format(Locale.ENGLISH, "%.${places}f", value)
        return normalizedValue.toDouble()
    }

    class ViewHolder(val binding: CardInfoBinding) : RecyclerView.ViewHolder(binding.root)
}
