package com.nachc.dba.routelistscreen

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.nachc.dba.R
import com.nachc.dba.databinding.TripItemBinding
import com.nachc.dba.models.Trip

class RouteListAdapter(private val tripList: ArrayList<Trip>):
    RecyclerView.Adapter<RouteListAdapter.RouteListViewHolder>(), RouteClickListener {

    fun updateTripList(newTripList: List<Trip>) {
        tripList.clear()
        tripList.addAll(newTripList)
        notifyDataSetChanged()
    }

    class RouteListViewHolder(var view: TripItemBinding): RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<TripItemBinding>(inflater, R.layout.trip_item, parent, false)
        return RouteListViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteListViewHolder, position: Int) {
        holder.view.trip = tripList[position]
        holder.view.listener = this
        holder.view.tripLayout.tag = tripList[position].id
    }

    override fun getItemCount() = tripList.size

    override fun onClick(v: View) {
        for (trip in tripList) {
            if (v.tag == trip.id) {
                Log.i("RouteListAdapter", trip.id)
            }
        }
    }
}