package com.coolcats.restaurantfinder.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coolcats.restaurantfinder.R
import com.coolcats.restaurantfinder.model.Result
import kotlinx.android.synthetic.main.location_list_item.view.*

class LocationAdapter:RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    private var list:List<Result> = listOf()

    inner class LocationViewHolder(view: View):RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(
            R.layout.location_list_item,
            parent,
            false)

        return LocationViewHolder(item)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {

        list[position].let {
            holder.itemView.apply {
                name_textview.text = it.name
                location_textview.text = it.vicinity
                rating_textview.text = "Rating:" + it.rating.toString()
                if (it.opening_hours.open_now)
                    hours_textview.text = "Open Now"
                else
                    hours_textview.text = "Closed Now"

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(list: List<Result>){
        this.list = list
        notifyDataSetChanged()
    }


}