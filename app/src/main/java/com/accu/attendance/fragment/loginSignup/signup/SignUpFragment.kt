package com.accu.attendance.fragment.loginSignup.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.accu.attendance.R
import com.accu.attendance.fragment.BaseFragment
import com.accu.attendance.fragment.loginSignup.login.LoginFragment
import com.accu.attendance.retrofitManager.ResponseListener
import com.accu.attendance.utils.Const
import com.accu.attendance.utils.Utils
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fg_signup.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call


/**
 * Created by neeraj on 16/12/17.
 */
class SignUpFragment : BaseFragment(), SignupContract.ViewContract {
    private var signUpCall: Call<JsonObject>? = null
    private var depttCall: Call<JsonArray>? = null
    private var hMap: HashMap<String, String> = hashMapOf()
    lateinit var array: Array<String?>
    lateinit var presenter: SignupPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_signup, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = SignupPresenter(this, SignupPresenterImpl(apiManager))
        initUI()
    }

    private fun initUI() {
        signUpTV.setOnClickListener {
            if (validate())
                signUp()
        }
        backIV.setOnClickListener { baseActivity.onBackPressed() }
        backTV.setOnClickListener { baseActivity.onBackPressed() }
        logInTV.setOnClickListener { gotoLogin(null, null) }
        getDepttList()
    }

    private fun getDepttList() {
        depttCall = apiInterface.depttList()
        presenter.onGetDeptt(depttCall!!)
    }

    private fun gotoLogin(email: String?, pass: String?) {
        val frag = LoginFragment()
        val bundle = Bundle()
        bundle.putString("title", email)
        bundle.putString("pass", pass)
        frag.arguments = bundle
        baseActivity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_ls, frag)
                .commit()
    }

    private fun loadSpinner() {
        val spinnerArrayAdapter = ArrayAdapter<String>(baseActivity, R.layout.adapter_simple_item,
                array) //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item)
        depttSP.adapter = spinnerArrayAdapter
    }

    private fun validate(): Boolean {
        when {
            getText(nameET).isEmpty() -> showToast("Please enter Email", true)
            getText(phoneET).isEmpty() -> showToast("Please enter Phone Number", true)
            getText(empCodeET).isEmpty() -> showToast("Please enter Employee Code", true)
            depttSP.selectedItem == 0 -> showToast("Please select Deptt", true)
            getText(emailET).isEmpty() -> showToast("Please enter Email", true)
            !Utils.isValidMail(getText(emailET)) -> showToast("Please enter valid Email", true)
            getText(passET).isEmpty() -> showToast("Please enter Password", true)
            getText(passET) != getText(conPassET) -> showToast("Passwords doesn't match", true)
            else -> return true
        }
        return false
    }

    private fun signUp() {
        val name = RequestBody.create(MediaType.parse("text/plain"), getText(nameET))
        val email = RequestBody.create(MediaType.parse("text/plain"), getText(emailET))
        val pass = RequestBody.create(MediaType.parse("text/plain"), getText(passET))
        val empCode = RequestBody.create(MediaType.parse("text/plain"), getText(empCodeET))
        val phone = RequestBody.create(MediaType.parse("text/plain"), getText(phoneET))
        val deptt = RequestBody.create(MediaType.parse("text/plain"), hMap[depttSP.selectedItem])
        val token = RequestBody.create(MediaType.parse("text/plain"), baseActivity.store.getString(Const.DEVICE_TOKEN))

        signUpCall = apiInterface.signUp(name, email, pass, empCode, phone, deptt, token)
        presenter.onSignupClick(signUpCall!!)
    }

    override fun onPSignupSucess(jsonObject: JsonObject) {
        showToast(jsonObject.get("message").asString)
        gotoLogin(getText(emailET), getText(passET))
    }

    override fun onPGetDepttSuccess(hMap: HashMap<String, String>, array: Array<String?>) {
        this.hMap = hMap
        this.array = array
        loadSpinner()
    }

    override fun onPError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        super.onError(call, statusCode, errorMessage, responseListener)
    }
}