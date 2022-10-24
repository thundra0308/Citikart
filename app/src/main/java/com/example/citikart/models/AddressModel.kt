package com.example.citikart.models

import android.os.Parcel
import android.os.Parcelable

data class AddressModel(
    var fullName: String? = "",
    var mobileNo: String? = "",
    var areaPin: String? = "",
    var flatHouse: String? = "",
    var areaStreet: String? = "",
    var landmark: String? = "",
    var townCity: String? = "",
    var state: String? = "",
    var addressType: String? = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fullName)
        parcel.writeString(mobileNo)
        parcel.writeString(areaPin)
        parcel.writeString(flatHouse)
        parcel.writeString(areaStreet)
        parcel.writeString(landmark)
        parcel.writeString(townCity)
        parcel.writeString(state)
        parcel.writeString(addressType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddressModel> {
        override fun createFromParcel(parcel: Parcel): AddressModel {
            return AddressModel(parcel)
        }

        override fun newArray(size: Int): Array<AddressModel?> {
            return arrayOfNulls(size)
        }
    }
}