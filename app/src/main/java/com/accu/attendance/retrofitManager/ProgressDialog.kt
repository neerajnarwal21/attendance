package com.accu.attendance.retrofitManager

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.TextView

import com.accu.attendance.R


/**
 * Created by neeraj on 27/7/17.
 */
class ProgressDialog {
    private var progressDialog: Dialog? = null
    private var txtMsgTV: TextView? = null

    fun initiateProgressDialog() {
        progressDialog = Dialog(context!!)
        val view = View.inflate(context, R.layout.progress_dialog, null)
        progressDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog!!.setContentView(view)
        progressDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        txtMsgTV = view.findViewById(R.id.txtMsgTV)
        progressDialog!!.setCancelable(false)
    }

    fun startProgressDialog() {
        if (progressDialog != null && !progressDialog!!.isShowing) {
            try {
                progressDialog!!.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun stopProgressDialog() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            try {
                progressDialog!!.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun updateMessage(message: String) {
        txtMsgTV!!.text = message
    }

    companion object {

        private var context: Context? = null
        private var instace: ProgressDialog? = null

        fun getInstance(context: Context): ProgressDialog {
            ProgressDialog.context = context
            if (instace == null) {
                instace = ProgressDialog()
            }
            return instace!!
        }
    }
}
