package com.accu.attendance.fragment.loginSignup.login

import com.accu.attendance.data.UserData
import com.accu.attendance.retrofitManager.ResponseListener
import com.google.gson.JsonObject
import retrofit2.Call

/**
 * Created by neeraj on 23/7/18.
 */
interface LoginContract {
    interface PresenterContract {
        fun onLoginClick(loginCall: Call<JsonObject>)
    }

    interface PresenterInterector {
        interface OnLoginFinished {
            fun onLoginSuccess(userData: UserData)
            fun onAnyError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener)
        }

        fun doLogin(loginCall: Call<JsonObject>, onLoginFinished: OnLoginFinished)
    }

    interface ViewContract {
        fun onPSucess(userData: UserData)
        fun onPError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener)
    }
}