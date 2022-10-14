package com.example.citikart.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.citikart.R
import com.example.citikart.models.ProductModel
import org.w3c.dom.Text

class HomeFragmentAdapter(private val context: Context, private val productList :ArrayList<ProductModel>): RecyclerView.Adapter<HomeFragmentAdapter.MainViewHolder>() {
    private lateinit var mListener: onItemClickListener
    inner class MainViewHolder(private val itemView: View, private val listener: onItemClickListener): RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition,productList[adapterPosition])
            }
        }
        fun bindData(product: ProductModel) {
            val productImage = itemView.findViewById<ImageView>(R.id.iv_homefragment_productimage)
            val productName = itemView.findViewById<TextView>(R.id.tv_homefragment_productname)
            val price = itemView.findViewById<TextView>(R.id.tv_homefragment_productprice)
            val desc = itemView.findViewById<TextView>(R.id.tv_homefragment_productdesc)
            productName.text = product.name
            val p: Long = product.price
            price.text = p.toString()
            desc.text = product.description
            Glide
                .with(context)
                .load(product.images?.get(0))
                .centerCrop()
                .into(productImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_home_item,parent,false)
        val layoutParms = LinearLayout.LayoutParams((parent.width*0.5).toInt(),(700.toDp()).toPx())
        layoutParms.setMargins((15.toDp()).toPx(),(100.toDp()),(15.toDp()).toPx(),0)
        view.layoutParams = layoutParms
        return MainViewHolder(view,mListener)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindData(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    private fun Int.toDp(): Int = (this/ Resources.getSystem().displayMetrics.density).toInt()
    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    fun setOnClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    interface onItemClickListener{
        fun onItemClick(position: Int, model: ProductModel)
    }

}