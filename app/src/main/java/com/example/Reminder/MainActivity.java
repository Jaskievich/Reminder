package com.example.Reminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String FILE_NAME = "content.txt";
    private ReminderCtrl reminderCtrl = new ReminderCtrl();
    private ArrayList<ReminderItem> listReminder = null;
    private  ListView lv = null;
    private AdapterReminder adp;
    private int curr_pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if( !loadFromFile(FILE_NAME) ){
            listReminder = new ArrayList<>();
        }
        reminderCtrl.setListReminder(listReminder);
        lv = (ListView) findViewById(R.id.ltv);
        adp = new AdapterReminder(this, listReminder);
        lv.setAdapter(adp);
        final Button btn_add = (Button) findViewById(R.id.button_add);
        btn_add.setOnClickListener(this);
        final Button btn_start = (Button)findViewById(R.id.button_start);
        btn_start.setOnClickListener(this);
        final Button btn_stop = (Button)findViewById(R.id.button_stop);
        btn_stop.setOnClickListener(this);
        final Button btn_del = (Button)findViewById(R.id.button_delete);
        btn_del.setOnClickListener(this);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ReminderItem item = listReminder.get(position);
                Intent intent = new Intent(MainActivity.this, ActivityItem.class);
                intent.putExtra(ReminderItem.class.getSimpleName(), item);
                startActivityForResult(intent, position);
                return false;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                curr_pos = position;
            }
        });

        lv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                curr_pos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    void saveToFile(String name_file)
    {
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

    boolean loadFromFile(String name_file)
    {
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
        if (listActualReminder.size() == 0) return;
        adp.notifyDataSetChanged();
        Collections.reverse(listActualReminder);
        int index_last = listActualReminder.size() - 1;
        ReminderItem item = listActualReminder.get(index_last);
        MyReceiver.startNewAlarmTask(this, listActualReminder, item.getDate());
        Toast.makeText(this, "Сигнализация установлена", Toast.LENGTH_SHORT).show();
    }

  /*  void stopServiceTask()
    {
        stopService(new Intent(this, MyService.class) );
    }
    */

    private void DeleteItem()
    {
       if( reminderCtrl.delItemByIndex(curr_pos) ) {
           adp.notifyDataSetChanged();
           if(curr_pos == listReminder.size()) {
               lv.requestFocusFromTouch();
               lv.setSelection(curr_pos - 1);
           }

       }
    }

    private void stopAlarm(){
        MyReceiver.stopAlarmTask(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add:
                Intent intent = new Intent(this, ActivityItem.class);
                startActivityForResult(intent,  listReminder.size());
                break;
            case R.id.button_start:
                startAlarmTask();
                break;
            case R.id.button_stop:
                stopAlarm();
                break;
            case R.id.button_delete:
                DeleteItem();
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
        saveToFile(FILE_NAME);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    class AdapterReminder extends ArrayAdapter<ReminderItem>
    {
        String myFormat = "MM/dd/yy HH:mm"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

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
                        .setText(sdf.format(dt));
            }
            return convertView;
            //  return super.getView(position, convertView, parent);
        }
    }
}