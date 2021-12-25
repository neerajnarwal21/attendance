package com.accu.attendance.fragment.loginSignup.login

import com.accu.attendance.data.UserData
import com.accu.attendance.retrofitManager.ResponseListener
import com.google.gson.JsonObject
import retrofit2.Call

/**
 * Created by neeraj on 23/7/18.
 */
class LoginPresenter(private val viewContract: LoginContract.ViewContract
                     , private val loginPresenterImpl: LoginContract.PresenterInterector) :
        LoginContract.PresenterContract
        , LoginContract.PresenterInterector.OnLoginFinished {
    override fun onLoginClick(loginCall: Call<JsonObject>) {
        loginPresenterImpl.doLogin(loginCall, this)
    }

    override fun onLoginSuccess(userData: UserData) {
        viewContract.onPSucess(userData)
    }

    override fun onAnyError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        viewContract.onPError(call, statusCode, errorMessage, responseListener)
    }
}