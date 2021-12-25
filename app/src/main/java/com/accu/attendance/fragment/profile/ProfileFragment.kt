package com.accu.attendance.fragment.profile

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import com.accu.attendance.R
import com.accu.attendance.activity.MainActivity
import com.accu.attendance.data.UserData
import com.accu.attendance.fragment.BaseFragment
import com.accu.attendance.retrofitManager.ResponseListener
import com.accu.attendance.utils.Const
import com.accu.attendance.utils.ImageUtils
import com.accu.attendance.utils.PermissionsManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fg_profile.*
import kotlinx.android.synthetic.main.inc_profile_header.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File

/**
 * Created by neeraj on 16/12/17.
 */
class ProfileFragment : BaseFragment(), PermissionsManager.PermissionCallback, ImageUtils.ImageSelectCallback, ProfileContract.ViewContract {

    private lateinit var file: File
    private var editEnable = false
    private var hMap: HashMap<String, String> = hashMapOf()
    private lateinit var presenter: ProfilePresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_profile, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(false, true)
        presenter = ProfilePresenter(this, ProfilePresenterImpl(apiManager))
        getDepttList()
    }

    private fun getDepttList() {
        val depttCall = apiInterface.depttList()
        presenter.onGetDeptt(depttCall)
    }

    private fun initUI(array: Array<String?>) {
        val userData = baseActivity.store.getUserData(Const.USER_DATA, UserData::class.java)
        baseActivity.picasso.load(Const.SERVER_REMOTE_URL + userData.photo).placeholder(R.mipmap.ic_default_user).into(picCIV)
        nameTV.text = userData.name
        nameET.setText(userData.name)
        phoneET.setText(userData.phone)
        emailET.setText(userData.email)
        empCodeET.setText(userData.empcode)

        nameIV.setOnClickListener(this)
        phoneIV.setOnClickListener(this)
        depttSP.isEnabled = false
        depttIV.setOnClickListener(this)
        empCodeIV.setOnClickListener(this)
        picCIV.setOnClickListener {
            if (PermissionsManager.checkPermissions(baseActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 123, this))
                ImageUtils.ImageSelectBuilder(baseActivity, this, 321).aspectRatio(2, 3).start()
        }
        saveTV.setOnClickListener { if (validate()) submitUpdates() }
        getAndSetSpinner(userData, array)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.nameIV -> nameET.enable()
            R.id.phoneIV -> phoneET.enable()
            R.id.empCodeIV -> empCodeET.enable()
            R.id.depttIV -> depttSP.isEnabled = true
        }
        v.visibility = View.GONE
        enableSaveChanges()
    }

    private fun EditText.enable() {
        this.isFocusableInTouchMode = true
        this.showKeyboard()
        this.setSelection(getText(this).length)
    }

    private fun View.showKeyboard() {
        this.requestFocus()
        try {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        } catch (ignored: Exception) {
        }
    }

    private fun getAndSetSpinner(userData: UserData?, array: Array<String?>) {
        for (i in 0..array.size) {
            if (userData!!.deptt == array[i]) {
                depttSP.setSelection(i)
                break
            }
        }
    }

    private fun enableSaveChanges() {
        editEnable = true
        saveLL.visibility = View.VISIBLE
    }

    fun getEditState() = editEnable

    private fun validate(): Boolean {
        when {
            getText(nameET).isEmpty() -> showToast("Please enter Name", true)
            getText(phoneET).isEmpty() -> showToast("Please enter Phone Number", true)
            depttSP.selectedItemPosition == 0 -> showToast("Please select Deptt", true)
            getText(empCodeET).isEmpty() -> showToast("Please enter Employee Code", true)
            else -> return true
        }
        return false
    }


    private fun submitUpdates() {
        val userId = RequestBody.create(MediaType.parse("text/plain"), baseActivity.store.getString(Const.USER_ID))
        val name = RequestBody.create(MediaType.parse("text/plain"), getText(nameET))
        val email = RequestBody.create(MediaType.parse("text/plain"), getText(emailET))
        val phone = RequestBody.create(MediaType.parse("text/plain"), getText(phoneET))
        val empCode = RequestBody.create(MediaType.parse("text/plain"), getText(empCodeET))
        val deptt = RequestBody.create(MediaType.parse("text/plain"), hMap[depttSP.selectedItem])
        var image: MultipartBody.Part? = null
        try {
            image = MultipartBody.Part.createFormData("pic", file.name, RequestBody.create(MediaType.parse("image/jpeg"), file))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val updateCall = apiInterface.updateProfile(userId, name, email, phone, empCode, deptt, image)
        presenter.onProfileUpdateClick(updateCall)
    }

    override fun onGetDepttSuccess(hMap: HashMap<String, String>, array: Array<String?>) {
        this.hMap = hMap
        loadSpinner(array)
        initUI(array)
    }

    override fun onProfileUpdated(jsonObject: JsonObject) {
        showToast(jsonObject.get("message").asString)
        val data = jsonObject.getAsJsonArray("data").get(0).asJsonObject
        val objectType = object : TypeToken<UserData>() {}.type
        val userData = Gson().fromJson<UserData>(data, objectType)
        baseActivity.store.saveUserData(Const.USER_DATA, userData)
        baseActivity.store.saveString(Const.USER_ID, userData.iD)
        (baseActivity as MainActivity).setUserData()
        editEnable = false
        baseActivity.onBackPressed()
    }

    override fun onPError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        super.onError(call, statusCode, errorMessage, responseListener)
    }

    private fun loadSpinner(array: Array<String?>) {
        val spinnerArrayAdapter = ArrayAdapter<String>(baseActivity, R.layout.adapter_simple_item_dark, array)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        depttSP.adapter = spinnerArrayAdapter
    }

    override fun onImageSelected(imagePath: String?, resultCode: Int) {
        val bitmap = ImageUtils.imageCompress(imagePath)
        picCIV.setImageBitmap(bitmap)
        file = ImageUtils.bitmapToFile(bitmap, baseActivity)
        log(file.length().toString())
        enableSaveChanges()
    }

    override fun onPermissionsGranted(resultCode: Int) {
        ImageUtils.ImageSelectBuilder(baseActivity, this, 321).aspectRatio(2, 3).start()
    }

    override fun onPermissionsDenied(resultCode: Int) {
    }
}