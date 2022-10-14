package com.example.citikart.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.citikart.databinding.ActivityLoginBinding
import com.example.citikart.firebase.FirestoreClass
import com.example.citikart.models.User
import com.example.citikart.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity() {
    private var binding: ActivityLoginBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.btnDonthave?.setOnClickListener {
            startActivity(Intent(this@LoginActivity,RegistrationActivity::class.java))
            finish()
        }
        binding?.btnLogin?.setOnClickListener {
            loginUser()
        }
        binding?.btnForgotpass?.setOnClickListener {
            startActivity(Intent(this@LoginActivity,ForgotPasswordActivity::class.java))
            finish()
        }
    }

    private fun loginUser() {
        showProgressDialog("Please Wait ...")
        val email = binding?.etloginemail?.text.toString().trim{it<=' '}
        val password = binding?.etloginpassword?.text.toString().trim{it<=' '}
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful) {
                FirestoreClass().getCurrentUserDetails(this@LoginActivity)
            } else {
                hideProgressDialog()
                showErrorSnackBar(it.exception!!.message.toString(),true)
            }
        }
    }

    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()
        Log.i("First Name", user.firstName!!)
        Log.i("Last Name", user.lastName!!)
        Log.i("Email", user.email!!)
        val intent = Intent(this@LoginActivity,PrimeActivity::class.java)
        intent.putExtra(Constants.EXTRA_USER_DETAILS,user)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        binding=null
    }

}