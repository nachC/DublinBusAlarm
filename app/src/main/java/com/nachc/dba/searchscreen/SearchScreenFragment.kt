package com.nachc.dba.searchscreen

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.nachc.dba.R
import com.nachc.dba.databinding.SearchScreenFragmentBinding

class SearchScreenFragment : Fragment() {

    companion object {
        fun newInstance() = SearchScreenFragment()
    }

    val TAG: String = "SearchScreenFragment"
    private lateinit var viewModel: SearchScreenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: SearchScreenFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.search_screen_fragment, container, false)

        binding.searchBtn.setOnClickListener {
            Log.d(TAG, "button clicked")
            findNavController().navigate(SearchScreenFragmentDirections.actionSearchScreenFragmentToRouteListScreenFragment())
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchScreenViewModel::class.java)
        // TODO: Use the ViewModel
    }

}