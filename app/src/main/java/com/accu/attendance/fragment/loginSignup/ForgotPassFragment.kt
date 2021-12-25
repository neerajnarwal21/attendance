package com.accu.attendance.fragment.loginSignup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.accu.attendance.R
import com.accu.attendance.fragment.BaseFragment
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fg_forgot_pass.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call

/**
 * Created by neeraj on 16/12/17.
 */
class ForgotPassFragment : BaseFragment() {
    private lateinit var forgotCall: Call<JsonObject>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_forgot_pass, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        submitTV.setOnClickListener {
            if (validate())
                submit()
        }
        backIV.setOnClickListener { baseActivity.onBackPressed() }
        backTV.setOnClickListener { baseActivity.onBackPressed() }
    }

    private fun validate(): Boolean {
        when {
            getText(emailET).isEmpty() -> showToast("Please enter Email", true)
            else -> return true
        }
        return false
    }

    private fun submit() {
        val email = RequestBody.create(MediaType.parse("text/plain"), getText(emailET))
        forgotCall = apiInterface.forgotPassword(email)
        apiManager.makeApiCall(forgotCall, this)
    }

    override fun onSuccess(call: Call<*>, `object`: Any) {
        super.onSuccess(call, `object`)
        if (call === forgotCall) {
            val obj = `object` as JsonObject
            try {
                showToast(obj.get("message").asString)
            } catch (e: Exception) {
                showToast("Please check your email for new Password")
            }
            baseActivity.onBackPressed()
        }
    }
}