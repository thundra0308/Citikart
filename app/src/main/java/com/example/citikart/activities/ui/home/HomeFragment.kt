package com.example.citikart.activities.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.citikart.activities.ProductDetailActivity
import com.example.citikart.activities.ProfileDetailActivity
import com.example.citikart.adapters.HomeFragmentAdapter
import com.example.citikart.databinding.FragmentHomeBinding
import com.example.citikart.models.ProductModel
import com.example.citikart.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        getProductList()
        return root
    }

    private fun setUpRecyclerView(products: ArrayList<ProductModel>?) {
            val adapter = HomeFragmentAdapter(requireContext(),products!!)
            adapter.setOnClickListener(object : HomeFragmentAdapter.onItemClickListener{
                override fun onItemClick(position: Int, model: ProductModel) {
                    Log.e("hf", "${model.sellerId} ${model.documentId}")
                    val intent = Intent(activity,ProductDetailActivity::class.java)
                    intent.putExtra(Constants.SELLER_ID,model.sellerId)
                    intent.putExtra(Constants.PRODUCT_ID,model.documentId)
                    startActivity(intent)
                }
            })
            val rv = binding.rvHomefragment
            rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            rv.setHasFixedSize(true)
            rv.adapter = adapter
    }

    private fun getProductList() {
        val productList: ArrayList<ProductModel> = ArrayList()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                FirebaseFirestore.getInstance().collection(Constants.USERS).get()
                    .addOnSuccessListener { users ->
                        Log.i(activity?.javaClass?.simpleName, users.documents.toString())
                        for (i in users.documents) {
                            FirebaseFirestore.getInstance().collection(Constants.USERS)
                                .document(i.id)
                                .collection(Constants.PRODUCT).get()
                                .addOnSuccessListener { products ->
                                    for (j in products.documents) {
                                        val p = j.toObject(ProductModel::class.java)
                                        productList.add(p!!)
                                    }
                                    setUpRecyclerView(productList)
                                }
                        }
                    }.addOnFailureListener {
                        Log.e(activity?.javaClass?.simpleName, "Error While Creating a Board", it)
                    }
            }
        }
        Log.e("Products", "$productList")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}