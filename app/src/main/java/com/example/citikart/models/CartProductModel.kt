package com.example.citikart.models

import android.os.Parcel
import android.os.Parcelable

data class CartProductModel(
    var sellerId: String? = "",
    var productId: String?=""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sellerId)
        parcel.writeString(productId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartProductModel> {
        override fun createFromParcel(parcel: Parcel): CartProductModel {
            return CartProductModel(parcel)
        }

        override fun newArray(size: Int): Array<CartProductModel?> {
            return arrayOfNulls(size)
        }
    }
}