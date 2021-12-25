package com.accu.attendance.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by neeraj on 1/2/18.
 */

data class UserLoginData(
        @SerializedName("ID") @Expose var iD: String,
        @SerializedName("Userid") @Expose var userid: String,
        @SerializedName("Date") @Expose var date: String,
        @SerializedName("SignInTime") @Expose var signInTime: String,
        @SerializedName("SignInLat") @Expose var signInLat: String,
        @SerializedName("SignInLong") @Expose var signInLong: String,
        @SerializedName("SignInNote") @Expose var signInNote: String,
        @SerializedName("SignInAddress") @Expose var signInAddress: String,
        @SerializedName("SignOutTime") @Expose var signOutTime: String?,
        @SerializedName("SignOutLat") @Expose var signOutLat: String = "",
        @SerializedName("SignOutLong") @Expose var signOutLong: String = "",
        @SerializedName("SignOutAddress") @Expose var signOutAddress: String = "",
        @SerializedName("SignOutNote") @Expose var signOutNote: String = "",
        @SerializedName("State") @Expose var state: String,
        @SerializedName("DayStatus") @Expose var dayStatus: String,
        @SerializedName("Deptt") @Expose var deptt: String) : Parcelable {
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
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(iD)
        parcel.writeString(userid)
        parcel.writeString(date)
        parcel.writeString(signInTime)
        parcel.writeString(signInLat)
        parcel.writeString(signInLong)
        parcel.writeString(signInNote)
        parcel.writeString(signInAddress)
        parcel.writeString(signOutTime)
        parcel.writeString(signOutLat)
        parcel.writeString(signOutLong)
        parcel.writeString(signOutAddress)
        parcel.writeString(signOutNote)
        parcel.writeString(state)
        parcel.writeString(dayStatus)
        parcel.writeString(deptt)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<UserLoginData> {
        override fun createFromParcel(parcel: Parcel): UserLoginData {
            return UserLoginData(parcel)
        }

        override fun newArray(size: Int): Array<UserLoginData?> {
            return arrayOfNulls(size)
        }
    }
}

