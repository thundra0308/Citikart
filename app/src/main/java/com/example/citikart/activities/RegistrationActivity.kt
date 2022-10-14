package com.example.citikart.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.citikart.R
import com.example.citikart.databinding.ActivityLoginBinding
import com.example.citikart.databinding.ActivityRegistrationBinding
import com.example.citikart.firebase.FirestoreClass
import com.example.citikart.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegistrationActivity : BaseActivity() {
    private var binding: ActivityRegistrationBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.btnAlready?.setOnClickListener {
            startActivity(Intent(this@RegistrationActivity,LoginActivity::class.java))
            finish()
        }
        binding?.btnRegister?.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        showProgressDialog("Please Wait ...")
        val email: String = binding?.etregemail?.text.toString().trim{it<=' '}
        val password: String = binding?.etregpass?.text.toString().trim{it<=' '}
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(
            OnCompleteListener<AuthResult> {
                if(it.isSuccessful) {
                    val firebaseUser: FirebaseUser = it.result!!.user!!
                    val user = User(
                        firebaseUser.uid,
                        binding?.etregfirstname?.text.toString().trim{it<=' '},
                        binding?.etreglastname?.text.toString().trim{it<=' '},
                        binding?.etregemail?.text.toString().trim{it<=' '}
                    )
                    FirestoreClass().registerUser(this,user)
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@RegistrationActivity,LoginActivity::class.java))
                    finish()
                } else {
                    hideProgressDialog()
                    showErrorSnackBar(it.exception!!.message.toString(),true)
                }
            })
    }

    fun userRegistrationSuccess() {
        hideProgressDialog()
        Toast.makeText(this@RegistrationActivity,"Registration Successful",Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        binding = null
        startActivity(Intent(this@RegistrationActivity,LoginActivity::class.java))
        finish()
    }

}