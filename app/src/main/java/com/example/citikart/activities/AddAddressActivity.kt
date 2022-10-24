package com.example.citikart.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.example.citikart.R
import com.example.citikart.databinding.ActivityAddAddressBinding
import com.example.citikart.models.AddressModel
import com.example.citikart.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class AddAddressActivity : AppCompatActivity() {

    private var binding: ActivityAddAddressBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val state = resources.getStringArray(R.array.state_select)
        val arrayAdapter0 = ArrayAdapter(this, R.layout.drop_down_layout, state)
        val autocompleteTV0 = findViewById<AutoCompleteTextView>(R.id.act_addaddress_state)
        autocompleteTV0.setAdapter(arrayAdapter0)
        val addressType = resources.getStringArray(R.array.address_type_select)
        val arrayAdapter1 = ArrayAdapter(this, R.layout.drop_down_layout, addressType)
        val autocompleteTV1 = findViewById<AutoCompleteTextView>(R.id.act_addaddress_addresstype)
        autocompleteTV1.setAdapter(arrayAdapter1)
        val sharedPref = this.getSharedPreferences(Constants.MY_CITIKART_PREF, Context.MODE_PRIVATE)
        val uid = sharedPref?.getString(Constants.LOGGED_IN_USER_UID,"")
        binding?.cvAddaddressAddbtn?.setOnClickListener {
            val address = AddressModel(
                fullName = binding?.etAddaddressFullname?.text?.toString(),
                mobileNo = binding?.etAddaddressMobilenumber?.text?.toString(),
                areaPin = binding?.etAddaddressPincode?.text?.toString(),
                flatHouse = binding?.etAddaddressFlat?.text?.toString(),
                areaStreet = binding?.etAddaddressArea?.text?.toString(),
                landmark = binding?.etAddaddressLandmark?.text?.toString(),
                townCity = binding?.etAddaddressTown?.text?.toString(),
                state = binding?.actAddaddressState?.text?.toString(),
                addressType = binding?.actAddaddressAddresstype?.text?.toString()
            )
            FirebaseFirestore.getInstance().collection(Constants.USERS).document(uid!!).collection(Constants.ADDRESS).document().set(address,SetOptions.merge()).addOnSuccessListener {
                Toast.makeText(this@AddAddressActivity,"Address Added Successfully",Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}