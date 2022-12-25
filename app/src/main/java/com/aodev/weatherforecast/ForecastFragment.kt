package com.aodev.weatherforecast

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aodev.weatherforecast.databinding.FragmentForecastBinding
import com.aodev.weatherforecast.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForecastFragment : Fragment() {
    private lateinit var binding: FragmentForecastBinding
    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var GET: SharedPreferences

    val args : ForecastFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForecastBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GET = requireContext().getSharedPreferences("WeatherForecast", Context.MODE_PRIVATE)
        initView()
        getLiveData()
    }

    private fun initView() {
        binding.backBtn.setOnClickListener(View.OnClickListener {
            findNavController().popBackStack()
        })
    }

    private fun getLiveData() {
        val isFahrenheit: Boolean = GET.getBoolean("isFahrenheit", false)
        viewModel.searchCity(args.cityName)
        viewModel.forecastResponse.observe(viewLifecycleOwner) { forecast ->
            binding.rvForecastReport.visibility = View.VISIBLE
            binding.rvForecastReport.apply {
                manager = LinearLayoutManager(context)
                adapter = ForeCastRecyclerRowViewAdapter(forecast.list, isFahrenheit)
                layoutManager = manager
            }
        }

        viewModel.forecastError.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                if (error) {
                    binding.pbFdLoading.visibility = View.GONE
                    binding.rvForecastReport.visibility=View.GONE
                    binding.tvFdError.visibility = View.VISIBLE
                } else {
                    binding.pbFdLoading.visibility = View.GONE
                }
            }
        })

        viewModel.forecastLoading.observe(viewLifecycleOwner, Observer { loading ->
            loading?.let {
                if (loading) {
                    binding.pbFdLoading.visibility = View.VISIBLE
                    binding.tvFdError.visibility = View.GONE
                    binding.rvForecastReport.visibility = View.GONE
                } else {
                    binding.pbFdLoading.visibility = View.GONE
                    binding.tvFdError.visibility = View.GONE
                }
            }
        })
    }
}
