package com.example.sunnyweather.ui.place

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sunnyweather.databinding.PlaceItemBinding
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.ui.master.MasterActivity
import com.example.sunnyweather.ui.weather.WeatherFragment

class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<Place>) :
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    inner class ViewHolder(binding: PlaceItemBinding) : RecyclerView.ViewHolder(binding.root){
        val placeName : TextView = binding.placeName
        val placeAddress : TextView = binding.placeAddress
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        holder.itemView.setOnClickListener {
            Log.d("Test", "测试")
            val position = holder.adapterPosition
            val place = placeList[position]
            val parentFragment = fragment.parentFragment
            if(parentFragment is WeatherFragment){
                parentFragment.drawerLayout.closeDrawers()
                parentFragment.viewModel.locationLng = place.location.lng
                parentFragment.viewModel.locationLat = place.location.lat
                parentFragment.viewModel.placeName = place.name
                parentFragment.refreshWeather()
            }else{
                val intent = Intent(parent.context, MasterActivity::class.java)
                    .apply {
                        putExtra("location_lng", place.location.lng)
                        putExtra("location_lat", place.location.lat)
                        putExtra("place_name", place.name)
                    }
                fragment.startActivity(intent)
                fragment.activity?.finish()
            }
            fragment.viewModel.savePlace(place)

        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address
    }

    override fun getItemCount() = placeList.size



}