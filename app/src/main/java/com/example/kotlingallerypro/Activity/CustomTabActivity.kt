package com.example.kotlingallerypro.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.example.kotlingallerypro.R
import com.google.android.material.tabs.TabLayout

class CustomTabActivity : AppCompatActivity() {
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    lateinit var adapterr: MyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_tab)
        tabLayout = findViewById(R.id.tablayout)
        viewPager = findViewById(R.id.view_Pager)



    }





}