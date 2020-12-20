package com.example.Reminder;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

    final String LOG_TAG = "MyService";
    private Timer timer;
    private Stack<ReminderItem> stackReminder = null;

    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Служба создана", Toast.LENGTH_SHORT).show();
        Log.d(LOG_TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "Служба уничтожена", Toast.LENGTH_SHORT).show();
        Log.d(LOG_TAG, "onDestroy");
    }

  /*  @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer = new Timer();
        Date dt = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        calendar.add(Calendar.MINUTE, 2);
        timer.schedule(new MyTimerTask(), calendar.getTime());
        Log.d(LOG_TAG, "onStartCommand");
        return Service.START_STICKY;
    }*/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ArrayList<ReminderItem> listReminder = (ArrayList<ReminderItem>) intent.getSerializableExtra("listRemind");
        if( listReminder == null ) {
            Toast.makeText(this, "Служба не запущена", Toast.LENGTH_SHORT).show();
            return Service.START_NOT_STICKY;
        }
        Collections.reverse(listReminder);
        stackReminder = new Stack<>();
        stackReminder.addAll(listReminder);
        timer = new Timer();
        ReminderItem item = stackReminder.pop();
        timer.schedule(new MyTimerTask(item), item.getDate());
        Log.d(LOG_TAG, "onStartCommand");
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class MyTimerTask extends TimerTask{
        private ReminderItem item = null;

        public MyTimerTask(ReminderItem item){
            this.item = item;
        }

        @Override
        public void run() {
            Log.d(LOG_TAG, "run");
            Intent intent = new Intent(getApplicationContext(), ActivityItem.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(ReminderItem.class.getSimpleName(), item);
            startActivity(intent);
            if( !stackReminder.empty() ) {
                Log.d(LOG_TAG, "!stackReminder.empty()");
                ReminderItem item = stackReminder.pop();
                timer.schedule(new MyTimerTask(item), item.getDate());
            }
            else stopSelf();
        }
    }
}
