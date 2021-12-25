package com.accu.attendance.utils

import android.Manifest
import android.app.Activity
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

/**
 * Created by neeraj on 26/7/17.
 */
class LocationManager : GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, PermissionsManager.PermissionCallback {

    lateinit var mLocationRequest: LocationRequest
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var activity: Activity? = null
    private var locationUpdates: LocationUpdates? = null

    companion object {
        const val REQUEST_LOCATION = 199
    }

    private var accuracy: Accuracy? = null
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            val location = locationResult!!.locations[0]
            log("Location : " + location.latitude + " " + location.longitude + " " + locationResult.locations.size)
            locationUpdates?.onLocationFound(location)
            locationUpdates = null
            removeLocationUpdates()
        }

        override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
            super.onLocationAvailability(locationAvailability)
            if (!locationAvailability!!.isLocationAvailable) {
                log("Location not found")
                locationUpdates?.onLocationNotFound()
                removeLocationUpdates()
            }
        }
    }

    fun removeLocationUpdates() {
        if (mFusedLocationClient != null) {
            log("Removing updates")
            mFusedLocationClient!!.removeLocationUpdates(locationCallback)
        }
    }

    fun startLocationManager(activity: Activity, accuracy: Accuracy, locationUpdates: LocationUpdates) {
        this.locationUpdates = locationUpdates
        this.activity = activity
        this.accuracy = accuracy
        val mGoogleApiClient = GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build()
        mGoogleApiClient.connect()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    }

    override fun onConnected(bundle: Bundle?) {
        if (PermissionsManager.checkPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 122, this)) {
            mLocationRequest = LocationRequest.create()
            if (accuracy == Accuracy.HIGH) {
                mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                mLocationRequest.smallestDisplacement = 0.001f
                mLocationRequest.interval = 1000
                mLocationRequest.fastestInterval = 1000
            } else {
                mLocationRequest.priority = LocationRequest.PRIORITY_LOW_POWER
            }
            val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest)
                    .setAlwaysShow(true)

            val result = LocationServices.getSettingsClient(activity!!).checkLocationSettings(builder.build())

            result.addOnCompleteListener { task ->
                try {
                    //It will trigger any exception if there is any requirement of resolution
                    task.getResult(ApiException::class.java)
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    getLocation()
                } catch (exception: ApiException) {
                    when (exception.statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                val resolvable = exception as ResolvableApiException
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(activity, REQUEST_LOCATION)
                            } catch (e: IntentSender.SendIntentException) {
                                // Ignore the error.
                            } catch (e: ClassCastException) {
                                // Ignore, should be an impossible error.
                            }

                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ->
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            locationUpdates?.onLocationNotFound()
                    }
                }
            }
        }
    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    private fun log(s: String) {
        Utils.debugLog("Location Manager: ", s)
    }

    fun onGPSEnable() {
        if (PermissionsManager.checkPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 123, this)) {
            getLocation()
        }
    }

    private fun getLocation() {
        mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, locationCallback, null)
        log("Starting updates")
        locationUpdates?.onStartingGettingLocation()
    }

    override fun onPermissionsGranted(resultCode: Int) {
        if (resultCode == 123)
            onGPSEnable()
        else if (resultCode == 122) {
            onConnected(null)
        }
    }

    override fun onPermissionsDenied(resultCode: Int) {
        locationUpdates?.onLocationPermissionDenied()
    }

    enum class Accuracy {
        HIGH,
        LOW
    }

    interface LocationUpdates {

        fun onStartingGettingLocation()

        fun onLocationFound(location: Location)

        fun onLocationNotFound()

        fun onLocationPermissionDenied()
    }
}
