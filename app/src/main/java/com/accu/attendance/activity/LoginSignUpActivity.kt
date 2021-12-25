package com.accu.attendance.activity

import android.os.Bundle
import com.accu.attendance.R
import com.accu.attendance.fragment.loginSignup.login.LoginFragment
import com.accu.attendance.fragment.loginSignup.signup.SignUpFragment
import com.accu.attendance.utils.Utils

class LoginSignUpActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setBackgroundDrawableResource(R.mipmap.ic_bg)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ls)
        Utils.setStatusBarTranslucent(this,true)
        initUI()
    }

    private fun initUI() {
        if (intent.getStringExtra("goto").equals("login")) {
            gotoLogin()
        } else
            gotoSignUp()
    }

    private fun gotoLogin() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.frame_ls, LoginFragment())
                .commit()
    }

    private fun gotoSignUp() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.frame_ls, SignUpFragment())
                .commit()
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame_ls)
        if (frag is LoginFragment || frag is SignUpFragment) {
            gotoLoginHome()
        } else if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else
            gotoLoginHome()
    }

    private fun gotoLoginHome() {
        finish()
    }
}