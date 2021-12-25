package com.accu.attendance.retrofitManager

import com.accu.attendance.data.UserDayData
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.*

/**
 * Created by Neeraj Narwal on 5/5/17.
 */
interface ApiInterface {

    @Multipart
    @POST("api/login")
    fun login(@Part("email") email: RequestBody,
              @Part("password") password: RequestBody,
              @Part("token") deviceToken: RequestBody)
            : Call<JsonObject>

    @Multipart
    @POST("api/logout")
    fun logout(@Part("userid") userId: RequestBody)
            : Call<JsonObject>

    @Multipart
    @POST("api/signup")
    fun signUp(@Part("name") name: RequestBody,
               @Part("email") email: RequestBody,
               @Part("password") pass: RequestBody,
               @Part("empcode") empCode: RequestBody,
               @Part("phone") phone: RequestBody,
               @Part("deptt") deptt: RequestBody,
               @Part("token") token: RequestBody)
            : Call<JsonObject>

    @GET("api/department")
    fun depttList(): Call<JsonArray>

    @Multipart
    @POST("api/forget")
    fun forgotPassword(@Part("email") email: RequestBody)
            : Call<JsonObject>

    @Multipart
    @POST("api/update-profile")
    fun updateProfile(@Part("userid") userId: RequestBody,
                      @Part("name") name: RequestBody,
                      @Part("email") email: RequestBody,
                      @Part("phone") phone: RequestBody,
                      @Part("empcode") empCode: RequestBody,
                      @Part("deptt") deptt: RequestBody,
                      @Part imagePart: MultipartBody.Part?)
            : Call<JsonObject>

    @Multipart
    @POST("api/change-password")
    fun changePassword(@Part("userid") userId: RequestBody,
                       @Part("oldpassword") oldPass: RequestBody,
                       @Part("newpassword") newPass: RequestBody)
            : Call<JsonObject>

    @Multipart
    @POST("api/userdata")
    fun getMonthCalender(@Part("userid") userId: RequestBody,
                         @Part("start") startDate: RequestBody,
                         @Part("end") endDate: RequestBody)
            : Call<ArrayList<UserDayData>>

    @Multipart
    @POST("api/userstatus")
    fun getUserStatus(@Part("userid") userId: RequestBody,
                      @Part("token") token: RequestBody)
            : Call<JsonObject>

    @Multipart
    @POST("api/signin")
    fun signInDay(@Part("userid") userId: RequestBody,
                  @Part("SignInTime") time: RequestBody,
                  @Part("SignInLat") lat: RequestBody,
                  @Part("SignInLong") lng: RequestBody,
                  @Part("SignInNote") note: RequestBody,
                  @Part("SignInAddress") address: RequestBody)
            : Call<JsonObject>

    @Multipart
    @POST("api/signout")
    fun signOutDay(@Part("userid") userId: RequestBody,
                   @Part("signouttime") time: RequestBody,
                   @Part("signoutlatitude") lat: RequestBody,
                   @Part("signoutlongitude") lng: RequestBody,
                   @Part("signoutnote") note: RequestBody,
                   @Part("signoutaddress") address: RequestBody)
            : Call<JsonObject>

    // If API call is in type of Data object (RequestBody in general words) then send whole serialized model class in post

    //    @POST("/api/fuel/RegisterUser")
    //    Call<UserData> signup(@Body SignupData signupData);

}
