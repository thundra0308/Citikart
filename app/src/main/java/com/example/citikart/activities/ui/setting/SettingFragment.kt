package com.example.citikart.activities.ui.setting

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.citikart.R
import com.example.citikart.activities.AddAddressActivity
import com.example.citikart.activities.AddProductActivity
import com.example.citikart.activities.LoginActivity
import com.example.citikart.activities.PrimeActivity
import com.example.citikart.databinding.FragmentSettingBinding
import com.google.firebase.auth.FirebaseAuth


class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingViewModel =
            ViewModelProvider(this).get(SettingViewModel::class.java)

        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnSettingfragmentLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            activity?.finish()
            PrimeActivity().finish()
        }

        binding.cvSettingfragmentAddyourproduct.setOnClickListener {
            val intent = Intent(activity,AddProductActivity::class.java)
            startActivity(intent)
        }
        binding.cvSettingfragmentAddaddress.setOnClickListener {
            val intent = Intent(activity,AddAddressActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}