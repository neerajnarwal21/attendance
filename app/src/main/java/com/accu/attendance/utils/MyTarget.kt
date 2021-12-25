package com.accu.attendance.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.ImageView
import com.accu.attendance.activity.BaseActivity
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

/**
 * Created by neeraj on 1/5/18.
 */
class MyTarget(activity: BaseActivity, path: String, dialog: AlertDialog, locIV: ImageView) : Target {
    var dialog: AlertDialog
    var locIV: ImageView
    val handler: Handler
    val reloadTask: Runnable
    val act: BaseActivity

    init {
        this.act = activity
        this.dialog = dialog
        this.locIV = locIV
        reloadTask = Runnable {
            Log.e("Task", "Task doing its job now")
            activity.picasso.cancelRequest(this@MyTarget)
            activity.picasso.load(path).into(locIV)
            try {
                if (!this.dialog.isShowing)
                    this.dialog.show()
            } catch (ignored: Exception) {
            }
        }
        handler = Handler()
        handler.postDelayed(reloadTask, 5000)
    }

    override fun onBitmapFailed(errorDrawable: Drawable?) {
//        act.showToast("Bitmap was failed", false)
        dialog.show()
        handler.removeCallbacks(reloadTask)
    }

    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
//        act.showToast("Bitmap was loaded", false)
        locIV.setImageBitmap(bitmap)
        dialog.show()
        handler.removeCallbacks(reloadTask)
    }

    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        Log.e("Task", "Bitmap was prepared")
    }
}