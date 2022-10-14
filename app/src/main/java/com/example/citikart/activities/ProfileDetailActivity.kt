package com.example.citikart.activities

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.citikart.R
import com.example.citikart.databinding.ActivityProfileDetailBinding
import com.example.citikart.firebase.FirestoreClass
import com.example.citikart.models.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.IOException

class ProfileDetailActivity : BaseActivity() {

    private var binding: ActivityProfileDetailBinding? = null
    private var saveImageToInternalStorage: Uri? = null
    private var mProfileImageUrl: String = ""
    private lateinit var mUserDetail: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        FirestoreClass().getCurrentUserDetails(this)
        setUpActionBar()
        binding?.ivProfilefragment?.setOnClickListener {
            val pictureDialog = android.app.AlertDialog.Builder(this)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems =
                arrayOf("Select photo from gallery", "Capture photo from camera")
            pictureDialog.setItems(
                pictureDialogItems
            ) { _, which ->
                when (which) {
                    0 -> choosePhotoFromGallery()
                    1 -> takePhotoFromCamera()
                }
            }
            pictureDialog.show()
        }
        binding?.llProfilefragmentSavebtn?.setOnClickListener {
            if(saveImageToInternalStorage!=null) {
                uploadUserImage()
            } else {
                showProgressDialog("Please Wait ...")
                updateUserProfileData()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        @Suppress("DEPRECATION")
                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                        saveImageToInternalStorage = data.data
                        try {
                            Glide
                                .with(this@ProfileDetailActivity)
                                .load(saveImageToInternalStorage)
                                .centerCrop()
                                .placeholder(R.drawable.ic_profileplaceholder)
                                .into(binding?.ivProfilefragment!!)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
//                        binding?.hdodenprofile?.setImageBitmap(selectedImageBitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this@ProfileDetailActivity, "Failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if(requestCode == CAMERA) {
                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap
                saveImageToInternalStorage = data.data
                try {
                    Glide
                        .with(this@ProfileDetailActivity)
                        .load(saveImageToInternalStorage)
                        .centerCrop()
                        .placeholder(R.drawable.ic_profileplaceholder)
                        .into(binding?.ivProfilefragment!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
//                binding?.hdodenprofile?.setImageBitmap(thumbnail)
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Cancelled")
        }
    }

    private fun uploadUserImage() {
        showProgressDialog("Please Wait ...")
        if(saveImageToInternalStorage!=null) {
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child("USER_IMAGE"+System.currentTimeMillis()+"."+getFileExtension(saveImageToInternalStorage!!))
            sRef.putFile(saveImageToInternalStorage!!).addOnSuccessListener {
                    taskSnapshot ->
                Log.i("Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri->
                    Log.i("Downloadable Image URL", uri.toString())
                    mProfileImageUrl = uri.toString()
                    updateUserProfileData()
                }
            }.addOnFailureListener {
                    exception ->
                Toast.makeText(this@ProfileDetailActivity,exception.message.toString(),Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun choosePhotoFromGallery() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, GALLERY)
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                showRationalDialog("Permission Denied","It Looks Like You have Turned Off Permission for Storage.. Please Enable it From Settings to Continue..")
            }
        }).onSameThread().check()
    }

    private fun takePhotoFromCamera() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, CAMERA)
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                showRationalDialog("Permission Denied","It Looks Like You have Turned Off Permission for Storage.. Please Enable it From Settings to Continue..")
            }
        }).onSameThread().check()
    }

    private fun updateUserProfileData() {
        val userHashMap = HashMap<String,Any>()
        var anyChangesMade = false
        if(mProfileImageUrl.isNotEmpty() && mProfileImageUrl!=mUserDetail.image) {
            userHashMap["image"] = mProfileImageUrl
            anyChangesMade = true
        }
        if(binding?.etProfilefragmentFirstname?.text.toString()!= mUserDetail.firstName) {
            userHashMap["firstName"] = binding?.etProfilefragmentFirstname?.text.toString()
            anyChangesMade = true
        }
        if(binding?.etProfilefragmentLastname?.text.toString()!= mUserDetail.lastName) {
            userHashMap["lastName"] = binding?.etProfilefragmentLastname?.text.toString()
            anyChangesMade = true
        }
        if(binding?.etProfilefragmentPhonenumber?.text.toString()!= mUserDetail.mobile.toString()) {
            userHashMap["mobile"] = binding?.etProfilefragmentPhonenumber?.text.toString().toLong()
            anyChangesMade = true
        }
        if(binding?.actProfilefragmentAutoCompleteTextView?.text.toString()!= mUserDetail.gender) {
            userHashMap["gender"] = binding?.actProfilefragmentAutoCompleteTextView?.text.toString()
            anyChangesMade = true
        }
        if(anyChangesMade) {
            FirestoreClass().updateUserProfileData(this, userHashMap)
        }
    }

    fun profileUpdateSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setUpActionBar() {
        val gender = resources.getStringArray(R.array.gender_select)
        val arrayAdapter = ArrayAdapter(this, R.layout.drop_down_layout, gender)
        val autocompleteTV = findViewById<AutoCompleteTextView>(R.id.act_profilefragment_autoCompleteTextView)
        autocompleteTV.setAdapter(arrayAdapter)
        setSupportActionBar(binding?.toolbarProfilefragment)
        if(supportActionBar!=null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_backwhite)
        }
        binding?.toolbarProfilefragment?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun setUpUserDetails(user: User) {
        mUserDetail = user
        Glide
            .with(this@ProfileDetailActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_profileplaceholder)
            .into(binding?.ivProfilefragment!!)
        binding?.etProfilefragmentFirstname?.setText(user.firstName)
        binding?.etProfilefragmentLastname?.setText(user.lastName)
        binding?.etProfilefragmentEmail?.setText(user.email)
        if(user.gender!="") {
            binding?.actProfilefragmentAutoCompleteTextView?.setText(user.gender,false)
            binding?.actProfilefragmentAutoCompleteTextView?.setSelection(binding?.actProfilefragmentAutoCompleteTextView?.text!!.count())
        }
        if(user.mobile!=0L) {
            binding?.etProfilefragmentPhonenumber?.setText(user.mobile.toString())
        }
    }

    companion object{
        private const val GALLERY = 1
        private const val CAMERA = 2
    }

}