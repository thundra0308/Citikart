package com.example.citikart.activities

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.citikart.R
import com.example.citikart.activities.ui.account.AccountFragment
import com.example.citikart.activities.ui.cart.CartFragment
import com.example.citikart.activities.ui.home.HomeFragment
import com.example.citikart.activities.ui.setting.SettingFragment
import com.example.citikart.databinding.ActivityPrimeBinding

class PrimeActivity : BaseActivity() {

    private lateinit var binding: ActivityPrimeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPrimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        navView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.navigation_home -> {
                    setThatFragment(HomeFragment())
                }
                R.id.navigation_account->{
                    setThatFragment(AccountFragment())
                }
                R.id.navigation_cart->{
                    setThatFragment(CartFragment())
                }
                R.id.navigation_setting->{
                    setThatFragment(SettingFragment())
                }
            }
            true
        }

        val navController = findNavController(R.id.nav_host_fragment_activity_prime)
        navView.setupWithNavController(navController)
    }

    private fun setThatFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_activity_prime, fragment)
            commit()
        }
    }

}