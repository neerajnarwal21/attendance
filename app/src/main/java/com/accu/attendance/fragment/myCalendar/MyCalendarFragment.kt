package com.accu.attendance.fragment.myCalendar

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.accu.attendance.R
import com.accu.attendance.adapter.LeavesAdapter
import com.accu.attendance.data.UserDayData
import com.accu.attendance.fragment.BaseFragment
import com.accu.attendance.retrofitManager.ResponseListener
import com.accu.attendance.utils.Const
import com.accu.attendance.utils.Utils
import com.pugtools.fcalendar.data.CalendarAdapter
import com.pugtools.fcalendar.data.Day
import com.pugtools.fcalendar.widget.DayClose
import com.pugtools.fcalendar.widget.DayTypeGet
import com.pugtools.fcalendar.widget.FlexibleCalendar
import kotlinx.android.synthetic.main.fg_my_calender.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by neeraj on 16/12/17.
 */
class MyCalendarFragment : BaseFragment(), CalendarContract.ViewContract {

    private lateinit var monthDataCall: Call<ArrayList<UserDayData>>
    private lateinit var cal: Calendar
    private var dayTypeGet: DayTypeGet? = null
    private var dayClose: DayClose? = null
    private var isShowing = false
    private var message: String = ""
    private lateinit var presenter: CalendarPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null && arguments!!.containsKey("message")) {
            message = arguments!!.getString("message")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_my_calender, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(false, true)
        presenter = CalendarPresenter(this, CalendarPresenterImpl(apiManager))
        initUI()
        notiMsgV.text = message
    }

    private fun initUI() {
        listRV.layoutManager = LinearLayoutManager(baseActivity)
        cal = Calendar.getInstance()
        getMonthData(cal, null)
    }

    private fun getMonthData(currentCal: Calendar, dayTypeGet: DayTypeGet?) {
        this.dayTypeGet = dayTypeGet
        val firstDayCal = currentCal.clone() as Calendar
        val lastDayCal = currentCal.clone() as Calendar
        firstDayCal.set(Calendar.DAY_OF_MONTH, firstDayCal.getActualMinimum(Calendar.DAY_OF_MONTH))
        lastDayCal.set(Calendar.DAY_OF_MONTH, lastDayCal.getActualMaximum(Calendar.DAY_OF_MONTH))

        val userId = RequestBody.create(MediaType.parse("text/plain"), baseActivity.store.getString(Const.USER_ID))
        val startDate = RequestBody.create(MediaType.parse("text/plain"), SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(firstDayCal.time))
        val endDate = RequestBody.create(MediaType.parse("text/plain"), SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(lastDayCal.time))

        monthDataCall = apiInterface.getMonthCalender(userId, startDate, endDate)
        presenter.onMonthGetClick(monthDataCall)

    }

    override fun onPSucess(dayTypeHashMap: HashMap<Int, Day.DayType>, leavesList: ArrayList<UserDayData>) {
        if (dayTypeGet == null) {
            val adapter = CalendarAdapter(baseActivity, cal, dayTypeHashMap)
            flexCal.setAdapter(adapter, dayTypeHashMap)
            setFlexCalenderListener()
        } else
            dayTypeGet!!.onDayMapFilled(dayTypeHashMap)
        listRV.adapter = LeavesAdapter(context!!, leavesList)
    }

    override fun onCalenderGetError() {
        showToast("Error in getting Calendar")
        baseActivity.onBackPressed()
    }

    override fun onPDayOpenSuccess(data: UserDayData) {
        openView(data)
    }

    override fun onPError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        super.onError(call, statusCode, errorMessage, responseListener)
    }

    private fun setFlexCalenderListener() {
        flexCal.setCalendarListener(object : FlexibleCalendar.CalendarListener {

            override fun onMonthChange(currentCal: Calendar, dayTypeGet: DayTypeGet) {
                getMonthData(currentCal, dayTypeGet)
                if (isShowing) {
                    closeIV.callOnClick()
                }
            }

            override fun onDaySelect() {

            }

            override fun onItemClick(calView: View, day: Int, dayClose: DayClose) {
                this@MyCalendarFragment.dayClose = dayClose
                presenter.onDayClick(day)
            }

            override fun onDataUpdate() {

            }

            override fun onWeekChange(position: Int) {

            }
        })
    }

    private fun openView(data: UserDayData) {
        if (!isShowing) {
            dateRL.visibility = View.VISIBLE
            listRV.visibility = View.GONE
            isShowing = true
        }
        dateTV.text = Utils.changeDateFormat(data.date, "yyyy-MM-dd", "dd")
        if (data.signIn != null && !data.signIn!!.isEmpty()) {
            dayInfoTV.text = Utils.changeDateFormat(data.signIn, "yyyy-MM-dd HH:mm:ss", "hh:mm a")
            if (data.signout != null && !data.signout!!.isEmpty()) {
                dayInfoTV.append(" - ${Utils.changeDateFormat(data.signout, "yyyy-MM-dd HH:mm:ss", "hh:mm a")}" +
                        if (Utils.changeDateFormat(data.signIn, "yyyy-MM-dd HH:mm:ss", "dd").toInt()
                                != Utils.changeDateFormat(data.signout, "yyyy-MM-dd HH:mm:ss", "dd").toInt()) {
                            " (" + Utils.changeDateFormat(data.signout, "yyyy-MM-dd HH:mm:ss", "dd MMM") + ")"
                        } else {
                            ""
                        })
            }
        } else {
            dayInfoTV.text = "Day Off"
        }
        closeIV.setOnClickListener {
            dateRL.visibility = View.GONE
            listRV.visibility = View.VISIBLE
            dayClose?.onDayClose()
            isShowing = false
        }
    }
}