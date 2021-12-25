package com.accu.attendance.activity

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.View
import com.accu.attendance.R
import com.accu.attendance.adapter.DrawerAdapter
import com.accu.attendance.data.DrawerData
import com.accu.attendance.data.UserData
import com.accu.attendance.fragment.ChangePasswordFragment
import com.accu.attendance.fragment.SettingsFragment
import com.accu.attendance.fragment.home.HomeFragment
import com.accu.attendance.fragment.myCalendar.MyCalendarFragment
import com.accu.attendance.fragment.profile.ProfileFragment
import com.accu.attendance.retrofitManager.ResponseListener
import com.accu.attendance.utils.Const
import com.accu.attendance.utils.LocationManager
import com.accu.attendance.utils.Utils
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_profile_save.view.*
import kotlinx.android.synthetic.main.toolbar_custom.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import java.util.*


class MainActivity : BaseActivity() {

    private lateinit var logoutCall: Call<JsonObject>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
        handleIntent(intent)
    }

    private fun initUI() {
        setDrawer()
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        setUserData()
        jumpToFragment(HomeFragment())

        backTBIV.setOnClickListener(this)
        backTBTV.setOnClickListener(this)
        topRL.setOnClickListener(this)
        navTBIV.setOnClickListener(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    fun handleIntent(intent: Intent) {

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(1)
        if (intent.getBooleanExtra("fromPush", false)) {
            val action = intent.getStringExtra("action")
            when (action) {
                "time-change" -> {
                    val message = intent.getStringExtra("message")
                    val bndl = Bundle()
                    bndl.putString("message", message)
                    val frag = MyCalendarFragment()
                    frag.arguments = bndl
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fc_home, frag)
                            .commit()
                }
            }
        } else
            jumpToFragment(HomeFragment())
    }

    private fun jumpToFragment(fragment: Fragment) {
        drawer.closeDrawer(Gravity.START)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fc_home, fragment)
                .commit()
    }

    private fun setDrawer() {
        val drawerData = ArrayList<DrawerData>()
        drawerData.add(DrawerData(getString(R.string.drawer_home)))
        drawerData.add(DrawerData(getString(R.string.drawer_change_pass)))
        drawerData.add(DrawerData(getString(R.string.drawer_my_calender)))
        drawerData.add(DrawerData(getString(R.string.drawer_settings)))
        drawerData.add(DrawerData(getString(R.string.drawer_logout)))
        left_drawerRV.layoutManager = LinearLayoutManager(this)
        left_drawerRV.adapter = DrawerAdapter(this, drawerData)
    }

    fun onAdapterItemClick(drawerData: DrawerData) {
        when (drawerData.title) {
            getString(R.string.drawer_home) -> jumpToFragment(HomeFragment())
            getString(R.string.drawer_change_pass) -> jumpToFragment(ChangePasswordFragment())
            getString(R.string.drawer_my_calender) -> jumpToFragment(MyCalendarFragment())
            getString(R.string.drawer_settings) -> jumpToFragment(SettingsFragment())
            getString(R.string.drawer_logout) -> logoutUser()
        }
        drawer.closeDrawer(Gravity.START)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.backTBIV,
            R.id.backTBTV -> onBackPressed()
            R.id.navTBIV -> {
                hideSoftKeyboard()
                drawer.openDrawer(Gravity.START)
            }
            R.id.topRL -> jumpToFragment(ProfileFragment())
        }
    }

    fun setUserData() {
        val userData = store.getUserData(Const.USER_DATA, UserData::class.java)
        picasso.load(Const.SERVER_REMOTE_URL + userData.photo).placeholder(R.mipmap.ic_default_user).into(userIV)
        nameTV.setText(userData.name)
        infoTV.setText("${userData.deptt} - ${userData.empcode}")
    }

    fun setToolbar(showDrawer: Boolean, showToolbar: Boolean) {
        navTBIV.visibility = if (showDrawer) View.VISIBLE else View.GONE
        backTBTV.visibility = if (showDrawer) View.GONE else View.VISIBLE
        backTBIV.visibility = if (showDrawer) View.GONE else View.VISIBLE
        toolbar.visibility = if (showToolbar) View.VISIBLE else View.GONE
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.START)) {
            drawer.closeDrawer(Gravity.START)
        } else {
            val frag = supportFragmentManager.findFragmentById(R.id.fc_home)
            if (supportFragmentManager.backStackEntryCount < 0) {
                supportFragmentManager.popBackStack()
            } else if (frag is HomeFragment)
                showExit()
            else if (frag is ProfileFragment) {
                if (frag.getEditState() && !store.getBoolean(Const.DONT_SHOW_PROFILE_EXIT, false)) {
                    showProfileExitDialog()
                } else
                    jumpToFragment(HomeFragment())
            } else {
                jumpToFragment(HomeFragment())
            }
        }
    }

    private fun showProfileExitDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.save_changes))
        builder.setMessage(getString(R.string.sure_goback_without_saving))
        val view = View.inflate(this, R.layout.dialog_profile_save, null)
        builder.setView(view)
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            if (view.dontAskCB.isChecked) {
                store.setBoolean(Const.DONT_SHOW_PROFILE_EXIT, true)
            }
            jumpToFragment(HomeFragment())
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }
        hideSoftKeyboard()
        builder.create().show()
    }

    private fun logoutUser() {
        val userId = RequestBody.create(MediaType.parse("text/plain"), store.getString(Const.USER_ID))
        logoutCall = apiInterface.logout(userId)
        apiManager.makeApiCall(logoutCall, this, getString(R.string.logging_out))
    }

    override fun onSuccess(call: Call<*>, `object`: Any) {
        super.onSuccess(call, `object`)
        Utils.logoutUser(this, store)
    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        if (call == logoutCall) {
            Utils.logoutUser(this, store)
        } else
            super.onError(call, statusCode, errorMessage, responseListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = supportFragmentManager.findFragmentById(R.id.fc_home)

        if (fragment is HomeFragment) {
            when (requestCode) {
                LocationManager.REQUEST_LOCATION ->
                    when (resultCode) {
                        Activity.RESULT_OK -> {
                            fragment.onGPSEnable()
                        }
                        Activity.RESULT_CANCELED -> {
                            fragment.onGPSEnableDenied()
                        }
                        else -> {
                            fragment.onGPSEnableDenied()
                        }
                    }
            }
        }
    }
}
