package com.nachc.dba.routelistscreen

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.nachc.dba.R
import com.nachc.dba.databinding.RouteListScreenFragmentBinding

class RouteListScreenFragment : Fragment() {

    companion object {
        fun newInstance() = RouteListScreenFragment()
    }

    val TAG: String = "RouteListScreenFragment"
    private lateinit var viewModel: RouteListScreenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: RouteListScreenFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.route_list_screen_fragment, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RouteListScreenViewModel::class.java)
        // TODO: Use the ViewModel
    }

}