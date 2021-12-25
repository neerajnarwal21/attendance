package com.accu.attendance.fragment.home

import android.location.Location
import com.accu.attendance.activity.BaseActivity
import com.accu.attendance.retrofitManager.ApiManager
import com.accu.attendance.retrofitManager.ResponseListener
import com.accu.attendance.utils.LocationManager
import com.google.gson.JsonObject
import retrofit2.Call

/**
 * Created by neeraj on 23/7/18.
 */
class HomePresenterImpl(private val apiManager: ApiManager, private val baseActivity: BaseActivity) : HomeContract.PresenterInterector, ResponseListener, LocationManager.LocationUpdates {


    private lateinit var onFinished: HomeContract.PresenterInterector.OnFinished

    private lateinit var userDataCall: Call<JsonObject>
    private var locationManager = LocationManager()


    //Presenter Callbacks
    override fun setPresenterListener(onFinished: HomeContract.PresenterInterector.OnFinished) {
        this.onFinished = onFinished
    }

    override fun getUserData(call: Call<JsonObject>) {
        apiManager.makeApiCall(call, this, false)
        userDataCall = call
    }

    override fun getUserLocation() {
        locationManager.startLocationManager(baseActivity, LocationManager.Accuracy.HIGH, this)
    }

    override fun GPSIsEnabledNow() {
        locationManager.onGPSEnable()
    }

    override fun onDestroyed() {
        locationManager.removeLocationUpdates()
    }

    override fun doSignIn(call: Call<JsonObject>, message: String) {
        apiManager.makeApiCall(call, this, message)
    }

    override fun doSignOut(call: Call<JsonObject>, message: String) {
        apiManager.makeApiCall(call, this, message)
    }

    //Retrofit Callbacks
    override fun onSuccess(call: Call<*>, `object`: Any) {
        if (call === userDataCall) {
            val userObject = `object` as JsonObject
            onFinished.onGetUserDataSuccess(userObject)
        } else {
            val jsonObject = `object` as JsonObject
            onFinished.onSignInOutSuccess(jsonObject)
        }
    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        onFinished.onAnyError(call, statusCode, errorMessage, responseListener)
    }


    //Location Manager Callbacks
    override fun onStartingGettingLocation() {
        onFinished.onStartGettingLocation()
    }

    override fun onLocationFound(location: Location) {
        onFinished.onLocationFound(location)
    }

    override fun onLocationNotFound() {
        onFinished.onLocationNotFound()
    }

    override fun onLocationPermissionDenied() {
        onFinished.onLocationPermissionDenied()
    }
}