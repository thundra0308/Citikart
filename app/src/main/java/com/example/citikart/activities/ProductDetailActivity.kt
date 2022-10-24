package com.example.citikart.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.example.citikart.R
import com.example.citikart.adapters.ProductDetailViewpagerAdapter
import com.example.citikart.databinding.ActivityProductDetailBinding
import com.example.citikart.firebase.FirestoreClass
import com.example.citikart.models.CartProductModel
import com.example.citikart.models.ProductModel
import com.example.citikart.utils.Constants
import java.util.ArrayList

class ProductDetailActivity : AppCompatActivity() {

    private var binding: ActivityProductDetailBinding? = null
    private var handler: Handler = Handler(Looper.myLooper()!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val productId = intent.getStringExtra(Constants.PRODUCT_ID)
        val sellerId = intent.getStringExtra(Constants.SELLER_ID)
        Log.e("pda","$sellerId $productId")
        val viewPager2 = findViewById<ViewPager2>(R.id.vp_productdetail_banner)
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

            }
        })
        FirestoreClass().getProductDetails(this@ProductDetailActivity,sellerId!!, productId!!)
        binding?.cvProductdetailAddtocart?.setOnClickListener {
            addProductToCart(productId, sellerId)
        }
    }

    fun productDetails(product: ProductModel) {
        setUpProductImages(product.images!!)
        binding?.tvProductdetailName?.text = product.name
        binding?.tvProductdetailDescription?.text = product.description
        binding?.tvProductdetailPrice?.text = "${product.price} Rs"
        binding?.tvProductdetailAlldetails?.text = product.details
    }

    private fun setUpProductImages(imagelist: ArrayList<String>) {
        if (imagelist.isNotEmpty()) {
            val viewPager2 = findViewById<ViewPager2>(R.id.vp_productdetail_banner)
            val adapter = ProductDetailViewpagerAdapter(this@ProductDetailActivity,imagelist,viewPager2)
            binding?.vpProductdetailBanner?.adapter = adapter
            binding?.wiProductdetailBanner?.attachTo(viewPager2)
        }
    }

    private fun addProductToCart(productId: String, sellerId: String) {
        val cartProduct = CartProductModel(sellerId = sellerId, productId = productId)
        FirestoreClass().addProductToCart(this,cartProduct)
    }

}