package com.accu.attendance.data

/**
 * Created by Neeraj Narwal on 26/6/17.
 */

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DrawerData(
        @SerializedName("title") @Expose var title: String) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<DrawerData> {
        override fun createFromParcel(parcel: Parcel): DrawerData {
            return DrawerData(parcel)
        }

        override fun newArray(size: Int): Array<DrawerData?> {
            return arrayOfNulls(size)
        }
    }
}
