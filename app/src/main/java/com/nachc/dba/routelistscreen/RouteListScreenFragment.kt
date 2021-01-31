package com.nachc.dba.routelistscreen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nachc.dba.R
import com.nachc.dba.databinding.RouteListScreenFragmentBinding
import com.nachc.dba.models.Trip

class RouteListScreenFragment : Fragment() {

    val TAG: String = "RouteListScreenFragment"

    private val viewModel: RouteListScreenViewModel by viewModels()

    private val listAdapter = RouteListAdapter(arrayListOf())

    var trips: List<Trip>? = null
    private lateinit var binding: RouteListScreenFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.route_list_screen_fragment, container, false)
        binding.routeListViewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // retrieve argument passed to fragment by navigation
        // asign it to local variable and update the recyclerview list
        arguments?.let {
            trips = RouteListScreenFragmentArgs.fromBundle(it).trips.toList()
            Log.i(TAG, "update trip list. Size: " + trips?.size)
            listAdapter.updateTripList(trips!!)
        }

        binding.routeList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }
    }

}