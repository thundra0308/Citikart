package com.example.citikart.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.citikart.R
import com.example.citikart.databinding.ActivityAddProductBinding
import com.example.citikart.firebase.FirestoreClass
import com.example.citikart.models.ProductModel
import com.example.citikart.models.User
import com.example.citikart.utils.Constants
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.IOException

class AddProductActivity : BaseActivity() {

    private var binding: ActivityAddProductBinding? = null
    private var saveImageToInternalStorage: Uri? = null
    private var mProductImageUrl: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val sharedPref = this.getSharedPreferences(Constants.MY_CITIKART_PREF, Context.MODE_PRIVATE)
        val uid = sharedPref?.getString(Constants.LOGGED_IN_USER_UID,"")

        binding?.cvAddproductactivityImg1?.setOnClickListener {
            choosePhotoFromGallery()
        }
        binding?.cvAddproductactivityAddproductbtn?.setOnClickListener {
            if(saveImageToInternalStorage!=null) {
                uploadProductImage(uid!!)
            } else {
                showProgressDialog("Please Wait ...")
                addProduct(uid!!)
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
                                .with(this@AddProductActivity)
                                .load(saveImageToInternalStorage)
                                .centerCrop()
                                .placeholder(R.drawable.ic_profileplaceholder)
                                .into(binding?.ivAddproductactivityImg1!!)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this@AddProductActivity, "Failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if(requestCode == CAMERA) {
                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap
                saveImageToInternalStorage = data.data
                try {
                    Glide
                        .with(this@AddProductActivity)
                        .load(saveImageToInternalStorage)
                        .centerCrop()
                        .placeholder(R.drawable.ic_profileplaceholder)
                        .into(binding?.ivAddproductactivityImg1!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Cancelled")
        }
    }

    fun productAddedSuccessfully() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun addProduct(sellerId: String) {
        var p: String = binding?.etAddproductAddproductprice?.text?.toString()!!
        val product = ProductModel(
            sellerId = sellerId,
            images = mProductImageUrl,
            description = binding?.etAddproductAddproductdescription?.text?.toString(),
            price = p.toLong(),
            name = binding?.etAddproductAddproductname?.text?.toString(),
            details = binding?.etAddproductAddproductdetails?.text?.toString()
        )
        FirestoreClass().addProduct(this,product)
    }

    private fun uploadProductImage(sellerId: String) {
        showProgressDialog("Please Wait ...")
        if(saveImageToInternalStorage!=null) {
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child("USER_IMAGE"+System.currentTimeMillis()+"."+getFileExtension(saveImageToInternalStorage!!))
            sRef.putFile(saveImageToInternalStorage!!).addOnSuccessListener {
                    taskSnapshot ->
                Log.i("Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri->
                    Log.i("Downloadable Image URL", uri.toString())
                    mProductImageUrl.add(uri.toString())
                    addProduct(sellerId)
                }
            }.addOnFailureListener {
                    exception ->
                Toast.makeText(this@AddProductActivity,exception.message.toString(),Toast.LENGTH_SHORT).show()
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

    companion object{
        private const val GALLERY = 1
        private const val CAMERA = 2
    }

}