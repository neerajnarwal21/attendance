package com.accu.attendance.utils

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.accu.attendance.R
import com.accu.attendance.activity.BaseActivity
import java.util.*

/**
 * Created by neeraj on 29/9/17.
 */
object PermissionsManager {

    private var permissionCallback: PermissionCallback? = null
    private var reqCode: Int = 0
    private val PERMISSION_REQUEST_CODE = 99

    fun checkPermissions(activity: Activity, perms: Array<String>, requestCode: Int, permissionCallback: PermissionCallback): Boolean {
        PermissionsManager.permissionCallback = permissionCallback
        PermissionsManager.reqCode = requestCode
        val permsArray = ArrayList(Arrays.asList(*perms))
        for (perm in perms) {
            if (ContextCompat.checkSelfPermission(activity, perm) == PackageManager.PERMISSION_GRANTED)
                permsArray.remove(perm)
        }
        if (permsArray.size > 0)
            ActivityCompat.requestPermissions(activity, permsArray.toTypedArray(), PERMISSION_REQUEST_CODE)
        return permsArray.size == 0
    }

    fun onPermissionsResult(activity: BaseActivity, requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        var permGrantedBool = false
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                for (grantResult in grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        try {
                            activity.showToast(activity.getString(R.string.not_sufficient_permissions, activity.getString(R.string.app_name)), true)
                        } catch (e: Exception) {
                            Toast.makeText(activity, activity.getString(R.string.not_sufficient_permissions, activity.getString(R.string.app_name)), Toast.LENGTH_SHORT).show()
                        }

                        permGrantedBool = false
                        break
                    } else {
                        permGrantedBool = true
                    }
                }
                if (permGrantedBool) {
                    permissionCallback!!.onPermissionsGranted(reqCode)
                } else {
                    permissionCallback!!.onPermissionsDenied(reqCode)
                }
            }
        }
    }

    interface PermissionCallback {
        fun onPermissionsGranted(resultCode: Int)

        fun onPermissionsDenied(resultCode: Int)
    }

}
