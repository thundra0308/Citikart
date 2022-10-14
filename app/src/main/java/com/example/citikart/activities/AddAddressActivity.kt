package com.example.citikart.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.example.citikart.R

class AddAddressActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)
        val state = resources.getStringArray(R.array.state_select)
        val arrayAdapter0 = ArrayAdapter(this, R.layout.drop_down_layout, state)
        val autocompleteTV0 = findViewById<AutoCompleteTextView>(R.id.act_addaddress_state)
        autocompleteTV0.setAdapter(arrayAdapter0)
        val addressType = resources.getStringArray(R.array.address_type_select)
        val arrayAdapter1 = ArrayAdapter(this, R.layout.drop_down_layout, addressType)
        val autocompleteTV1 = findViewById<AutoCompleteTextView>(R.id.act_addaddress_addresstype)
        autocompleteTV1.setAdapter(arrayAdapter1)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}