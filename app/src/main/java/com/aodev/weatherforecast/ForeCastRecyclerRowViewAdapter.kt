package com.aodev.weatherforecast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aodev.weatherforecast.model.forecast.WeatherData
import com.aodev.weatherforecast.utils.Constants
import com.aodev.weatherforecast.utils.Util
import com.bumptech.glide.Glide

class ForeCastRecyclerRowViewAdapter(
    private val items: List<WeatherData>,
    var isFahrenheit: Boolean
) : RecyclerView.Adapter<ForeCastRecyclerRowViewAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {

        val itemHolder = LayoutInflater.from(parent.context).inflate(
            R.layout.forecast_item_row,
            parent,
            false
        )
        return ItemHolder(itemHolder)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        var data: WeatherData = items.get(position)
        var unit = "°C"
        var temp: Double = data.main.temp
        if (isFahrenheit) {
            unit = "°F"
            temp = Util.toFahrenheit(temp)
        }
        holder.tv_time_item.text = Util.getDateTimeAsFormattedString(data.dt)
        holder.tv_date_item.text = Util.getDayOfWeek(data.dt)
        holder.tv_temp_item.text = "${String.format("%.2f", temp)} $unit"
        Glide.with(holder.itemView)
            .load(Constants.BASE_URL_ICON + data.weather.get(0).icon + "@2x.png")
            .into(holder.img_weather_item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_date_item = itemView.findViewById<TextView>(R.id.tv_date_item_row)
        var tv_time_item = itemView.findViewById<TextView>(R.id.tv_time_item_row)
        var tv_temp_item = itemView.findViewById<TextView>(R.id.tv_temp_item_row)
        var img_weather_item = itemView.findViewById<ImageView>(R.id.img_weather_item_row)
    }
}