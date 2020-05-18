package com.ivanovme.blackhawk.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import com.ivanovme.blackhawk.R
import com.ivanovme.blackhawk.ui.search.data.CityData
import kotlinx.android.synthetic.main.item_search_result.view.*


class SearchAdapter(
    private val onItemSelected: (CityData) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private val diffCallback: ItemCallback<CityData> = object : ItemCallback<CityData>() {
        override fun areItemsTheSame(oldItem: CityData, newItem: CityData): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: CityData, newItem: CityData): Boolean =
            oldItem == newItem
    }

    private val asyncDiffer: AsyncListDiffer<CityData> = AsyncListDiffer(this, diffCallback)

    fun updateData(cities: List<CityData>) {
        asyncDiffer.submitList(cities)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return SearchViewHolder(view)
    }

    override fun getItemCount(): Int = asyncDiffer.currentList.size

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(asyncDiffer.currentList[position])
    }

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cityNameView = itemView.cityName
        private val cityCountryView = itemView.cityCountry
        private val airportCodeView = itemView.airportCode
        private val clickHandler = itemView.clickHandler

        fun bind(data: CityData) {
            cityNameView.text = data.city
            cityCountryView.text = data.country
            airportCodeView.text = data.airportCode

            clickHandler.setOnClickListener {
                onItemSelected(data)
            }
        }

    }
}