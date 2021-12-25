package com.accu.attendance.data

/**
 * Created by Neeraj Narwal on 26/6/17.
 */

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserDayData(
        @SerializedName("Date") @Expose var date: String,
        @SerializedName("SignIn") @Expose var signIn: String?,
        @SerializedName("Signout") @Expose var signout: String?,
        @SerializedName("Status") @Expose var status: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(date)
        parcel.writeString(signIn)
        parcel.writeString(signout)
        parcel.writeInt(status)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<UserDayData> {
        override fun createFromParcel(parcel: Parcel): UserDayData {
            return UserDayData(parcel)
        }

        override fun newArray(size: Int): Array<UserDayData?> {
            return arrayOfNulls(size)
        }
    }
}