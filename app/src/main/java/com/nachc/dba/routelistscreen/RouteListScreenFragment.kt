package com.nachc.dba.routelistscreen

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nachc.dba.R
import com.nachc.dba.databinding.RouteListScreenFragmentBinding
import com.nachc.dba.models.Trip

class RouteListScreenFragment : Fragment() {

    val TAG: String = "RouteListScreenFragment"
    private val viewModel: RouteListScreenViewModel by viewModels()
    private val listAdapter = RouteListAdapter(arrayListOf())
    private lateinit var binding: RouteListScreenFragmentBinding

    private val tripsDataObserver = Observer<List<Trip>> { list ->
        list?.let {
            Log.i(TAG, list.size.toString())
            binding.routeList.visibility = View.VISIBLE
            listAdapter.updateTripList(it)
        }
    }

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
        viewModel.trips.observe(viewLifecycleOwner, tripsDataObserver)
        viewModel.getTrips()

        binding.routeList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }
    }

}