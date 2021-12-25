package com.accu.attendance.utils

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.WindowManager
import com.accu.attendance.BuildConfig
import com.accu.attendance.activity.BaseActivity
import com.accu.attendance.activity.SplashActivity
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by neeraj on 11/9/17.
 */
object Utils {

    fun changeDateFormat(dateString: String?, sourceDateFormat: String, targetDateFormat: String): String {
        if (dateString == null || dateString.isEmpty()) {
            return ""
        }
        val inputDateFromat = SimpleDateFormat(sourceDateFormat, Locale.getDefault())
        var date = Date()
        try {
            date = inputDateFromat.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return SimpleDateFormat(targetDateFormat, Locale.getDefault()).format(date)
    }

    fun changeDateFormatFromDate(sourceDate: Date?, targetDateFormat: String?): String {
        return if (sourceDate == null || targetDateFormat == null || targetDateFormat.isEmpty()) {
            ""
        } else SimpleDateFormat(targetDateFormat, Locale.getDefault()).format(sourceDate)
    }

    fun getUniqueDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun isValidMail(email: String): Boolean {
        return email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex())
    }

    fun isValidPassword(password: String): Boolean {
        return password.matches("^(?=\\S+$).{8,}$".toRegex())
    }

    fun changeStatusBarColor(activity: Activity, colorId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(activity, colorId)
        }
    }

    fun setStatusBarTranslucent(activity: Activity, makeTranslucent: Boolean) {
        if (makeTranslucent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
        }
    }

    fun getAddress(location: Location, context: Context): String {
        try {
            val geocoder: Geocoder
            val addresses: List<Address>
            geocoder = Geocoder(context)
            if (location.latitude != 0.0 || location.longitude != 0.0) {
                addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val address = addresses[0].getAddressLine(0)
                val address1 = addresses[0].getAddressLine(1)
                val country = addresses[0].countryName
                return address + ", " + address1 + ", " + (country ?: "")
            } else {
                return "Unable to get address of this location"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return "Unable to get address of this location"
        }

    }

    fun getAddress(lat: Double, lng: Double, context: Context): String {
        try {
            val geocoder = Geocoder(context)
            val addresses: List<Address>
            if (lat != 0.0 || lng != 0.0) {
                addresses = geocoder.getFromLocation(lat, lng, 1)
                val address = addresses[0].getAddressLine(0)
                val address1 = addresses[0].getAddressLine(1)
                val country = addresses[0].countryName
                return address + ", " + address1 + ", " + (country ?: "")
            } else {
                return ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }

    }

    fun debugLog(tag: String, s: String) {
        if (BuildConfig.DEBUG)
            Log.e(tag, s)
    }

    fun keyHash(context: Context) {
        try {
            val info = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KeyHash:>>>>>>>>>>>>>>>", "" + Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

    }

    fun logoutUser(activity: AppCompatActivity, store: PrefStore) {
        //Cancel any pending notification alarm
        val intent = Intent(activity, AlarmReceiver::class.java)
        val penIntent = PendingIntent.getBroadcast(activity, 1, intent, 0)
        val alManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alManager.cancel(penIntent)

        //Logout User
        store.saveString(Const.USER_ID, null)
        store.saveUserData(Const.USER_DATA, null)
        store.setBoolean(Const.IS_SIGNED_IN, true)
        activity.startActivity(Intent(activity, SplashActivity::class.java))
        activity.finish()
    }

    fun setNotification(baseActivity: BaseActivity) {
        if (baseActivity.store.getBoolean(Const.Settings.IS_NOTI_ON, true)) {
            val intent = Intent(baseActivity, AlarmReceiver::class.java)
            val penIntent = PendingIntent.getBroadcast(baseActivity, 1, intent, 0)
            val alManager = baseActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis + (baseActivity.store.getInt(Const.Settings.REM_H, 9) * 60 + baseActivity.store.getInt(Const.Settings.REM_M, 0)) * 60 * 1000, penIntent)
        }
    }

    fun cancelNotification(baseActivity: BaseActivity) {
        val intent = Intent(baseActivity, AlarmReceiver::class.java)
        val penIntent = PendingIntent.getBroadcast(baseActivity, 1, intent, 0)
        val alManager = baseActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alManager.cancel(penIntent)
    }
}
