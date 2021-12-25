package com.accu.attendance.fragment.home

import android.location.Location
import com.accu.attendance.retrofitManager.ResponseListener
import com.google.gson.JsonObject
import retrofit2.Call

/**
 * Created by neeraj on 23/7/18.
 */
interface HomeContract {
    interface PresenterContract {
        fun onGetUserDataCall(call: Call<JsonObject>)
        fun onGetLocationCall()
        fun onGPSEnabled()
        fun onDestroy()
        fun onSignInClick(call: Call<JsonObject>, message: String)
        fun onSignOutClick(call: Call<JsonObject>, message: String)
    }

    interface PresenterInterector {
        interface OnFinished {
            fun onGetUserDataSuccess(jsonObject: JsonObject)
            fun onStartGettingLocation()
            fun onLocationFound(location: Location)
            fun onLocationNotFound()
            fun onLocationPermissionDenied()
            fun onSignInOutSuccess(jsonObject: JsonObject)
            fun onAnyError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener)
        }

        fun setPresenterListener(onFinished: OnFinished)
        fun getUserData(call: Call<JsonObject>)
        fun getUserLocation()
        fun GPSIsEnabledNow()
        fun onDestroyed()
        fun doSignIn(call: Call<JsonObject>, message: String)
        fun doSignOut(call: Call<JsonObject>, message: String)
    }

    interface ViewContract {
        fun onGetUserDataSuccess(jsonObject: JsonObject)
        fun onStartGettingLocation()
        fun onLocationFound(location: Location)
        fun onLocationNotFound()
        fun onLocationPermissionDenied()
        fun onSignInOutSuccess(jsonObject: JsonObject)
        fun onPError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener)
    }
}