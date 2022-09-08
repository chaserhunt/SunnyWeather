package com.example.sunnyweather.ui.master

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.sunnyweather.R
import com.example.sunnyweather.ui.test.TestFragment
import com.example.sunnyweather.ui.weather.WeatherFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MasterActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
    private lateinit var navigationView: BottomNavigationView

    val viewModel by lazy { ViewModelProvider(this).get(MasterViewModel::class.java) }

    val viewPagerListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener{
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {}

        override fun onPageSelected(position: Int) {
            when(position){
                0 -> navigationView.selectedItemId = R.id.weather
                1 -> navigationView.selectedItemId = R.id.finding
                2 -> navigationView.selectedItemId = R.id.setting
            }
        }

        override fun onPageScrollStateChanged(state: Int) {

        }
    }

    val navigationViewListener: BottomNavigationView.OnNavigationItemSelectedListener = object : BottomNavigationView.OnNavigationItemSelectedListener{
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            viewPager.setCurrentItem(item.order)
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_master)

        if(viewModel.locationLng.isEmpty()){
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if(viewModel.locationLat.isEmpty()){
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if(viewModel.placeName.isEmpty()){
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }


        viewPager = findViewById(R.id.viewPager)
        navigationView = findViewById(R.id.nav_view)
        navigationView.itemIconTintList = null
        val weatherFragment = WeatherFragment()
        val findingFragment = TestFragment()
        val settingFragment = TestFragment()

        val fragmentPagerAdapter: FragmentPagerAdapter = object : FragmentPagerAdapter(supportFragmentManager){
            override fun getCount(): Int {
                return 3
            }

            override fun getItem(position: Int): Fragment {
                var fragment: Fragment = TestFragment()
                when(position){
                    0 -> fragment = weatherFragment
                    1 -> fragment = findingFragment
                    2 -> fragment = settingFragment
                }
                return fragment
            }
        }

        viewPager.addOnPageChangeListener(viewPagerListener)
        viewPager.adapter = fragmentPagerAdapter
        navigationView.setOnNavigationItemSelectedListener(navigationViewListener)

    }
}


