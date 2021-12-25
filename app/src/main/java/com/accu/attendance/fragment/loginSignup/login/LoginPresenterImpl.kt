package com.accu.attendance.fragment.loginSignup.login

import com.accu.attendance.data.UserData
import com.accu.attendance.retrofitManager.ApiManager
import com.accu.attendance.retrofitManager.ResponseListener
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call

/**
 * Created by neeraj on 23/7/18.
 */
class LoginPresenterImpl(private val apiManager: ApiManager) : LoginContract.PresenterInterector, ResponseListener {
    private lateinit var onLoginFinished: LoginContract.PresenterInterector.OnLoginFinished

    override fun doLogin(loginCall: Call<JsonObject>, onLoginFinished: LoginContract.PresenterInterector.OnLoginFinished) {
        apiManager.makeApiCall(loginCall, this)
        this.onLoginFinished = onLoginFinished
    }

    override fun onSuccess(call: Call<*>, `object`: Any) {
        val jsonObject = `object` as JsonObject
        val data = jsonObject.getAsJsonArray("data").get(0).asJsonObject
        val objectType = object : TypeToken<UserData>() {}.type
        val userData = Gson().fromJson<UserData>(data, objectType)
        onLoginFinished.onLoginSuccess(userData)
    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        onLoginFinished.onAnyError(call, statusCode, errorMessage, responseListener)
    }
}