package com.accu.attendance.fragment.loginSignup.signup

import com.accu.attendance.retrofitManager.ApiManager
import com.accu.attendance.retrofitManager.ResponseListener
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call

/**
 * Created by neeraj on 23/7/18.
 */
class SignupPresenterImpl(private val apiManager: ApiManager) : SignupContract.PresenterInterector, ResponseListener {

    private var signupCall: Call<JsonObject>? = null
    private var depttCall: Call<JsonArray>? = null
    private lateinit var onFinished: SignupContract.PresenterInterector.OnFinished

    override fun onSuccess(call: Call<*>, `object`: Any) {
        if (call == signupCall) {
            val jsonObject = `object` as JsonObject
            onFinished.onSignupSuccess(jsonObject)
        } else if (call == depttCall) {
            val hMap: HashMap<String, String> = hashMapOf()
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
        }
    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        onFinished.onAnyError(call, statusCode, errorMessage, responseListener)
    }

    override fun setPresenterListener(onFinished: SignupContract.PresenterInterector.OnFinished) {
        this.onFinished = onFinished
    }

    override fun doSignup(call: Call<JsonObject>) {
        apiManager.makeApiCall(call, this)
        signupCall = call
    }

    override fun getDeptt(call: Call<JsonArray>) {
        apiManager.makeApiCall(call, this)
        depttCall = call
    }
}