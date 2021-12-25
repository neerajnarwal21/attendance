package com.accu.attendance.fragment.loginSignup.signup

import com.accu.attendance.retrofitManager.ResponseListener
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call

/**
 * Created by neeraj on 23/7/18.
 */
class SignupPresenter(private val viewContract: SignupContract.ViewContract, private val signupPresenterImpl: SignupContract.PresenterInterector) :
        SignupContract.PresenterContract, SignupContract.PresenterInterector.OnFinished {

    init {
        signupPresenterImpl.setPresenterListener(this)
    }

    override fun onSignupClick(signupCall: Call<JsonObject>) {
        signupPresenterImpl.doSignup(signupCall)
    }

    override fun onGetDeptt(depttCall: Call<JsonArray>) {
        signupPresenterImpl.getDeptt(depttCall)
    }

    override fun onSignupSuccess(jsonObject: JsonObject) {
        viewContract.onPSignupSucess(jsonObject)
    }

    override fun onGetDepttSuccess(hMap: HashMap<String, String>, array: Array<String?>) {
        viewContract.onPGetDepttSuccess(hMap, array)
    }

    override fun onAnyError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        viewContract.onPError(call, statusCode, errorMessage, responseListener)
    }
}