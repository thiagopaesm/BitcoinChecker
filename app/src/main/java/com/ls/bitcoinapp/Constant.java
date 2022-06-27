package com.ls.bitcoinapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Constant {
    public static long MINUTE = 1 * 60 * 1000;
    public static long MINUTE_TEST = 1 * 60 * 1000;
    public static int ID_ALARM_BIT_COIN = 1;
    public static int ID_ALARM_XMR = 2;


    public static void startAlarm(int idAlarm, Context context) {
        AlarmManager alarmMgr =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, NotificationBroadcast.class);
        PendingIntent intent = PendingIntent.getBroadcast(
                context,
                idAlarm,
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE
        );
        alarmMgr.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                MINUTE_TEST,
                intent
        );
    }

    public static void cancelAlarm(int idAlarm, Context context) {
        AlarmManager alarmMgr =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, NotificationBroadcast.class);
        PendingIntent intent = PendingIntent.getBroadcast(
                context,
                idAlarm,
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE
        );
        alarmMgr.cancel(intent);
    }
}
