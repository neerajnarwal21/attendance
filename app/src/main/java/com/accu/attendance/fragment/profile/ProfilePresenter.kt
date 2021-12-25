package com.accu.attendance.fragment.profile

import com.accu.attendance.retrofitManager.ResponseListener
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call

/**
 * Created by neeraj on 23/7/18.
 */
class ProfilePresenter(private val viewContract: ProfileContract.ViewContract
                       , private val presenterImpl: ProfileContract.PresenterInterector) :
        ProfileContract.PresenterContract
        , ProfileContract.PresenterInterector.OnFinished {

    init {
        presenterImpl.setPresenterListener(this)
    }

    override fun onGetDeptt(call: Call<JsonArray>) {
        presenterImpl.getDeptt(call)
    }

    override fun onProfileUpdateClick(call: Call<JsonObject>) {
        presenterImpl.updateProfile(call)
    }

    override fun onGetDepttSuccess(hMap: HashMap<String, String>, array: Array<String?>) {
        viewContract.onGetDepttSuccess(hMap, array)
    }

    override fun onProfileUpdated(jsonObject: JsonObject) {
        viewContract.onProfileUpdated(jsonObject)
    }

    override fun onAnyError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        viewContract.onPError(call, statusCode, errorMessage, responseListener)
    }
}