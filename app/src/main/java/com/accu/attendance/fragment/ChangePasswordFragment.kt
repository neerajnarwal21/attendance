package com.accu.attendance.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.accu.attendance.R
import com.accu.attendance.data.UserData
import com.accu.attendance.utils.Const
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fg_change_password.*
import kotlinx.android.synthetic.main.inc_profile_header.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call

/**
 * Created by neeraj on 16/12/17.
 */
class ChangePasswordFragment : BaseFragment() {

    private lateinit var updateCall: Call<JsonObject>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_change_password, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(false, true)
        initUI()
    }

    private fun initUI() {
        val userData = baseActivity.store.getUserData(Const.USER_DATA, UserData::class.java)
        baseActivity.picasso.load(Const.SERVER_REMOTE_URL + userData.photo).placeholder(R.mipmap.ic_default_user).into(picCIV)
        nameTV.text = userData.name

        saveTV.setOnClickListener {
            if (validate()) {
                submitUpdates()
            }
        }
    }

    private fun validate(): Boolean {
        when {
            getText(oldPassET).isEmpty() -> showToast("Please enter Old Password", true)
            getText(newPassET).isEmpty() -> showToast("Please enter New Password", true)
            getText(retypePassET).isEmpty() -> showToast("Please enter New Password", true)
            getText(newPassET) != getText(retypePassET) -> showToast("New Password and Confirm Password doesn't match", true)
            else -> return true
        }
        return false
    }

    private fun submitUpdates() {
        val userId = RequestBody.create(MediaType.parse("text/plain"), baseActivity.store.getString(Const.USER_ID))
        val oldPass = RequestBody.create(MediaType.parse("text/plain"), getText(oldPassET))
        val newPass = RequestBody.create(MediaType.parse("text/plain"), getText(newPassET))

        updateCall = apiInterface.changePassword(userId, oldPass, newPass)
        apiManager.makeApiCall(updateCall, this)
    }

    override fun onSuccess(call: Call<*>, `object`: Any) {
        super.onSuccess(call, `object`)
        if (call == updateCall) {
            val jsonObject = `object` as JsonObject
            showToast(jsonObject.get("message").asString)
            baseActivity.onBackPressed()
        }
    }
}