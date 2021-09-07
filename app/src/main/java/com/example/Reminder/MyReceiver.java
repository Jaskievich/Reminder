package com.example.Reminder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;

import androidx.core.app.NotificationCompat;

import java.util.Date;
import java.util.Objects;

import static android.content.Context.ALARM_SERVICE;

public class MyReceiver extends BroadcastReceiver
{
    public static final String CHANNEL_ID = "channel_1111";
    public static final int NOTIFICATION_ID = 111111;
    @Override
    public void onReceive(Context context, Intent _intent)
    {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Bundle bundle = _intent.getBundleExtra("listBundle");
        if (bundle == null) return;
        ReminderItem item = (ReminderItem) bundle.getSerializable("item");
        if ( item == null ) return;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                startNotification(context, NOTIFICATION_ID,item);
        }
        else {
            // Разбудить экран
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            @SuppressLint("InvalidWakeLockTag")
            PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
            //   PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "TAG");
            wakeLock.acquire();
            try {
                Intent intent = new Intent(context, ActivityItemView.class);
                //      _intent.setClass(context, ActivityItemView.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ReminderItem.class.getSimpleName(), item);
                context.startActivity(_intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            wakeLock.release();
        }
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

//    public static void startActivityNotification(Context context, int notificationID, ReminderItem item)
//    {
//        NotificationManager mNotificationManager =
//                (NotificationManager)
//                        context.getSystemService(Context.NOTIFICATION_SERVICE);
//        //Create GPSNotification builder
//        NotificationCompat.Builder mBuilder;
//
//        //Initialise ContentIntent
//        Intent ContentIntent = new Intent(context, ActivityItemView.class);
//        ContentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent ContentPendingIntent = PendingIntent.getActivity(context,
//                0,
//                ContentIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        mBuilder = new NotificationCompat.Builder(context)
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContentTitle(item.getTitle())
//                .setContentText(item.getDescription())
//                .setColor(context.getResources().getColor(R.color.colorPrimaryDark))
//                .setAutoCancel(true)
//                .setContentIntent(ContentPendingIntent)
//                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID,
//                    "Activity Opening Notification",
//                    NotificationManager.IMPORTANCE_HIGH);
//            mChannel.enableLights(true);
//            mChannel.enableVibration(true);
//            mChannel.setDescription("Activity opening notification");
//
//            mBuilder.setChannelId(CHANNEL_ID);
//
//            Objects.requireNonNull(mNotificationManager).createNotificationChannel(mChannel);
//        }
//
//        Objects.requireNonNull(mNotificationManager).notify(TAG_NOTIFICATION,notificationID,
//                mBuilder.build());
//    }

    public static void startNotification(Context context, int notificationID, ReminderItem item) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Create GPSNotification builder
        NotificationCompat.Builder mBuilder;

        mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(item.getTitle())
                .setContentText(item.getDescription())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

        mNotificationManager.notify( notificationID, mBuilder.build());

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