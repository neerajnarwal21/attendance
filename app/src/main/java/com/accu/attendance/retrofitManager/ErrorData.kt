package com.accu.attendance.retrofitManager

import com.google.gson.annotations.SerializedName

/**
 * Created by neeraj on 15/6/17.
 */
data class ErrorData(@SerializedName("Message") var message: String? = null)
