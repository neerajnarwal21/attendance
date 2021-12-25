/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.accu.attendance.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.accu.attendance.R;
import com.accu.attendance.activity.MainActivity;
import com.accu.attendance.activity.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


public class MyFcmListenerService extends FirebaseMessagingService {

    private static final String TAG = "MyFcmListenerService";
    private PrefStore store;

    @Override
    public void onCreate() {
        super.onCreate();
        store = new PrefStore(this);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e(TAG, "Refreshed token: " + s);
        sendRegistrationToServer(s);
    }

    private void sendRegistrationToServer(String token) {
        PrefStore store = new PrefStore(this);
        store.saveString(Const.DEVICE_TOKEN, token);
        TokenRefresh tokenRefresh = TokenRefresh.getInstance();
        tokenRefresh.tokenRefreshComplete();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map data = remoteMessage.getData();
        Log.e("bundle Data", "" + data);
        String from = remoteMessage.getFrom();
        Utils.INSTANCE.debugLog(TAG, "From: " + from);
        String action = null;
        if (data.containsKey("action"))
            action = data.get("action").toString();
        if (action != null && store.getString(Const.USER_ID) != null) {
            Notification.Builder builder;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                builder = new Notification.Builder(this, Const.NOTI_CHANNEL_ID);
            } else {
                builder = new Notification.Builder(this);
            }
            builder.setContentTitle(getString(R.string.app_name));
            builder.setContentText(data.get("message").toString());
            builder.setStyle(new Notification.BigTextStyle().bigText(data.get("message").toString()));
            builder.setSmallIcon(R.mipmap.ic_logo_trans);

            Intent intent = new Intent(this, SplashActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);


            NotificationManager mNotificationManager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            switch (action) {
                case "time-change":
                    Intent intentMain = new Intent(this, MainActivity.class);
                    intentMain.putExtra("fromPush", true);
                    intentMain.putExtra("action", action);
                    intentMain.putExtra("message", data.get("message").toString());
                    PendingIntent pendingIntentMain = PendingIntent.getActivity(this, 0, intentMain, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(pendingIntentMain);

                    mNotificationManager.notify(1, builder.build());
                    break;
            }
        } else if (data.containsKey("thought")) {
            String thought = data.get("thought").toString();
            store.saveString(Const.CURRENT_DATE, new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date()));
            store.saveString(Const.THOUGHT_MSG, thought);
        }
//        Bundle bundle = new Bundle();
//        bundle.putString("action", data.get("action").toString());
//        bundle.putString("controller", data.get("controller").toString());
//        bundle.putString("message", data.get("message").toString());
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//            String channelId = "some_channel_id";
//            String channelName = "Some Channel";
//            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
//            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.RED);
//            notificationChannel.enableVibration(true);
//            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//            notificationManager.createNotificationChannel(notificationChannel);
//
//            Notification notification = new Notification.ImageSelectBuilder(this, "some_channel_id")
//                    .setContentTitle("Some Message")
//                    .setContentText("You've received new messages!")
//                    .setSmallIcon(R.drawable.ic_camera_white)
//                    .build();
//
//            notificationManager.notify(1, notification);
//        }
    }
}