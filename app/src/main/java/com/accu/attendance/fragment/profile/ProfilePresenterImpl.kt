package com.accu.attendance.fragment.profile

import com.accu.attendance.retrofitManager.ApiManager
import com.accu.attendance.retrofitManager.ResponseListener
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call

/**
 * Created by neeraj on 23/7/18.
 */
class ProfilePresenterImpl(private val apiManager: ApiManager) : ProfileContract.PresenterInterector, ResponseListener {

    private lateinit var onFinished: ProfileContract.PresenterInterector.OnFinished
    private lateinit var updateCall: Call<JsonObject>
    private lateinit var depttCall: Call<JsonArray>

    override fun setPresenterListener(onFinished: ProfileContract.PresenterInterector.OnFinished) {
        this.onFinished = onFinished
    }

    override fun getDeptt(call: Call<JsonArray>) {
        apiManager.makeApiCall(call, this)
        depttCall = call
    }

    override fun updateProfile(call: Call<JsonObject>) {
        apiManager.makeApiCall(call, this)
        updateCall = call
    }

    override fun onSuccess(call: Call<*>, `object`: Any) {
        if (call == depttCall) {
            val hMap = HashMap<String, String>()
            val depttArray = `object` as JsonArray
            val array = arrayOfNulls<String>(depttArray.size() + 1)
            var i = 1
            array[0] = "-- Select Deptt --"
            for (data in depttArray) {
                val obj = data.asJsonObject
                hMap.put(obj.get("name").asString, obj.get("ID").asString)
                array.set(i, obj.get("name").asString)
                i++
            }
            onFinished.onGetDepttSuccess(hMap, array)
        } else if (call == updateCall) {
            val jsonObject = `object` as JsonObject
            onFinished.onProfileUpdated(jsonObject)
        }
    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        onFinished.onAnyError(call, statusCode, errorMessage, responseListener)
    }
}