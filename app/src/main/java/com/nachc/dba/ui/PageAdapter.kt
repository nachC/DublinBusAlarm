package com.nachc.dba.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nachc.dba.twitternews.TwitterNewsFragment
import com.nachc.dba.searchscreen.SearchScreenFragment

class PageAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {


    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return SearchScreenFragment()
            1 -> return TwitterNewsFragment()
            2 -> return SettingsFragment()
        }
        return null!!
    }

}