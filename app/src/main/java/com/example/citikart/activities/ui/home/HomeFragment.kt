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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.citikart.activities.ProductDetailActivity
import com.example.citikart.activities.ProfileDetailActivity
import com.example.citikart.adapters.HomeFragmentAdapter
import com.example.citikart.databinding.FragmentHomeBinding
import com.example.citikart.models.ProductModel
import com.example.citikart.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore

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
                    val intent = Intent(activity,ProductDetailActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,model.documentId)
                    startActivity(intent)
                }
            })
            val rv = binding.rvHomefragment
            rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            rv.setHasFixedSize(true)
            rv.adapter = adapter
    }

    private fun getProductList() {
        FirebaseFirestore.getInstance().collection(Constants.PRODUCT).get().addOnSuccessListener {
                document->
            Log.i(activity?.javaClass?.simpleName, document.documents.toString())
            val productList: ArrayList<ProductModel> = ArrayList()
            for(i in document.documents) {
                val product = i.toObject(ProductModel::class.java)!!
                product.documentId = i.id
                val hashmap = HashMap<String,Any>()
                hashmap["documentId"] = i.id
                FirebaseFirestore.getInstance().collection(Constants.PRODUCT).document(i.id).update(hashmap)
                productList.add(product)
            }
            setUpRecyclerView(productList)
        }.addOnFailureListener {
            Log.e(activity?.javaClass?.simpleName,"Error While Creating a Board",it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}