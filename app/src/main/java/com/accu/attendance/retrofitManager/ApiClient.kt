package com.accu.attendance.retrofitManager


import com.accu.attendance.BuildConfig
import com.accu.attendance.utils.Const
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Neeraj Narwal on 5/5/17.
 */
object ApiClient {

    /*
           Creates client for retrofit, here you can configure different settings of retrofit manager
          like Logging, Cache size, Decoding factories, Convertor factories etc.
         */
    val client: Retrofit
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            val client = OkHttpClient.Builder().addInterceptor(interceptor)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .build()

            return Retrofit.Builder()
                    .baseUrl(Const.SERVER_REMOTE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
        }
}
