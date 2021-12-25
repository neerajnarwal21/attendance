package com.accu.attendance.fragment.profile

import com.accu.attendance.retrofitManager.ResponseListener
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call

/**
 * Created by neeraj on 23/7/18.
 */
interface ProfileContract {
    interface PresenterContract {
        fun onGetDeptt(call: Call<JsonArray>)
        fun onProfileUpdateClick(call: Call<JsonObject>)
    }

    interface PresenterInterector {
        interface OnFinished {
            fun onGetDepttSuccess(hMap: HashMap<String, String>, array: Array<String?>)
            fun onProfileUpdated(jsonObject: JsonObject)
            fun onAnyError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener)
        }

        fun setPresenterListener(onFinished: OnFinished)
        fun getDeptt(call: Call<JsonArray>)
        fun updateProfile(call: Call<JsonObject>)
    }

    interface ViewContract {
        fun onGetDepttSuccess(hMap: HashMap<String, String>, array: Array<String?>)
        fun onProfileUpdated(jsonObject: JsonObject)
        fun onPError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener)
    }
}