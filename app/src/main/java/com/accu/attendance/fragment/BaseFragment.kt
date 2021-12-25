package com.accu.attendance.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.TextView

import com.accu.attendance.activity.BaseActivity
import com.accu.attendance.activity.MainActivity
import com.accu.attendance.retrofitManager.ApiClient
import com.accu.attendance.retrofitManager.ApiInterface
import com.accu.attendance.retrofitManager.ApiManager
import com.accu.attendance.retrofitManager.ResponseListener

import retrofit2.Call


/**
 * Created by Neeraj Narwal on 2/5/17.
 */
open class BaseFragment : Fragment(), AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener, ResponseListener {

    lateinit var baseActivity: BaseActivity
    lateinit var apiInterface: ApiInterface
    lateinit var apiManager: ApiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseActivity = activity as BaseActivity
        baseActivity.hideSoftKeyboard()
        apiManager = ApiManager.getInstance(baseActivity)
        apiInterface = ApiClient.client.create(ApiInterface::class.java)
    }

    fun setToolbar(showDrawer: Boolean, show: Boolean) {
        (baseActivity as MainActivity).setToolbar(showDrawer, show)
    }

    fun getText(view: TextView): String {
        return view.text.toString().trim { it <= ' ' }
    }

    override fun onResume() {
        super.onResume()
        activity!!.invalidateOptionsMenu()
    }

    fun showToast(msg: String, isError: Boolean = false) {
        baseActivity.showToast(msg, isError)
    }

    fun log(s: String) {
        baseActivity.log(s)
    }

    override fun onClick(v: View) {}

    override fun onItemClick(parent: AdapterView<*>, view: View?, position: Int, id: Long) {}

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {}

    override fun onNothingSelected(parent: AdapterView<*>) {}

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {}

    override fun onSuccess(call: Call<*>, `object`: Any) {}

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        baseActivity.onError(call, statusCode, errorMessage, responseListener)
    }
}
