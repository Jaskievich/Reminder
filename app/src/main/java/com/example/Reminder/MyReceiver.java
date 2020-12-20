package com.example.Reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class MyReceiver extends BroadcastReceiver {
//public class MyReceiver extends WakefulBroadcastReceiver {

    private Stack<ReminderItem> stackReminder = null;
    @Override
    public void onReceive(Context context, Intent _intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Toast.makeText(context.getApplicationContext(), "Сигнализация сработала", Toast.LENGTH_SHORT).show();
        Bundle bundle = _intent.getBundleExtra("listBundle");
        if(bundle == null) return;
        ArrayList<ReminderItem> listReminder = (ArrayList<ReminderItem>) bundle.getSerializable("listRemind");
        if( listReminder == null ) return ;
        Collections.reverse(listReminder);
        stackReminder = new Stack<>();
        stackReminder.addAll(listReminder);
        ReminderItem item = stackReminder.pop();
        Intent intent = new Intent(context, ActivityItem.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ReminderItem.class.getSimpleName(), item);
        context.startActivity(intent);
    }
}