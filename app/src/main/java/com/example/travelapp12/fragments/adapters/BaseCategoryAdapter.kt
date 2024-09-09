package com.example.travelapp12.fragments.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp12.R
import com.example.travelapp12.fragments.data.Sights
import com.squareup.picasso.Picasso
import android.widget.Filter
import android.widget.Filter.FilterResults
import java.util.Locale


class BaseCategoryAdapter(
    private var sightsList: ArrayList<Sights>,
    private val activityTypesMap: HashMap<String, String>
) : RecyclerView.Adapter<BaseCategoryAdapter.MyViewHolder>() {

    var onClick: ((Sights) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_base_category, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return sightsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = sightsList[position]

        holder.name.text = currentitem.name
        holder.address.text = currentitem.address

        val activityType = activityTypesMap[currentitem.id_activtype]
        holder.acttype.text = activityType

        Picasso.get().load(currentitem.image).into(holder.imageView)

        holder.itemView.setOnClickListener {
            onClick?.invoke(currentitem)
        }
    }

    fun updateData(newList: ArrayList<Sights>) {
        sightsList = newList
        notifyDataSetChanged()
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvname)
        val acttype: TextView = itemView.findViewById(R.id.tvacttype)
        val address: TextView = itemView.findViewById(R.id.tvaddress)
        val imageView: ImageView = itemView.findViewById(R.id.image)
    }
}


