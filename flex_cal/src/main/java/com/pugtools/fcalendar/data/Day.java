package com.pugtools.fcalendar.data;

public class Day {
    private int mYear;
    private int mMonth;
    private int mDay;
    private DayType dayType;

    public Day(int year, int month, int day) {
        this.mYear = year;
        this.mMonth = month;
        this.mDay = day;
    }

    public Day(int year, int month, int day, DayType dayType) {
        this.mYear = year;
        this.mMonth = month;
        this.mDay = day;
        this.dayType = dayType;
    }

    public DayType getDayType() {
        return dayType;
    }

    public void setDayType(DayType dayType) {
        this.dayType = dayType;
    }

    public int getMonth() {
        return mMonth;
    }

    public int getYear() {
        return mYear;
    }

    public int getDay() {
        return mDay;
    }

    public enum DayType {
        FULL, HALF, SHORT, ABSENT
    }

}
