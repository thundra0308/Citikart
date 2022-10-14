package com.example.citikart.adapters

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.citikart.R
import com.example.citikart.activities.PrimeActivity
import com.example.citikart.activities.ui.cart.CartFragment
import com.example.citikart.activities.ui.home.HomeFragment
import com.example.citikart.firebase.FirestoreClass
import com.example.citikart.models.ProductModel
import com.example.citikart.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore

class CartProductItemAdapter(private val context: Context, private val productList :ArrayList<ProductModel>): RecyclerView.Adapter<CartProductItemAdapter.MainViewHolder>() {
    private lateinit var mListener: onItemClickListener
    private lateinit var uListener: onUpdateListener
    inner class MainViewHolder(private val itemView: View, private val listener: onItemClickListener, private val ulistener: onUpdateListener): RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition,productList[adapterPosition])
            }
            ulistener.onUpdate()
        }
        fun bindData(product: ProductModel) {
            val imageview = itemView.findViewById<ImageView>(R.id.iv_cartproduct_image)
            val name = itemView.findViewById<TextView>(R.id.tv_cartproduct_name)
            val desc = itemView.findViewById<TextView>(R.id.tv_cartproduct_desc)
            val price = itemView.findViewById<TextView>(R.id.tv_cartproduct_price)
            val deleteBtn = itemView.findViewById<CardView>(R.id.cv_cartproduct_delete)
            deleteBtn.setOnClickListener {
                Log.e("Id","${product.documentId}")
                FirestoreClass().deleteCartProduct(product.documentId!!,CartFragment())
                productList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
            name.text = product.name
            desc.text = product.description
            val p: Long = product.price
            price.text = p.toString()
            Glide
                .with(context)
                .load(product.images?.get(0))
                .centerCrop()
                .into(imageview)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_cart_products,parent,false)
        return MainViewHolder(view,mListener,uListener)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindData(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun setOnClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    fun onUpdateListener(listener: onUpdateListener) {
        uListener = listener
    }

    interface onItemClickListener{
        fun onItemClick(position: Int, model: ProductModel)
    }

    interface onUpdateListener{
        fun onUpdate()
    }

}