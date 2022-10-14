package com.example.citikart.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.citikart.R

open class ProductDetailViewpagerAdapter(private val context: Context, private val images: ArrayList<String>, private val viewPager2: ViewPager2): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.product_detail_viewpager_item, parent, false))
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val image = images[position]
        if(holder is MyViewHolder) {
            val imageview = holder.itemView.findViewById<ImageView>(R.id.productdetail_viewpager_images)
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .into(imageview)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }


    private class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

}