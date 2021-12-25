package com.accu.attendance.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.animation.AnimationUtils
import com.accu.attendance.R
import com.accu.attendance.utils.Const
import com.accu.attendance.utils.TokenRefresh
import com.accu.attendance.utils.Utils
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_splash.*


/**
 * Created by Neeraj Narwal on 5/5/17.
 */


class SplashActivity : BaseActivity(), TokenRefresh.TokenListener {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        log("Token >>>> onCreate checking token :-- ${store.getString(Const.DEVICE_TOKEN)}")
        createNotificationChannel()
        signInTV.setOnClickListener { gotoLoginAct("login") }
        signUpTV.setOnClickListener { gotoLoginAct("signup") }
        Utils.setStatusBarTranslucent(this, true)
        if (initFCM())
            handler.postDelayed({
                log("Token >>>> After 1 sec checking token :-- ${store.getString(Const.DEVICE_TOKEN)}")
                if (!isFinishing && store.getString(Const.DEVICE_TOKEN) != null)
                    startAnimations()
                else {
                    registerForTokenCallback()
                    waitFor10SecMore()
                }
            }, 1000)
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(0)
    }

    private fun gotoLoginAct(s: String) {
        val intent = Intent(this, LoginSignUpActivity::class.java)
        intent.putExtra("goto", s)
        startActivity(intent)
    }

    private fun startAnimations() {
        if (store.getString(Const.USER_ID) != null) {
            gotoMainAct()
        } else
            runOnUiThread {
                signInLL.visibility = View.VISIBLE
                val animation = AnimationUtils.loadAnimation(this, R.anim.slide_from_bottom)
                val animationResize = AnimationUtils.loadAnimation(this, R.anim.resize_image)
                animation.fillAfter = true
                animationResize.fillAfter = true
                signInLL.startAnimation(animation)
                logoIV.startAnimation(animationResize)
            }
    }

    private fun gotoMainAct() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun waitFor10SecMore() {
        log("Token >>>> Waiting for token to come")
        handler.postDelayed(waitingRunnable, 15000)
    }

    private fun registerForTokenCallback() {
        val tokenRefresh = TokenRefresh.getInstance()
        tokenRefresh.setTokenListener(this)
    }

    override fun onTokenRefresh() {
        log("Token >>>> Token is here")
        handler.removeCallbacks(waitingRunnable)
        startAnimations()
    }

    private val waitingRunnable = Runnable {
        if (!isFinishing && store.getString(Const.DEVICE_TOKEN) == null) {
            showDialogNoServices()
        }
    }

    private fun showDialogNoServices() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.alert))
        builder.setMessage(getString(R.string.unable_to_initialize))
        builder.setPositiveButton(getString(R.string.close)) { dialog, _ -> dialog.dismiss() }
        builder.setOnDismissListener { exitFromApp() }
        builder.show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channelId = Const.NOTI_CHANNEL_ID
            val channelName = "Some Channel"
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        FirebaseMessaging.getInstance().subscribeToTopic(Const.THOUGHT_OF_DAY)
    }
}
