package com.example.kotlingallerypro.Activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kotlingallerypro.Fragments.Gallery_fragment

class MyAdapter( reagmentmaanager: FragmentManager,lifecycle:Lifecycle) :
    FragmentStateAdapter(reagmentmaanager!!,lifecycle) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> Gallery_fragment()
            1 -> Gallery_fragment()
            else -> Gallery_fragment()
        }

    }

}