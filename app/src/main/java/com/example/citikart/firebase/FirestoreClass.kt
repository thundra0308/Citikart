package com.example.citikart.firebase

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.example.citikart.activities.*
import com.example.citikart.activities.ui.cart.CartFragment
import com.example.citikart.activities.ui.home.HomeFragment
import com.example.citikart.models.CartProductModel
import com.example.citikart.models.ProductModel
import com.example.citikart.models.User
import com.example.citikart.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.math.acos

class FirestoreClass: BaseActivity() {

    private var mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegistrationActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(userInfo.id!!)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error While Registering The User!!!",it)
            }
    }

    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if(currentUser!=null) {
            currentUserId=currentUser.uid
        }
        return currentUserId
    }

    fun getCurrentUserDetails(activity: Activity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,it.toString())
                val user = it.toObject(User::class.java)
                val sharedPrefrences = activity.getSharedPreferences(Constants.MY_CITIKART_PREF,Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPrefrences.edit()
                editor.putString(Constants.LOGGED_IN_USERNAME, user?.firstName)
                editor.putString(Constants.LOGGED_IN_USER_UID, user?.id)
                editor.apply()
                when(activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user!!)
                    }
                    is ProfileDetailActivity -> {
                        activity.setUpUserDetails(user!!)
                    }
                }
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String,Any>) {
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).update(userHashMap).addOnSuccessListener {
            Log.i(activity.javaClass.simpleName,"Profile Data Updated")
            Toast.makeText(activity,"Profile Updated Successfully!", Toast.LENGTH_SHORT).show()
            when(activity) {
                is ProfileDetailActivity -> {
                    activity.profileUpdateSuccess()
                }
            }
        }.addOnFailureListener {
                e->
            when(activity) {
                is ProfileDetailActivity -> {
                    activity.hideProgressDialog()
                }
            }
            Log.e(activity.javaClass.simpleName,"Error While Creating a Board",e)
            Toast.makeText(activity,"Error When Updating the Profile!!", Toast.LENGTH_SHORT).show()
        }
    }

    fun addProduct(activity: AddProductActivity, product: ProductModel) {
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).collection(Constants.PRODUCT)
            .document()
            .set(product, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(activity,"Product Added Successfully",Toast.LENGTH_SHORT).show()
                activity.productAddedSuccessfully()
            }
            .addOnFailureListener {
                Log.e(activity.javaClass.simpleName, "Error Writing Document")
            }
    }

    fun getProductDetails(activity: ProductDetailActivity, sellerId: String, productId: String) {
        Log.e("documentid1", productId+" "+sellerId)
        mFireStore.collection(Constants.USERS).document(sellerId).collection(Constants.PRODUCT).document(productId).get().addOnSuccessListener {
                document->
            Log.i(activity.javaClass.simpleName,document.toString())
            val product = document.toObject(ProductModel::class.java)
            product?.documentId = document.id
            Log.e("documentid2", document.id)
            activity.productDetails(product!!)
        }
    }

    fun addProductToCart(activity: Activity, cartProduct: CartProductModel) {
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).collection(Constants.CART_PRODUCT_ID).document(cartProduct.productId!!).set(cartProduct, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(activity,"Product Added to Cart Successfully",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.e(activity.javaClass.simpleName, "Error Writing Document")
            }
    }

    fun deleteCartProduct(documentId: String, fragment: CartFragment) {
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).collection(Constants.CART_PRODUCT_ID).document(documentId).delete()
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }

}