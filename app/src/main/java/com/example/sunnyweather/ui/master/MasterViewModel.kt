package com.example.sunnyweather.ui.master

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.sunnyweather.logic.Repository
import com.example.sunnyweather.logic.model.Location

class MasterViewModel: ViewModel() {

    private val locationLiveData = MutableLiveData<Location>()

    var locationLng = ""

    var locationLat = ""

    var placeName = ""

    val weatherLiveData = Transformations.switchMap(locationLiveData){ location ->
        Repository.refreshWeather(location.lng, location.lat)
    }

    fun refreshWeather(lng: String, lat: String){
        locationLiveData.value = com.example.sunnyweather.logic.model.Location(lng, lat)
    }

}