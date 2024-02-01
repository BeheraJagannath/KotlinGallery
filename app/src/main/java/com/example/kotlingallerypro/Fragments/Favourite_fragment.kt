package com.example.kotlingallerypro.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.databinding.FragmentFavouriteFragmentBinding
import com.google.android.gms.ads.AdRequest


class Favourite_fragment : Fragment() {
    private  lateinit var favbinding: FragmentFavouriteFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val favbinding = DataBindingUtil.inflate<FragmentFavouriteFragmentBinding>(inflater,
            R.layout.fragment_favourite_fragment,container,false)

        val adRequest =  AdRequest.Builder().build()
        favbinding.favAdView.loadAd(adRequest)

        val pagerAdapter = ImagePagerAdapter(childFragmentManager)

        pagerAdapter.addFragment ( FavImageFragment(), "Photos")
        pagerAdapter.addFragment ( FavVideoFragment(), "Videos")

        favbinding.favViewpager.setAdapter(pagerAdapter)
        favbinding.tabLayout.setupWithViewPager(favbinding.favViewpager)


        return favbinding.root

    }

    private class ImagePagerAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm) {
        var fragmentList = ArrayList<Fragment>()
        var stringList = ArrayList<String>()
        fun addFragment(fragment: Fragment, title: String) {
            fragmentList.add(fragment)
            stringList.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return stringList[position]
        }
    }


}