package com.accu.attendance.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.accu.attendance.R
import com.accu.attendance.utils.Const
import kotlinx.android.synthetic.main.fg_settings.*

/**
 * Created by neeraj on 16/12/17.
 */
class SettingsFragment : BaseFragment() {

    private lateinit var hourArray: Array<String>
    private lateinit var minArray: Array<String>
    private lateinit var snoozeArray: Array<String>
    private var hour = 9
    private var min = 0
    private var snoozeMin = 10

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_settings, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(false, true)
        view!!.visibility = View.INVISIBLE
        initUI()
        Handler().postDelayed({ view.visibility = View.VISIBLE }, 200)
    }

    private fun initUI() {
        hourArray = arrayOf(resources.getStringArray(R.array.hours))[0]
        minArray = arrayOf(resources.getStringArray(R.array.mins))[0]
        snoozeArray = arrayOf(resources.getStringArray(R.array.snooze_mins))[0]

        hour = baseActivity.store.getInt(Const.Settings.REM_H, 9)
        min = baseActivity.store.getInt(Const.Settings.REM_M, 0)
        snoozeMin = baseActivity.store.getInt(Const.Settings.SNOOZE_M, 10)

        hSP.setSelection(hourArray.indexOf(hour.toString()))
        mSP.setSelection(minArray.indexOf(min.toString()))
        snoozeSP.setSelection(snoozeArray.indexOf(snoozeMin.toString()))
        notiSW.setOnCheckedChangeListener { _, isChecked ->
            hSP.isEnabled = isChecked
            mSP.isEnabled = isChecked
            snoozeSP.isEnabled = isChecked
            baseActivity.store.setBoolean(Const.Settings.IS_NOTI_ON, isChecked)
        }
        notiSW.isChecked = baseActivity.store.getBoolean(Const.Settings.IS_NOTI_ON, true)
        hSP.onItemSelectedListener = this
        mSP.onItemSelectedListener = this
        snoozeSP.onItemSelectedListener = this
        Handler().postDelayed({ saveLL.visibility = View.GONE }, 100)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        when (parent.id) {
            R.id.hSP -> hour = hourArray[position].toInt()
            R.id.mSP -> min = minArray[position].toInt()
            R.id.snoozeSP -> snoozeMin = snoozeArray[position].toInt()
        }
        enableEdit()
    }

    private fun enableEdit() {
        saveLL.visibility = View.VISIBLE
        saveTV.setOnClickListener {
            baseActivity.store.setInt(Const.Settings.REM_H, hour)
            baseActivity.store.setInt(Const.Settings.REM_M, min)
            baseActivity.store.setInt(Const.Settings.SNOOZE_M, snoozeMin)
            baseActivity.onBackPressed()
        }
    }
}