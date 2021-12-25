package com.accu.attendance.data

/**
 * Created by neeraj on 27/12/17.
 */

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserData(
        @SerializedName("ID") @Expose var iD: String,
        @SerializedName("Empcode") @Expose var empcode: String,
        @SerializedName("Name") @Expose var name: String,
        @SerializedName("Email") @Expose var email: String,
        @SerializedName("Password") @Expose var password: String,
        @SerializedName("Phone") @Expose var phone: String,
        @SerializedName("Department") @Expose var deptt: String,
        @SerializedName("Photo") @Expose var photo: String,
        @SerializedName("Token") @Expose var token: String,
        @SerializedName("Status") @Expose var status: String,
        @SerializedName("Logged") @Expose var logged: String,
        @SerializedName("pugmarks_lat") @Expose var companyLat: Double = 0.0,
        @SerializedName("pugmarks_lng") @Expose var companyLng: Double = 0.0) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readDouble())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(iD)
        parcel.writeString(empcode)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeString(phone)
        parcel.writeString(deptt)
        parcel.writeString(photo)
        parcel.writeString(token)
        parcel.writeString(status)
        parcel.writeString(logged)
        parcel.writeDouble(companyLat)
        parcel.writeDouble(companyLng)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<UserData> {
        override fun createFromParcel(parcel: Parcel): UserData {
            return UserData(parcel)
        }

        override fun newArray(size: Int): Array<UserData?> {
            return arrayOfNulls(size)
        }
    }
}

