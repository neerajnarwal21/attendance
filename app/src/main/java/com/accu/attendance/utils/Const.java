package com.accu.attendance.utils;

public class Const {

    //    public static final String SERVER_REMOTE_URL = "http://demo2.pugmarks.in/pugmarksattendence/";      //New App
    public static final String SERVER_REMOTE_URL = "http://hrd.pugmarks.co.in/";      //Live
    //    public static final String SERVER_REMOTE_URL = "http://103.35.120.95/pugmarksattendence/";    //Dev
    public static final String DISPLAY_MESSAGE_ACTION = "com.packagename.DISPLAY_MESSAGE";
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1234;

    public static final String NOTI_CHANNEL_ID = "my_custom_channel";
    public static final String THOUGHT_OF_DAY = "thought_of_day";
    public static final String CURRENT_DATE = "current_date";
    public static final String THOUGHT_MSG = "thought_msg";
    public static final String DEVICE_TOKEN = "dev_token";
    public static final String USER_ID = "user_id";
    public static final String USER_DATA = "user_data";
    public static final String USER_SAVED_EMAIL = "user_saved_email";
    public static final String USER_SAVED_PASS = "user_saved_pass";
    public static final String DONT_SHOW_PROFILE_EXIT = "profile_exit";
    public static final String IS_SIGNED_IN = "isSignedIn";

    public class Settings {
        public static final String IS_NOTI_ON = "isNotiOn";
        public static final String REM_H = "rem_h";
        public static final String REM_M = "rem_m";
        public static final String SNOOZE_M = "snooze_m";
    }

    public class DayStats {
        public static final int ABSENT = 0;
        public static final int FULL_DAY = 1;
        public static final int HALF_DAY = 2;
        public static final int SHORT_LEAVE = 3;
    }
}