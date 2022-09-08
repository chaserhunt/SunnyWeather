package com.example.sunnyweather.ui.weather

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.sunnyweather.R
import com.example.sunnyweather.databinding.FragmentWeatherBinding
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.model.getSky
import com.example.sunnyweather.ui.master.MasterViewModel
import java.text.SimpleDateFormat
import java.util.*

class WeatherFragment: Fragment() {

    val viewModel by activityViewModels<MasterViewModel>()
    lateinit var drawerLayout: DrawerLayout

    private var _binding: FragmentWeatherBinding?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        drawerLayout = binding.drawerLayout
        val decorView = activity?.window?.decorView
        decorView?.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        activity?.window?.statusBarColor = Color.TRANSPARENT

        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if(weather != null){
                showWeatherInfo(weather)
            }else{
                Toast.makeText(activity, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            binding.swipeRefresh.isRefreshing = false
        })
        binding.swipeRefresh.setColorSchemeColors(resources.getColor(R.color.black))
        refreshWeather()
        binding.swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }

        binding.now.navBtn.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })

    }

    fun refreshWeather(){
        binding.swipeRefresh.isRefreshing = true
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
    }

    private fun showWeatherInfo(weather: Weather){
        binding.now.placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily

        //填充now.xml的数据
        val currentTempText = "${realtime.temperature.toInt()}℃"
        binding.now.currentTemp.text = currentTempText
        binding.now.currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        binding.now.currentAQI.text = currentPM25Text
        binding.now.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        binding.weatherLayout.visibility = View.VISIBLE

        //填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        binding.lifeIndex.coldRiskText.text = lifeIndex.coldRisk[0].desc
        binding.lifeIndex.dressingText.text = lifeIndex.dressing[0].desc
        binding.lifeIndex.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        binding.lifeIndex.carWashingText.text = lifeIndex.carWashing[0].desc

        //填充forecast.xml的布局
        binding.forecast.forecastLayout.removeAllViews()
        val days = daily.skycon.size

        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(activity).inflate(
                R.layout.forecast_item,
                binding.forecast.forecastLayout, false
            )
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            binding.forecast.forecastLayout.addView(view)
        }

    }
}