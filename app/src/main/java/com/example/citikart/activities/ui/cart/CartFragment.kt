package com.example.citikart.activities.ui.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.citikart.activities.ProductDetailActivity
import com.example.citikart.adapters.CartProductItemAdapter
import com.example.citikart.databinding.FragmentCartBinding
import com.example.citikart.models.CartProductModel
import com.example.citikart.models.ProductModel
import com.example.citikart.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore

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

    private fun getAllCartProducts(list: ArrayList<Pair<String,String>>) {
        val products: ArrayList<ProductModel> = ArrayList()
        for(pair in list) {
            FirebaseFirestore.getInstance().collection(Constants.USERS).document(pair.first).collection(Constants.PRODUCT).document(pair.second).get().addOnSuccessListener {
                val p = it.toObject(ProductModel::class.java)
                products.add(p!!)
                setUpCartRecyclerView(products)
            }
        }
        Log.e("cf2","$products")
    }

    private fun getAllCartProductId() {
        val sharedPref = activity?.getSharedPreferences(Constants.MY_CITIKART_PREF, Context.MODE_PRIVATE)
        val uid = sharedPref?.getString(Constants.LOGGED_IN_USER_UID,"")
        val cartList: ArrayList<Pair<String,String>> = ArrayList()
        FirebaseFirestore.getInstance().collection(Constants.USERS).document(uid!!).collection(Constants.CART_PRODUCT_ID).get().addOnSuccessListener { products->
            for(product in products) {
                val p = product.toObject(CartProductModel::class.java)
                val x = Pair(p.sellerId!!,p.productId!!)
                cartList.add(x)
            }
            Log.e("cf", "$cartList")
            getAllCartProducts(cartList)
        }
    }

    private fun setUpCartRecyclerView(cartProducts: ArrayList<ProductModel>) {
        adapter = CartProductItemAdapter(requireContext(),cartProducts)
        adapter?.setOnClickListener(object : CartProductItemAdapter.onItemClickListener{
            override fun onItemClick(position: Int, model: ProductModel) {
                val intent = Intent(activity,ProductDetailActivity::class.java)
                intent.putExtra(Constants.SELLER_ID,model.sellerId)
                intent.putExtra(Constants.PRODUCT_ID,model.documentId)
                startActivity(intent)
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