package com.accu.attendance.fragment.home

import android.location.Location
import com.accu.attendance.retrofitManager.ResponseListener
import com.google.gson.JsonObject
import retrofit2.Call

/**
 * Created by neeraj on 23/7/18.
 */
class HomePresenter(private val viewContract: HomeContract.ViewContract
                    , private val presenterImpl: HomeContract.PresenterInterector) :
        HomeContract.PresenterContract
        , HomeContract.PresenterInterector.OnFinished {

    init {
        presenterImpl.setPresenterListener(this)
    }

    override fun onGetUserDataCall(call: Call<JsonObject>) {
        presenterImpl.getUserData(call)
    }

    override fun onGetLocationCall() {
        presenterImpl.getUserLocation()
    }

    override fun onGPSEnabled() {
        presenterImpl.GPSIsEnabledNow()
    }

    override fun onDestroy() {
        presenterImpl.onDestroyed()
    }

    override fun onSignInClick(call: Call<JsonObject>, message: String) {
        presenterImpl.doSignIn(call, message)
    }

    override fun onSignOutClick(call: Call<JsonObject>, message: String) {
        presenterImpl.doSignOut(call, message)
    }


    override fun onGetUserDataSuccess(jsonObject: JsonObject) {
        viewContract.onGetUserDataSuccess(jsonObject)
    }

    override fun onStartGettingLocation() {
        viewContract.onStartGettingLocation()
    }

    override fun onLocationFound(location: Location) {
        viewContract.onLocationFound(location)
    }

    override fun onLocationNotFound() {
        viewContract.onLocationNotFound()
    }

    override fun onLocationPermissionDenied() {
        viewContract.onLocationPermissionDenied()
    }

    override fun onSignInOutSuccess(jsonObject: JsonObject) {
        viewContract.onSignInOutSuccess(jsonObject)
    }

    override fun onAnyError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        viewContract.onPError(call, statusCode, errorMessage, responseListener)
    }
}