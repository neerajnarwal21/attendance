package com.accu.attendance.fragment.loginSignup.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.accu.attendance.R
import com.accu.attendance.activity.MainActivity
import com.accu.attendance.data.UserData
import com.accu.attendance.fragment.BaseFragment
import com.accu.attendance.fragment.loginSignup.ForgotPassFragment
import com.accu.attendance.fragment.loginSignup.signup.SignUpFragment
import com.accu.attendance.retrofitManager.ResponseListener
import com.accu.attendance.utils.Const
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fg_login.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call

/**
 * Created by neeraj on 16/12/17.
 */
class LoginFragment : BaseFragment(), LoginContract.ViewContract {


    private lateinit var loginCall: Call<JsonObject>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_login, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        if (arguments != null && arguments!!.getString("title") != null) {
            emailET.setText(arguments!!.getString("title"))
            passET.setText(arguments!!.getString("pass"))
        }
        if (baseActivity.store.getString(Const.USER_SAVED_EMAIL) != null) {
            emailET.setText(baseActivity.store.getString(Const.USER_SAVED_EMAIL))
            passET.setText(baseActivity.store.getString(Const.USER_SAVED_PASS))
            remMeCB.isChecked = true
        }
//        if (BuildConfig.DEBUG) {
//            emailET.setText("test@test.com")
//            passET.setText("admin123")
//        }
    }

    private fun initUI() {
        logInTV.setOnClickListener {
            if (validate())
                login()
        }
        forgotTV.setOnClickListener { gotoForgotPass() }
        signUpTV.setOnClickListener { gotoSignUp() }
        backIV.setOnClickListener { baseActivity.onBackPressed() }
        backTV.setOnClickListener { baseActivity.onBackPressed() }
    }

    private fun gotoForgotPass() {
        baseActivity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_ls, ForgotPassFragment())
                .addToBackStack(null)
                .commit()
    }

    private fun gotoSignUp() {
        baseActivity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_ls, SignUpFragment())
                .commit()
    }

    private fun validate(): Boolean {
        when {
            getText(emailET).isEmpty() -> showToast("Please enter Email", true)
            getText(passET).isEmpty() -> showToast("Please enter Password", true)
            else -> return true
        }
        return false
    }

    private fun login() {
        val email = RequestBody.create(MediaType.parse("text/plain"), getText(emailET))
        val pass = RequestBody.create(MediaType.parse("text/plain"), getText(passET))
        val token = RequestBody.create(MediaType.parse("text/plain"), baseActivity.store.getString(Const.DEVICE_TOKEN, "tokenn"))
        loginCall = apiInterface.login(email, pass, token)
//        apiManager.makeApiCall(loginCall, this)
        val presenter = LoginPresenter(this, LoginPresenterImpl(apiManager))
        presenter.onLoginClick(loginCall)
    }

    override fun onPSucess(userData: UserData) {
        baseActivity.store.saveUserData(Const.USER_DATA, userData)
        baseActivity.store.saveString(Const.USER_ID, userData.iD)
        gotoHome()
        if (remMeCB.isChecked) {
            baseActivity.store.saveString(Const.USER_SAVED_EMAIL, getText(emailET))
            baseActivity.store.saveString(Const.USER_SAVED_PASS, getText(passET))
        } else {
            baseActivity.store.saveString(Const.USER_SAVED_EMAIL, null)
            baseActivity.store.saveString(Const.USER_SAVED_PASS, null)
        }
    }

    override fun onPError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        super.onError(call, statusCode, errorMessage, responseListener)
    }

    private fun gotoHome() {
        startActivity(Intent(baseActivity, MainActivity::class.java))
        baseActivity.finish()
        baseActivity.finishAffinity()
    }
}