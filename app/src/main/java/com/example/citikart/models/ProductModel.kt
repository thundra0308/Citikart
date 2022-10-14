package com.example.citikart.models

import android.os.Parcel
import android.os.Parcelable

data class ProductModel(
    var documentId: String?="",
    var sellerId: String?="",
    var images: ArrayList<String>?= ArrayList(),
    var name: String?="",
    var description: String?="",
    var price: Long=0,
    var details: String?=""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(documentId)
        parcel.writeString(sellerId)
        parcel.createStringArrayList()
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeLong(price)
        parcel.writeString(details)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductModel> {
        override fun createFromParcel(parcel: Parcel): ProductModel {
            return ProductModel(parcel)
        }

        override fun newArray(size: Int): Array<ProductModel?> {
            return arrayOfNulls(size)
        }
    }
}