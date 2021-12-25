package com.accu.attendance.fragment.myCalendar

import com.accu.attendance.data.UserDayData
import com.accu.attendance.retrofitManager.ResponseListener
import com.pugtools.fcalendar.data.Day
import retrofit2.Call

/**
 * Created by neeraj on 23/7/18.
 */
class CalendarPresenter(private val viewContract: CalendarContract.ViewContract
                        , private val presenterImpl: CalendarContract.PresenterInterector) :
        CalendarContract.PresenterContract
        , CalendarContract.PresenterInterector.OnFinished {

    init {
        presenterImpl.setPresenterListener(this)
    }

    override fun onMonthGetClick(call: Call<ArrayList<UserDayData>>) {
        presenterImpl.getCalender(call)
    }

    override fun onDayClick(day: Int) {
        presenterImpl.openDay(day)
    }

    override fun onCalenderGetSuccess(dayTypeHashMap: HashMap<Int, Day.DayType>, leavesList: ArrayList<UserDayData>) {
        viewContract.onPSucess(dayTypeHashMap, leavesList)
    }

    override fun onCalenderError() {
        viewContract.onCalenderGetError()
    }

    override fun onDayOpenSuccess(data: UserDayData) {
        viewContract.onPDayOpenSuccess(data)
    }

    override fun onAnyError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        viewContract.onPError(call, statusCode, errorMessage, responseListener)
    }
}