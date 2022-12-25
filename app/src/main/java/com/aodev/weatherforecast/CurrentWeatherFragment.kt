package com.aodev.weatherforecast

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aodev.weatherforecast.databinding.FragmentCurrentWeatherBinding
import com.aodev.weatherforecast.utils.Constants
import com.aodev.weatherforecast.utils.Util
import com.aodev.weatherforecast.viewmodel.WeatherViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrentWeatherFragment : Fragment() {
    private lateinit var binding: FragmentCurrentWeatherBinding
    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var GET: SharedPreferences
    private lateinit var SET: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrentWeatherBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GET = requireContext().getSharedPreferences("WeatherForecast", MODE_PRIVATE)
        SET = GET.edit()
        initView()
        getLiveData()
    }

    private fun initView() {
        val currentFahrenheit: Boolean = GET.getBoolean("isFahrenheit", false)

        if(currentFahrenheit){
            binding.toggleButton.text = "°C"
        }else{
            binding.toggleButton.text = "°F"
        }

        var cName = GET.getString("cityName", "thailand")
        viewModel.searchCity(cName!!)

        manager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.imgSearchCity.setOnClickListener(View.OnClickListener {
            val city = binding.edtCityName.text.toString()
            SET.putString("cityName", city)
            SET.apply()
            viewModel.searchCity(city)
        })

        binding.tvViewFullReport.setOnClickListener(View.OnClickListener {
            var city: String = GET.getString("cityName", "thailand")!!
            val action =
                CurrentWeatherFragmentDirections.actionCurrentWeatherFragmentToForecastFragment(city)
            findNavController().navigate(action)
        })

        binding.edtCityName.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                val city = binding.edtCityName.text.toString()
                viewModel.searchCity(city)
                true
            }
            false
        }

        binding.toggleButton.setOnClickListener(View.OnClickListener {
            val isFahrenheit: Boolean = GET.getBoolean("isFahrenheit", false)
            val newFahrenheit: Boolean = !isFahrenheit
            SET.putBoolean("isFahrenheit", newFahrenheit)
            SET.apply()

            if(newFahrenheit){
                binding.toggleButton.text = "°C"
            }else{
                binding.toggleButton.text = "°F"
            }
            val city: String = GET.getString("cityName", "thailand")!!
            viewModel.searchCity(city)
        })
    }

    private fun getLiveData() {

        viewModel.weatherResponse.observe(viewLifecycleOwner) { it ->
            binding.llData.visibility = View.VISIBLE
            val isFahrenheit: Boolean = GET.getBoolean("isFahrenheit", false)
            var temp = it.main.temp
            var temp_max = it.main.temp_max
            var temp_min = it.main.temp_min
            var unit = "°C"
            if (isFahrenheit) {
                unit = "°F"
                temp = Util.toFahrenheit(temp)
                temp_max = Util.toFahrenheit(temp_max)
                temp_min = Util.toFahrenheit(temp_min)
            }

            binding.apply {
                llData.visibility = View.VISIBLE

                tvCityName.text = "${it.name} (${it.sys.country})"
                tvDateTime.text = Util.getDayOfMonth(it.dt)

                Glide.with(this@CurrentWeatherFragment)
                    .load(Constants.BASE_URL_ICON + it.weather.get(0).icon + "@2x.png")
                    .into(imgWeatherPictures)
                tvDegree.text = "${String.format("%.2f", temp)} $unit"
                tvTemp.text ="${String.format("%.2f", temp)}°"
                tvHumidity.text = it.main.humidity.toString() + "%"
                tvWindSpeed.text = it.wind.speed.toString()
                tvMaxMin.text = String.format("%.2f", temp_max)+" / "+ String.format("%.2f", temp_min)+unit
                tvDescription.text = it.weather[0].main
            }
        }

        viewModel.forecastResponse.observe(viewLifecycleOwner) { forecast ->
            val isFahrenheit: Boolean = GET.getBoolean("isFahrenheit", false)
            binding.rvForecast.visibility = View.VISIBLE
            binding.rvForecast.apply {

                adapter = ForeCastRecyclerViewAdapter(forecast.list, isFahrenheit)
                layoutManager = manager
            }
        }

        viewModel.weatherError.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                if (error) {
                    binding.pbLoading.visibility = View.GONE
                    binding.llData.visibility=View.GONE
                    binding.tvError.visibility = View.VISIBLE
                } else {
                    binding.pbLoading.visibility = View.GONE
                }
            }
        })

        viewModel.weatherLoading.observe(viewLifecycleOwner, Observer { loading ->
            loading?.let {
                if (loading) {
                    binding.pbLoading.visibility = View.VISIBLE
                    binding.tvError.visibility = View.GONE
                    binding.llData.visibility = View.GONE
                } else {
                    binding.pbLoading.visibility = View.GONE
                    binding.tvError.visibility = View.GONE
                }
            }
        })
        viewModel.forecastError.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                if (error) {
                    binding.pbFLoading.visibility = View.GONE
                    binding.rvForecast.visibility=View.GONE
                    binding.tvFError.visibility = View.VISIBLE
                } else {
                    binding.pbFLoading.visibility = View.GONE
                }
            }
        })

        viewModel.forecastLoading.observe(viewLifecycleOwner, Observer { loading ->
            loading?.let {
                if (loading) {
                    binding.pbFLoading.visibility = View.VISIBLE
                    binding.tvFError.visibility = View.GONE
                    binding.rvForecast.visibility = View.GONE
                } else {
                    binding.pbFLoading.visibility = View.GONE
                    binding.tvFError.visibility = View.GONE
                }
            }
        })
    }
}