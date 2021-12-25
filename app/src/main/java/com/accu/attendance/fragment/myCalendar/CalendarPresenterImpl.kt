package com.accu.attendance.fragment.myCalendar

import com.accu.attendance.data.UserDayData
import com.accu.attendance.retrofitManager.ApiManager
import com.accu.attendance.retrofitManager.ResponseListener
import com.accu.attendance.utils.Const
import com.accu.attendance.utils.Utils
import com.pugtools.fcalendar.data.Day
import retrofit2.Call

/**
 * Created by neeraj on 23/7/18.
 */
class CalendarPresenterImpl(private val apiManager: ApiManager) : CalendarContract.PresenterInterector, ResponseListener {

    private lateinit var onFinished: CalendarContract.PresenterInterector.OnFinished
    private var dayDatasList = ArrayList<UserDayData>()

    override fun setPresenterListener(onFinished: CalendarContract.PresenterInterector.OnFinished) {
        this.onFinished = onFinished
    }

    override fun getCalender(call: Call<ArrayList<UserDayData>>) {
        apiManager.makeApiCall(call, this)
    }

    override fun openDay(day: Int) {
        for (data in dayDatasList) {
            if (Utils.changeDateFormat(data.date, "yyyy-MM-dd", "dd").toInt() == day) {
                onFinished.onDayOpenSuccess(data)
                break
            }
        }
    }

    override fun onSuccess(call: Call<*>, `object`: Any) {
        try {
            dayDatasList.clear()
            dayDatasList = `object` as ArrayList<UserDayData>
            onFinished.onCalenderGetSuccess(createHashMap(), createLeavesList())
        } catch (e: Exception) {
            onFinished.onCalenderError()
        }
    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        onFinished.onAnyError(call, statusCode, errorMessage, responseListener)
    }

    private fun createLeavesList(): ArrayList<UserDayData> {
        val list = ArrayList<UserDayData>()
        for (data in dayDatasList) {
            if (data.status == Const.DayStats.HALF_DAY
                    || data.status == Const.DayStats.SHORT_LEAVE) {
                list.add(data)
            }
        }
        return list
    }

    private fun createHashMap(): HashMap<Int, Day.DayType> {
        val dayTypeHashMap = hashMapOf<Int, Day.DayType>()
        for (data in dayDatasList) {
            if (data.status == Const.DayStats.FULL_DAY)
                dayTypeHashMap.put(Utils.changeDateFormat(data.date, "yyyy-MM-dd", "dd").toInt(), Day.DayType.FULL)
            else if (data.status == Const.DayStats.HALF_DAY)
                dayTypeHashMap.put(Utils.changeDateFormat(data.date, "yyyy-MM-dd", "dd").toInt(), Day.DayType.HALF)
            else if (data.status == Const.DayStats.SHORT_LEAVE)
                dayTypeHashMap.put(Utils.changeDateFormat(data.date, "yyyy-MM-dd", "dd").toInt(), Day.DayType.SHORT)
            else if (data.status == Const.DayStats.ABSENT)
                dayTypeHashMap.put(Utils.changeDateFormat(data.date, "yyyy-MM-dd", "dd").toInt(), Day.DayType.ABSENT)
        }
        return dayTypeHashMap
    }
}