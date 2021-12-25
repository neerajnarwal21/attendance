package com.accu.attendance.fragment.myCalendar

import com.accu.attendance.data.UserDayData
import com.accu.attendance.retrofitManager.ResponseListener
import com.pugtools.fcalendar.data.Day
import retrofit2.Call

/**
 * Created by neeraj on 23/7/18.
 */
interface CalendarContract {
    interface PresenterContract {
        fun onMonthGetClick(call: Call<ArrayList<UserDayData>>)
        fun onDayClick(day: Int)
    }

    interface PresenterInterector {
        interface OnFinished {
            fun onCalenderGetSuccess(dayTypeHashMap: HashMap<Int, Day.DayType>, leavesList: ArrayList<UserDayData>)
            fun onCalenderError()
            fun onAnyError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener)
            fun onDayOpenSuccess(data: UserDayData)
        }

        fun setPresenterListener(onFinished: OnFinished)
        fun getCalender(call: Call<ArrayList<UserDayData>>)
        fun openDay(day: Int)
    }

    interface ViewContract {
        fun onPSucess(dayTypeHashMap: HashMap<Int, Day.DayType>, leavesList: ArrayList<UserDayData>)
        fun onCalenderGetError()
        fun onPError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener)
        fun onPDayOpenSuccess(data: UserDayData)
    }
}