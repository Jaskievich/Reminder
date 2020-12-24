package com.example.Reminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String FILE_NAME = "content.txt";
    private ReminderCtrl reminderCtrl = new ReminderCtrl();
    private ArrayList<ReminderItem> listReminder = null;
    private AdapterReminder adp;
    private AlarmManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if( !loadFromFile(FILE_NAME) ){
            listReminder = new ArrayList<>();
        }
        reminderCtrl.setListReminder(listReminder);
        ListView lv = (ListView) findViewById(R.id.ltv);
        adp = new AdapterReminder(this, listReminder);
        lv.setAdapter(adp);
        final ImageButton btn_add = (ImageButton) findViewById(R.id.imageButton_add);
        btn_add.setOnClickListener(this);
        final ImageButton btn_save = (ImageButton)findViewById(R.id.imageButton_save);
        btn_save.setOnClickListener(this);
        final ImageButton btn_start = (ImageButton)findViewById(R.id.imageButton_start);
        btn_start.setOnClickListener(this);
        final ImageButton btn_stop = (ImageButton)findViewById(R.id.imageButton_stop);
        btn_stop.setOnClickListener(this);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ReminderItem item = listReminder.get(position);
                Intent intent = new Intent(MainActivity.this, ActivityItem.class);
                intent.putExtra(ReminderItem.class.getSimpleName(), item);
                startActivityForResult(intent, position);
            }
        });

        am = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    void saveToFile(String name_file) {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(name_file, MODE_PRIVATE);
            ObjectOutputStream obj_out = new ObjectOutputStream(fos);
            obj_out.writeObject(listReminder);
            obj_out.close();
            Toast.makeText(this, "Файл сохранен", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (fos != null) fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean loadFromFile(String name_file) {
        boolean isRes = true;
        FileInputStream fin = null;
        ObjectInputStream in = null;
        try {
            ReminderItem item = null;
            fin = openFileInput(name_file);
            in = new ObjectInputStream(fin);
            listReminder = ((ArrayList<ReminderItem>) in.readObject());
        } catch (IOException | ClassNotFoundException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            isRes = false;
        }
        try {
            if (in != null) in.close();
            if (fin != null) fin.close();
        } catch (IOException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            isRes = false;
        }
        return isRes;
    }

/*    void startServiceTask()
    {
        ArrayList<ReminderItem> stackReminderActual = reminderCtrl.getListActualReminder();
        if( stackReminderActual.size() == 0 ) return;
        adp.notifyDataSetChanged();
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("listRemind", stackReminderActual);
        startService(intent);
    }
    */

    void startAlarmTask()
    {
        ArrayList<ReminderItem> listActualReminder = reminderCtrl.getListActualReminder();
        if( listActualReminder.size() == 0 ) return;
        adp.notifyDataSetChanged();
        Intent intent = new Intent(this, MyReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("listRemind", listActualReminder);
        intent.putExtra("listBundle", bundle);
        PendingIntent pi = PendingIntent.getBroadcast(this.getApplicationContext(), 1, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
        ReminderItem item = listActualReminder.get(0);
      //  am.set(AlarmManager.RTC_WAKEUP, item.getDate().getTime(), pi);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    //        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, item.getDate().getTime(), pi);
            AlarmManager.AlarmClockInfo ac= new AlarmManager.AlarmClockInfo(item.getDate().getTime(),   pi);
            am.setAlarmClock(ac, pi);
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                am.setExact(AlarmManager.RTC_WAKEUP, item.getDate().getTime(), pi);
            }
        }

        Toast.makeText(this, "Сигнализация установлена", Toast.LENGTH_SHORT).show();
    }


   /* void startAlarmTask()
    {
        ArrayList<ReminderItem> listActualReminder = reminderCtrl.getListActualReminder();
        if( listActualReminder.size() == 0 ) return;
        adp.notifyDataSetChanged();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            ComponentName componentName = new ComponentName(this, MyJobService.class);
            JobInfo jobInfoObj = new JobInfo.Builder(1, componentName)
                    .setBackoffCriteria(6000, JobInfo.BACKOFF_POLICY_LINEAR)
                    .setMinimumLatency(1000*120)
                    .setOverrideDeadline(1000*200)
                    .build();
            jobScheduler.schedule(jobInfoObj);
            Toast.makeText(this, "Сигнализация установлена", Toast.LENGTH_SHORT).show();
        }
    }*/


  /*  void stopServiceTask()
    {
        stopService(new Intent(this, MyService.class) );
    }
    */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton_add:
                Intent intent = new Intent(this, ActivityItem.class);
                startActivityForResult(intent,  listReminder.size());
                break;
            case R.id.imageButton_save:
                saveToFile(FILE_NAME);
                break;
            case R.id.imageButton_start:
                startAlarmTask();
                break;
            case R.id.imageButton_stop:
          //      stopServiceTask();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode != RESULT_OK || data == null ) return;
        ReminderItem item = (ReminderItem) data.getSerializableExtra(ReminderItem.class.getSimpleName());
        if( requestCode < listReminder.size() ){
          //  ReminderItem item_curr = listReminder.get(requestCode);
            listReminder.set(requestCode, item);
        }
        else listReminder.add(item);
        adp.notifyDataSetChanged();

    }

    class AdapterReminder extends ArrayAdapter<ReminderItem> {
        public AdapterReminder(@NonNull Context context, ArrayList<ReminderItem> listReminder) {
            super(context, android.R.layout.simple_list_item_2, listReminder);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            ReminderItem item = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(android.R.layout.simple_list_item_2, null);
            }
            ((TextView) convertView.findViewById(android.R.id.text1))
                    .setText(item.getTitle());
            Date dt = item.getDate();
            if(dt != null) {
                ((TextView) convertView.findViewById(android.R.id.text2))
                        .setText(dt.toString());
            }
            return convertView;
            //  return super.getView(position, convertView, parent);
        }
    }
}