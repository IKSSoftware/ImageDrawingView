package com.kienht.imagedrawing.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kienht.imagedrawing.R

class ShapeData(val imageRes: Int,
                val name:String)

interface ItemClickListener {
    fun onItemClick(position: Int)
}

class ShapeAdapter(private val dataSet: List<ShapeData>, private val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<ShapeAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.name)
        val imageView: ImageView = view.findViewById(R.id.imgView)
        val relativeLayout: RelativeLayout = view.findViewById(R.id.layout)
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.shape_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = dataSet[position].name
        viewHolder.imageView.setImageResource(dataSet[position].imageRes)

        viewHolder.relativeLayout.setOnClickListener {
            itemClickListener.onItemClick(position = position)
        }
    }

    override fun getItemCount() = dataSet.size

}