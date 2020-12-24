package com.example.Reminder;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class MyReceiver extends BroadcastReceiver
{

    private Stack<ReminderItem> stackReminder = null;
    @Override
    public void onReceive(Context context, Intent _intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Bundle bundle = _intent.getBundleExtra("listBundle");
        if(bundle == null) return;
        ArrayList<ReminderItem> listReminder = (ArrayList<ReminderItem>) bundle.getSerializable("listRemind");
        if( listReminder == null ) return ;

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag")
        PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
     //    PowerManager.WakeLock wakeLock = pm.newWakeLock(( PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");

//        KeyguardManager keyguardManager = (KeyguardManager)  context.getSystemService(Context.KEYGUARD_SERVICE);
//        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
//        keyguardLock.disableKeyguard();
         wakeLock.acquire();

        Collections.reverse(listReminder);
        stackReminder = new Stack<>();
        stackReminder.addAll(listReminder);
        ReminderItem item = stackReminder.pop();
        Intent intent = new Intent(context, ActivityItem.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(ReminderItem.class.getSimpleName(), item);
        context.startActivity(intent);

        wakeLock.release();
    }
}