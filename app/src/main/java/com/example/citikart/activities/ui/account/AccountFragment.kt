package com.example.citikart.activities.ui.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.citikart.activities.LoginActivity
import com.example.citikart.activities.PrimeActivity
import com.example.citikart.activities.ProfileDetailActivity
import com.example.citikart.activities.YourWishListActivity
import com.example.citikart.databinding.FragmentAccountBinding
import com.example.citikart.models.User
import com.example.citikart.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val accountViewModel = ViewModelProvider(this).get(AccountViewModel::class.java)

        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.cvAccountfragmentYouraccount.setOnClickListener {
            startActivity(Intent(activity,ProfileDetailActivity::class.java))
        }
        binding.tvAccountYourwishlist.setOnClickListener {
            startActivity(Intent(activity,YourWishListActivity::class.java))
        }
        val sharedPref = activity?.getSharedPreferences(Constants.MY_CITIKART_PREF,Context.MODE_PRIVATE)
        val username = sharedPref?.getString(Constants.LOGGED_IN_USERNAME,"")
        val uid = sharedPref?.getString(Constants.LOGGED_IN_USER_UID,"")
        binding.tvAccountfragmentUsername.text = username
        getUserDetails(uid!!)
        return root
    }

    private fun getUserDetails(uid: String) {
        FirebaseFirestore.getInstance().collection(Constants.USERS)
            .document(uid)
            .get()
            .addOnSuccessListener {
                Log.i("Success","Success")
                val user = it.toObject(User::class.java)
                Glide
                    .with(requireActivity())
                    .load(user?.image)
                    .centerCrop()
                    .placeholder(com.example.citikart.R.drawable.ic_profileplaceholder)
                    .into(binding.accountProfileimagetop)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}