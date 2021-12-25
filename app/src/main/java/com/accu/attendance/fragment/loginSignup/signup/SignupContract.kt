package com.accu.attendance.fragment.loginSignup.signup

import com.accu.attendance.retrofitManager.ResponseListener
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call

/**
 * Created by neeraj on 23/7/18.
 */
interface SignupContract {
    interface PresenterContract {
        fun onSignupClick(signupCall: Call<JsonObject>)
        fun onGetDeptt(depttCall: Call<JsonArray>)
    }

    interface PresenterInterector {
        interface OnFinished {
            fun onSignupSuccess(jsonObject: JsonObject)
            fun onGetDepttSuccess(hMap: HashMap<String, String>, array: Array<String?>)
            fun onAnyError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener)
        }

        fun setPresenterListener(onFinished: OnFinished)
        fun doSignup(call: Call<JsonObject>)
        fun getDeptt(call: Call<JsonArray>)
    }

    interface ViewContract {
        fun onPSignupSucess(jsonObject: JsonObject)
        fun onPGetDepttSuccess(hMap: HashMap<String, String>, array: Array<String?>)
        fun onPError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener)
    }
}