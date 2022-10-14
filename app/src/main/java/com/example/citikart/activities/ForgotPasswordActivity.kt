package com.example.citikart.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.citikart.R
import com.example.citikart.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : BaseActivity() {
    private var binding: ActivityForgotPasswordBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.btnForgotpasssubmit?.setOnClickListener {
            val email = binding?.etforgot?.text.toString().trim{it<=' '}
            if(email.isEmpty()) {
                //TODO
            } else {
                showProgressDialog("Please Wait...")
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
                    hideProgressDialog()
                    if(it.isSuccessful) {
                        Toast.makeText(this@ForgotPasswordActivity,"Email Sent!!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@ForgotPasswordActivity,LoginActivity::class.java))
                        finish()
                    } else {
                        showErrorSnackBar(it.exception!!.message.toString(),true)
                    }
                }
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@ForgotPasswordActivity,LoginActivity::class.java))
        finish()
    }

}