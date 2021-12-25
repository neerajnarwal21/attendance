package com.pugtools.fcalendar.widget;

import com.pugtools.fcalendar.data.Day;

import java.util.HashMap;

/**
 * Created by neeraj on 9/1/18.
 */

public interface DayTypeGet {
    void onDayMapFilled(HashMap<Integer, Day.DayType> dayTypeHashMap);
}
