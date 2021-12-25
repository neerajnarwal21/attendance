package com.accu.attendance.fragment.home

import android.location.Location
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.accu.attendance.R
import com.accu.attendance.activity.MainActivity
import com.accu.attendance.data.UserData
import com.accu.attendance.data.UserLoginData
import com.accu.attendance.fragment.BaseFragment
import com.accu.attendance.retrofitManager.ProgressDialog
import com.accu.attendance.retrofitManager.ResponseListener
import com.accu.attendance.utils.Const
import com.accu.attendance.utils.MyTarget
import com.accu.attendance.utils.Utils
import com.accu.attendance.utils.maps.MapUtils
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fg_home.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by neeraj on 16/12/17.
 */
class HomeFragment : BaseFragment(), HomeContract.ViewContract {

    private val MIN_DISTANCE = 250
    private var PUGMARKS_LAT = 0.0
    private var PUGMARKS_LNG = 0.0

    private enum class Attendance {
        SIGNIN, SIGNOUT
    }

    private lateinit var userDataCall: Call<JsonObject>
    private lateinit var attendance: Attendance
    private var count = 0
    private var userCount = 0
    private lateinit var dialog: ProgressDialog
    private lateinit var presenter: HomePresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_home, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(true, true)
        presenter = HomePresenter(this, HomePresenterImpl(apiManager, baseActivity))
        initUI()
        dialog = ProgressDialog.getInstance(context!!)
    }

    private fun initUI() {
        wishTV.text = getWish()
        nameTV.text = (baseActivity.store.getUserData(Const.USER_DATA, UserData::class.java)).name
        monthTV.text = SimpleDateFormat("MMMM", Locale.getDefault()).format(Date())
        enableSignIn(baseActivity.store.getBoolean(Const.IS_SIGNED_IN, true))
        getUserStatus()
        setListeners(true)
        checkForThought()
    }

    private fun checkForThought() {
        if (baseActivity.store.containValue(Const.CURRENT_DATE)
                && SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date()) == baseActivity.store.getString(Const.CURRENT_DATE)) {
            thoughtTV.visibility = View.VISIBLE
            thoughtTV.setText(baseActivity.store.getString(Const.THOUGHT_MSG))
        }
    }

    private fun getUserStatus() {
        val userid = RequestBody.create(MediaType.parse("text/plain"), baseActivity.store.getString(Const.USER_ID))
        val token = RequestBody.create(MediaType.parse("text/plain"), baseActivity.store.getString(Const.DEVICE_TOKEN, "tokenn"))
        userDataCall = apiInterface.getUserStatus(userid, token)
        presenter.onGetUserDataCall(userDataCall)
    }

    private fun setListeners(doEnable: Boolean) {
        signInIV.setOnClickListener(if (doEnable) this else null)
        signOutIV.setOnClickListener(if (doEnable) this else null)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.signInIV -> attendance = Attendance.SIGNIN
            R.id.signOutIV -> attendance = Attendance.SIGNOUT
        }
        presenter.onGetLocationCall()
        setListeners(false)
    }

    private fun getWish(): String {
        return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 3..11 -> getString(R.string.good_morning)
            in 12..17 -> getString(R.string.good_afternoon)
            else -> getString(R.string.good_evening)
        }
    }

    fun onGPSEnable() {
        presenter.onGPSEnabled()
    }

    fun onGPSEnableDenied() {
        showToast(getString(R.string.check_gps_on), true)
        setListeners(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    private fun showLocationDialog() {
        val builer = AlertDialog.Builder(context!!)
        builer.setTitle(getString(R.string.location_alert))
        builer.setMessage(getString(R.string.location_services_error))
        builer.setPositiveButton(getString(R.string.ok), null)
        builer.create().show()
    }

    private fun showDialog(location: Location) {
        val ourLoc = Location("")
        ourLoc.latitude = PUGMARKS_LAT
        ourLoc.longitude = PUGMARKS_LNG

//        location.latitude = 30.703165
//        location.longitude = 76.694621

        val distance = Math.floor(MapUtils.with(context).distanceBetweenTwoLocations(ourLoc.latitude, ourLoc.longitude, location.latitude, location.longitude))
        log("Distance is $distance")

        //Log event on Firebase
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context!!)
        val bundle = Bundle()

        bundle.putString("location", location.latitude.toString() + ", " + location.longitude.toString())
        bundle.putString("username", (baseActivity.store.getUserData(Const.USER_DATA, UserData::class.java)).name)
        analytics.logEvent("post_params", bundle)

        val view = View.inflate(context, R.layout.dialog_signin_out, null)
        val builder = AlertDialog.Builder(context!!)
        builder.setView(view)

        val locIV = view.findViewById<ImageView>(R.id.locIV)
        val noteET = view.findViewById<EditText>(R.id.noteET)
        val titleTV = view.findViewById<TextView>(R.id.titleTV)
        titleTV.text = if (attendance == Attendance.SIGNIN) getString(R.string.mark_signin) else getString(R.string.mark_signout)
        builder.setPositiveButton(getString(R.string.submit)) { _, _ ->
            if (distance < MIN_DISTANCE)
                submitAttendance(getText(noteET), location)
            else
                showDistanceDialog(getText(noteET), location)
        }
        builder.setNegativeButton(getString(R.string.cancel), null)

        val dialog = builder.create()
        dialog.setOnShowListener {
            this.dialog.stopProgressDialog()
        }

        val path = "https://maps.googleapis.com/maps/api/staticmap?" +
                "size=600x450&path=fillcolor:" +
                (if (distance < MIN_DISTANCE) "0x01CA7D" else "0xFF6347") +
                "%7Cweight:1%7Ccolor:0xFFFFFF%7C" +
                URLEncoder.encode("enc:" + MapUtils.with(context).getEncodedUrl(ourLoc, 250), "utf-8") +
                "&markers=color:blue" + "%7C" +
                location.latitude + "," + location.longitude + "&format=jpg" +
                if (distance < MIN_DISTANCE) "&zoom=16&key=AIzaSyB4zUcSrAhg374z7BRcLGFjTo8zBU8HzGg" else ""
        val target = MyTarget(baseActivity, path, dialog, locIV)
        baseActivity.picasso.load(path).into(target)
        locIV.tag = target
    }

    private fun showDistanceDialog(text: String, location: Location) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(getString(R.string.location_alert))
        builder.setMessage(getString(R.string.your_location_is_outside_parameter
                , (if (attendance == Attendance.SIGNIN) getString(R.string.signin) else getString(R.string.signout))))
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            submitAttendance(text, location)
        }
        builder.setNegativeButton(getString(R.string.no), null)
        builder.create().show()
    }

    private fun submitAttendance(note: String, location: Location) {
        if (attendance == Attendance.SIGNIN)
            submitSignIn(note, location)
        else
            submitSignOut(note, location)
    }

    private fun submitSignIn(note: String, location: Location) {
        val userid = RequestBody.create(MediaType.parse("text/plain"), baseActivity.store.getString(Const.USER_ID))
        val signInTime = RequestBody.create(MediaType.parse("text/plain"), SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(Date()))
        val signInLat = RequestBody.create(MediaType.parse("text/plain"), location.latitude.toString())
        val signInLng = RequestBody.create(MediaType.parse("text/plain"), location.longitude.toString())
        val signInNote = RequestBody.create(MediaType.parse("text/plain"), note)
        val signInAddress = RequestBody.create(MediaType.parse("text/plain"), Utils.getAddress(location, context!!))
        val signInCall = apiInterface.signInDay(userid, signInTime, signInLat, signInLng, signInNote, signInAddress)
        presenter.onSignInClick(signInCall, getString(R.string.signin_you_in))
    }

    private fun submitSignOut(note: String, location: Location) {
        val userid = RequestBody.create(MediaType.parse("text/plain"), baseActivity.store.getString(Const.USER_ID))
        val signOutTime = RequestBody.create(MediaType.parse("text/plain"), SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(Date()))
        val signOutLat = RequestBody.create(MediaType.parse("text/plain"), location.latitude.toString())
        val signOutLng = RequestBody.create(MediaType.parse("text/plain"), location.longitude.toString())
        val signOutNote = RequestBody.create(MediaType.parse("text/plain"), note)
        val signOutAddress = RequestBody.create(MediaType.parse("text/plain"), Utils.getAddress(location, context!!))
        val signOutCall = apiInterface.signOutDay(userid, signOutTime, signOutLat, signOutLng, signOutNote, signOutAddress)
        presenter.onSignOutClick(signOutCall, getString(R.string.signin_you_out))
    }

    private fun enableSignIn(enableSignIn: Boolean) {
        signInIV.isEnabled = enableSignIn
        signOutIV.isEnabled = !enableSignIn
        signInTV.setTextColor(ContextCompat.getColor(context!!, if (enableSignIn) R.color.Black else R.color.text_disable))
        signOutTV.setTextColor(ContextCompat.getColor(context!!, if (enableSignIn) R.color.text_disable else R.color.Black))
    }

    override fun onStartGettingLocation() {
        dialog.updateMessage(getString(R.string.finding_your_location))
        dialog.startProgressDialog()
    }

    override fun onLocationFound(location: Location) {
        showDialog(location)
        setListeners(true)
    }

    override fun onLocationNotFound() {
        count++
        if (count < 3)
            showToast(getString(R.string.unable_to_find_location), true)
        else {
            count = 0
            showLocationDialog()
        }
        dialog.stopProgressDialog()
        setListeners(true)
    }

    override fun onLocationPermissionDenied() {
        showToast(getString(R.string.not_sufficient_permissions, getString(R.string.app_name)), true)
        setListeners(true)
    }

    override fun onGetUserDataSuccess(jsonObject: JsonObject) {
        val userdata = jsonObject.getAsJsonArray("userdata").get(0)
        val userObjectType = object : TypeToken<UserData>() {}.type
        val userData = Gson().fromJson<UserData>(userdata, userObjectType)
        baseActivity.store.saveUserData(Const.USER_DATA, userData)
        baseActivity.store.saveString(Const.USER_ID, userData.iD)
        PUGMARKS_LAT = userData.companyLat
        PUGMARKS_LNG = userData.companyLng
        (baseActivity as MainActivity).setUserData()

        //Get UserTime and update UI
        val userArray = jsonObject.getAsJsonArray("usertime")
        if (userArray.size() > 0) {
            val data = userArray.get(0)
            val objectType = object : TypeToken<UserLoginData>() {}.type
            val userTimeData = Gson().fromJson<UserLoginData>(data, objectType)
            if (userTimeData.signOutTime != null && !userTimeData.signOutTime!!.isEmpty()) {
                baseActivity.store.setBoolean(Const.IS_SIGNED_IN, true)
                enableSignIn(true)
                lastStatusTV.text = getString(R.string.last_out_time, Utils.changeDateFormat(userTimeData.signOutTime, "yyyy-MM-dd HH:mm:ss", "dd MMM, hh:mm a"))
            } else {
                enableSignIn(false)
                baseActivity.store.setBoolean(Const.IS_SIGNED_IN, false)
                lastStatusTV.text = getString(R.string.in_time, Utils.changeDateFormat(userTimeData.signInTime, "yyyy-MM-dd HH:mm:ss", "dd MMM, hh:mm a"))
            }
        }
    }

    override fun onSignInOutSuccess(jsonObject: JsonObject) {
        showToast(jsonObject.get("message").asString)
        getUserStatus()

        if (attendance === Attendance.SIGNIN)
            Utils.setNotification(baseActivity)
        else
            Utils.cancelNotification(baseActivity)
    }

    override fun onPError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        if (call == userDataCall) {
            log("On error userCount ${userCount}")
            if (errorMessage.equals("User doesn't exists")) {
                showToast(getString(R.string.accout_doesnt_exist))
                Utils.logoutUser(baseActivity, baseActivity.store)
                return
            }
            if (userCount < 2) {
                getUserStatus()
                userCount++
            } else {
                super.onError(call, statusCode, errorMessage, responseListener)
                userCount = 0
            }
        } else {
            super.onError(call, statusCode, errorMessage, responseListener)
            getUserStatus()
        }
    }
}