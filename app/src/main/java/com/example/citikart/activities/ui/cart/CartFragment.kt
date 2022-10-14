package com.example.citikart.activities.ui.cart

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.citikart.R
import com.example.citikart.activities.ProductDetailActivity
import com.example.citikart.adapters.CartProductItemAdapter
import com.example.citikart.adapters.HomeFragmentAdapter
import com.example.citikart.databinding.FragmentCartBinding
import com.example.citikart.models.CartProductModel
import com.example.citikart.models.ProductModel
import com.example.citikart.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlin.concurrent.fixedRateTimer

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var adapter: CartProductItemAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val cartViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val root: View = binding.root

        getAllCartProductId()

        return root

    }

    fun getAllCartProducts(list: ArrayList<String>) {
        FirebaseFirestore.getInstance().collection(Constants.PRODUCT).whereIn(FieldPath.documentId(),list).get()
            .addOnSuccessListener {
                val products = ArrayList<ProductModel>()
                var totalPrice: Long = 0.toLong()
                for(i in it.documents) {
                    val product = i.toObject(ProductModel::class.java)
                    totalPrice += product?.price!!
                    products.add(product)
                }
                binding.tvCartfragmentTotalPrice.text=totalPrice.toString()
                Log.e("Size 1", "${products.size}")
                if(products.size>0) {
                    setUpCartRecyclerView(products)
                }
            }
            .addOnFailureListener {
                //TODO
            }
    }

    fun getAllCartProductId() {
        FirebaseFirestore.getInstance().collection(Constants.USERS).document(FirebaseAuth.getInstance().currentUser?.uid!!).collection(Constants.CART_PRODUCT_ID).get().addOnSuccessListener {
                document->
            Log.e("ids", document.documents.toString())
            val cartProductIds = ArrayList<String>()
            for(i in document.documents) {
                val product = i.toObject(CartProductModel::class.java)!!
                cartProductIds.add(product.productId.toString())
            }
            Log.e("Size 2","${cartProductIds.size}")
            if(cartProductIds.size>0) {
                getAllCartProducts(cartProductIds)
            }
        }.addOnFailureListener {
            Log.e(activity?.javaClass?.simpleName,"Error While Creating a Board",it)
        }
    }

    fun setUpCartRecyclerView(cartProducts: ArrayList<ProductModel>) {
        adapter = CartProductItemAdapter(requireContext(),cartProducts)
        adapter?.setOnClickListener(object : CartProductItemAdapter.onItemClickListener{
            override fun onItemClick(position: Int, model: ProductModel) {

            }
        })
        adapter?.onUpdateListener(object : CartProductItemAdapter.onUpdateListener{
            override fun onUpdate() {
                fragmentManager?.beginTransaction()?.detach(CartFragment())?.attach(CartFragment())?.commit()
            }
        })
        val rv = binding.rvCartfragmentCartitems
        rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv.setHasFixedSize(true)
        rv.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}