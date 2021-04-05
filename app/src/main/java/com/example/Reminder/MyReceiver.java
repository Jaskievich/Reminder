package com.example.Reminder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;

import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

public class MyReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent _intent)
    {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Bundle bundle = _intent.getBundleExtra("listBundle");
        if (bundle == null) return;
        ReminderItem item = (ReminderItem) bundle.getSerializable("item");
        if ( item == null ) return;
        // Разбудить экран
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag")
        PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        wakeLock.acquire();
        Intent intent = new Intent(context, ActivityItemView.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ReminderItem.class.getSimpleName(), item);
        context.startActivity(intent);
        wakeLock.release();
        // Запустить следующее задание
        RemindDBHelper remindDBHelper = new RemindDBHelper(context);
        ReminderItem item_peek = remindDBHelper.getActualFistItem();
        if (item_peek != null )
            startNewAlarmTask(context, item_peek, item_peek.getDate());
        else stopAlarmTask(context);
        remindDBHelper.close();
    }


    static public void startNewAlarmTask(Context context, ReminderItem item, Date date)
    {
        Intent intent = new Intent(context, MyReceiver.class);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", item);
        intent.putExtra("listBundle", bundle);
        PendingIntent pi = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date.getTime(), pi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), pi);
        }
   /*     AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo( date.getTime(), pi);
        am.setAlarmClock(alarmClockInfo, pi);*/
    }

    static public void stopAlarmTask(Context context)
    {
        Intent intent = new Intent(context, MyReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_NO_CREATE);
        if (pi != null) {
            AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            am.cancel(pi);
        }
    }

    static public boolean isAlarmTask(Context context) {
        Intent intent = new Intent(context, MyReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

}


//public class MyReceiver extends BroadcastReceiver
//{
//
//    @Override
//    public void onReceive(Context context, Intent _intent)
//    {
//        // TODO: This method is called when the BroadcastReceiver is receiving
//        // an Intent broadcast.
//        Bundle bundle = _intent.getBundleExtra("listBundle");
//        if (bundle == null) return;
//        ArrayList<ReminderItem> listReminder = (ArrayList<ReminderItem>) bundle.getSerializable("listRemind");
//        if (listReminder == null || listReminder.size() == 0) return;
//        // Взять последнюю запись
//        int index_last = listReminder.size() - 1;
//        ReminderItem item = listReminder.get(index_last);
//        listReminder.remove(index_last);
//        // Разбудить экран
//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        @SuppressLint("InvalidWakeLockTag")
//        PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
//        wakeLock.acquire();
//        Intent intent = new Intent(context, ActivityItemView.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra(ReminderItem.class.getSimpleName(), item);
//        context.startActivity(intent);
//        wakeLock.release();
//        // Запустить следующее задание
//        index_last = listReminder.size() - 1;
//        if (index_last > -1) {
//            ReminderItem item_peek = listReminder.get(index_last);
//            startNewAlarmTask(context, listReminder, item_peek.getDate());
//        }
//    }
//
//
//    static public void startNewAlarmTask(Context context, ArrayList<ReminderItem> listReminder, Date date)
//    {
//        Intent intent = new Intent(context, MyReceiver.class);
//        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("listRemind", listReminder);
//        intent.putExtra("listBundle", bundle);
//        PendingIntent pi = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date.getTime(), pi);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            am.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), pi);
//        }
//    }
//
//    static public void stopAlarmTask(Context context)
//    {
//        Intent intent = new Intent(context, MyReceiver.class);
//        PendingIntent pi = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_NO_CREATE);
//        if (pi != null) {
//            AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//            am.cancel(pi);
//        }
//    }
//
//}