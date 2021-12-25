package com.accu.attendance.retrofitManager

import android.content.Context
import android.util.MalformedJsonException
import com.accu.attendance.utils.Utils
import com.google.gson.JsonElement
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.*

/**
 * Created by Neeraj Narwal on 31/5/17.
 */
class ApiManager {

    internal val TAG = ApiManager::class.java.simpleName
    private val apiResponseHashMap = HashMap<Call<*>, ResponseListener>()

    //Constructor when Progress message need to update
    fun <T> makeApiCall(call: Call<T>, responseListener: ResponseListener, progressMessage: String?) {
        if (progressMessage != null && !progressMessage.isEmpty()) {
            progressDialog.updateMessage(progressMessage)
        }
        makeApiCall(call, responseListener)
    }

    @JvmOverloads
    fun <T> makeApiCall(call: Call<T>, responseListener: ResponseListener, showProgress: Boolean = true) {
        try {
            apiResponseHashMap[call] = responseListener

//            call.enqueue(this)
            if (showProgress) {
                progressDialog.startProgressDialog()
            }

            //Logs post URL
            log(call.request().url().toString())

            //Logs post params of Multipart request
            log("Post Params >>>> \n" + bodyToString(call.request().body())
                    .replace("\r", "")
                    .replace("--+[a-zA-Z0-9-/:=; ]+\\n".toRegex(), "")
                    .replace("Content+[a-zA-Z0-9-/:=; ]+;\\s".toRegex(), "")
                    .replace("Content+[a-zA-Z0-9-/:=; ]+\\n".toRegex(), "")
                    .replace("charset+[a-zA-Z0-9-/:=; ]+\\n".toRegex(), "")
                    .replace("name=", "")
                    .replace("\n\n", "--> ")
            )
            call.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    handleResponse(call, response)
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    handleFailure(call, t)
                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun log(string: String) {
        Utils.debugLog(TAG, string)
    }

    private fun bodyToString(request: RequestBody?): String {
        try {
            val buffer = Buffer()
            if (request != null)
                request.writeTo(buffer)
            else
                return ""
            return buffer.readUtf8()
        } catch (e: IOException) {
            return "did not work"
        }

    }

    fun <T> handleResponse(call: Call<T>, response: Response<T>) {
        progressDialog.stopProgressDialog()
        val responseListener = apiResponseHashMap[call]
//        try {
//            if (response.isSuccessful) {
//                val o = response.body()
//                log(o.toString())
//                responseListener?.onSuccess(call, o)
//            } else {
//                val parser = JsonParser()
//                val mJson = parser.parse(response.errorBody().string())
////                Gson gson = new Gson();
//                val errorResponse = Gson().fromJson(mJson, ErrorData::class.java)
//                log(errorResponse.message + "")
//                responseListener?.onError(call, response.code(), errorResponse.message, responseListener)
//            }
//        } catch (e: Exception) {
//        }

        //This handling is to handle a "success" object in response which tell its an error or not like OK/NOK
        //Do update this method if your handling is of different type if not then send response as it is
        var wasError = false
        try {
            val obj = response.body() as JsonElement
            log("Response >>>>>>>> $obj")
            if (obj.asJsonObject.has("success")
                    && obj.asJsonObject.get("success").asString == "0") {
                responseListener?.onError(call, response.code(), obj.asJsonObject.get("message").asString, responseListener)
                wasError = true
            } else if (!response.isSuccessful) {
                responseListener?.onError(call, response.code(), "Internal Server Error", responseListener)
                wasError = true
            }
        } catch (ignored: Exception) {
        }

        try {
            if (!wasError) {
                responseListener?.onSuccess(call, response.body()!!)
                //        responseListener.onFinish();
            }
        } catch (ignored: Exception) {
        }
        apiResponseHashMap.remove(call)
    }

    fun <T> handleFailure(call: Call<T>, t: Throwable) {
        progressDialog.stopProgressDialog()
        val responseListener = apiResponseHashMap[call]
        try {
            when {
                t.javaClass == SocketTimeoutException::class.java ->
                    responseListener?.onError(call, 500, "socketTimeout", responseListener)
                t.javaClass == MalformedJsonException::class.java ->
                    responseListener?.onError(call, 500, "Internal server error", responseListener)
                else ->
                    responseListener?.onError(call, 500, t.message!!, responseListener)
            }
        } catch (ignored: Exception) {
        }
        //        responseListener.onFinish();
        apiResponseHashMap.remove(call)
    }

    companion object {
        var apiManager: ApiManager? = null
        private lateinit var progressDialog: ProgressDialog
        fun getInstance(context: Context): ApiManager {
            if (apiManager == null)
                apiManager = ApiManager()
            progressDialog = ProgressDialog.getInstance(context)
            progressDialog.initiateProgressDialog()
            return apiManager!!
        }
    }

//    fun <T> callback2(success: ((call: Call<T>, Response<T>) -> Unit)?, failure: ((call: Call<T>, t: Throwable) -> Unit)? = null): Callback<T> {
//        return object : Callback<T> {
//            override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) {
//                success?.invoke(call, response)
//            }
//
//            override fun onFailure(call: Call<T>, t: Throwable) {
//                failure?.invoke(call, t)
//            }
//        }
//    }
}
