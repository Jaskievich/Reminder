 package com.example.Reminder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class  MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_start, btn_stop, btn_del;
    private AdapterReminder adp;
    private int curr_pos = 0;
    private RemindDBHelper remindDBHelper;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        remindDBHelper = new RemindDBHelper(this);

        ListView lv = (ListView) findViewById(R.id.ltv);
        Cursor cursor = remindDBHelper.getAllTable();
        adp = new AdapterReminder(this, cursor, true);
        lv.setAdapter(adp);
        final Button btn_add = (Button) findViewById(R.id.button_add);
        btn_add.setOnClickListener(this);
        btn_start = (Button) findViewById(R.id.button_start);
        btn_start.setOnClickListener(this);
     //   btn_start.setEnabled(cursor.getCount() > 0);
        btn_stop = (Button) findViewById(R.id.button_stop);
        btn_stop.setOnClickListener(this);
    //    btn_stop.setEnabled(cursor.getCount() > 0);
        btn_del = (Button) findViewById(R.id.button_delete);
        btn_del.setOnClickListener(this);
        btn_del.setEnabled(false);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                ReminderItem item = adp.getItem(position);
                Intent intent = new Intent(MainActivity.this, ActivityItem.class);
                intent.putExtra(ReminderItem.class.getSimpleName(), item);
                startActivityForResult(intent, 1);
                return false;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                curr_pos = position;
                if (!btn_del.isEnabled()) btn_del.setEnabled(true);
            }
        });

        lv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                curr_pos = position;
                if (!btn_del.isEnabled()) btn_del.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (btn_del.isEnabled()) btn_del.setEnabled(false);
            }
        });

        // Определить запущено ли задание
        if(MyReceiver.isAlarmTask(this)) {
            btn_start.setEnabled(false);
            btn_stop.setEnabled(true);
        }
        else {
            btn_start.setEnabled(cursor.getCount() > 0);
            btn_stop.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adp.getCursor().close();
        remindDBHelper.close();
    }

    // Запустить задание будильника
    void startAlarmTask()
    {
        ReminderItem item = remindDBHelper.getActualFistItem();
        if( item == null ) return;
        MyReceiver.startNewAlarmTask(this, item, item.getDate());
        btn_stop.setEnabled(true);
        btn_start.setEnabled(false);
        Toast.makeText(this, "Сигнализация установлена "+item.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void DeleteItem() {

        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setMessage(R.string.quest_del).setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ReminderItem item = adp.getItem(curr_pos);
                                if( item.getAudio_file()!= null ) MyUtility.DeleteFile(item.getAudio_file());
                                remindDBHelper.deleteItem(item.getId());
                                adp.changeCursor(remindDBHelper.getAllTable());
                            }
                        }

                ).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        final AlertDialog dlg = build.create();
        dlg.show();
    }

    private void stopAlarm(){

        MyReceiver.stopAlarmTask(this);
        btn_start.setEnabled(remindDBHelper.getAllTable().getCount() > 0);
        btn_stop.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add:
                Intent intent = new Intent(this, ActivityItem.class);
                startActivityForResult(intent, 1);
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
        if(item.getId() > 0) remindDBHelper.updateItem(item);
        else remindDBHelper.insertItem(item);
        adp.changeCursor(remindDBHelper.getAllTable());
        startAlarmTask(); // Запустить будильник
    //    if( !btn_start.isEnabled() )  btn_start.setEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
   //     Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.clear_settings:
                remindDBHelper.deleteOldItem();
                adp.changeCursor(remindDBHelper.getAllTable());
                break;
            case R.id.sort_settings:
                ArrayList<ReminderItem> listItem = remindDBHelper.getAllTableSort();
                remindDBHelper.clearTable();
                for(ReminderItem it : listItem){
                    remindDBHelper.insertItem(it);
                }
                adp.changeCursor(remindDBHelper.getAllTable());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    static class AdapterReminder extends CursorAdapter
    {
        final String myFormat = "MM/dd/yy HH:mm"; //In which you need put here
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        final Date date = new Date();

        public AdapterReminder(Context context, Cursor c, boolean flags) {
            super(context, c, flags);
        }

        public ReminderItem getItem(int position) {
            Cursor cr = (Cursor) super.getItem(position);
            if( cr != null && cr.getCount() > 0){
                return RemindDBHelper.CursorToItem(cr);
            }
            return null;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tvTitle = (TextView) view.findViewById(R.id.text_name);
            tvTitle.setText( cursor.getString( cursor.getColumnIndexOrThrow(RemindDBHelper.COLUMN_NAME) ) );
            long tm = cursor.getLong(cursor.getColumnIndexOrThrow(RemindDBHelper.COLUMN_DATE));
            Date dt = new Date(tm);
            ((TextView) view.findViewById(R.id.text_date)).setText(sdf.format(dt));
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView_2);
            if(date.getTime() < dt.getTime()) imageView.setImageResource(android.R.drawable.star_big_on);
            else imageView.setImageResource(android.R.drawable.star_big_off);
            ImageView imageViewAudio = (ImageView) view.findViewById(R.id.imageView_1);
            String audi_file = cursor.getString( cursor.getColumnIndexOrThrow(RemindDBHelper.COLUMN_AFILE) );
            if( audi_file != null && !audi_file.isEmpty()){
                imageViewAudio.setImageResource(android.R.drawable.ic_btn_speak_now);
            }
            else  imageViewAudio.setImageResource(0);

        }
    }
}

//
//public class MainActivity extends AppCompatActivity implements View.OnClickListener {
//
//    private final static String FILE_NAME = "content.txt";
//    private Button btn_start, btn_stop, btn_del;
//    private ReminderCtrl reminderCtrl = new ReminderCtrl();
//    private ArrayList<ReminderItem> listReminder = null;
//    private  ListView lv = null;
//    private AdapterReminder adp;
//    private int curr_pos = 0;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        if( !loadFromFile(FILE_NAME) ){
//            listReminder = new ArrayList<>();
//        }
//        reminderCtrl.setListReminder(listReminder);
//        lv = (ListView) findViewById(R.id.ltv);
//        adp = new AdapterReminder(this, listReminder);
//        lv.setAdapter(adp);
//        final Button btn_add = (Button) findViewById(R.id.button_add);
//        btn_add.setOnClickListener(this);
//        btn_start = (Button)findViewById(R.id.button_start);
//        btn_start.setOnClickListener(this);
//        btn_start.setEnabled(listReminder.size() > 0);
//        btn_stop = (Button)findViewById(R.id.button_stop);
//        btn_stop.setOnClickListener(this);
//        btn_stop.setEnabled(listReminder.size() > 0);
//        btn_del = (Button)findViewById(R.id.button_delete);
//        btn_del.setOnClickListener(this);
//        btn_del.setEnabled(false);
//
//        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                ReminderItem item = listReminder.get(position);
//                Intent intent = new Intent(MainActivity.this, ActivityItem.class);
//                intent.putExtra(ReminderItem.class.getSimpleName(), item);
//                startActivityForResult(intent, position);
//                return false;
//            }
//        });
//
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                curr_pos = position;
//                if( !btn_del.isEnabled()) btn_del.setEnabled(true);
//            }
//        });
//
//        lv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                curr_pos = position;
//                if( !btn_del.isEnabled()) btn_del.setEnabled(true);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                if( btn_del.isEnabled()) btn_del.setEnabled(false);
//            }
//        });
//
//    }
//
//    void saveToFile(String name_file)
//    {
//        FileOutputStream fos = null;
//        try {
//            fos = openFileOutput(name_file, MODE_PRIVATE);
//            ObjectOutputStream obj_out = new ObjectOutputStream(fos);
//            obj_out.writeObject(listReminder);
//            obj_out.close();
//            Toast.makeText(this, "Файл сохранен", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            if (fos != null) fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    boolean loadFromFile(String name_file)
//    {
//        boolean isRes = true;
//        FileInputStream fin = null;
//        ObjectInputStream in = null;
//        try {
//            ReminderItem item = null;
//            fin = openFileInput(name_file);
//            in = new ObjectInputStream(fin);
//            listReminder = ((ArrayList<ReminderItem>) in.readObject());
//        } catch (IOException | ClassNotFoundException ex) {
//            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
//            isRes = false;
//        }
//        try {
//            if (in != null) in.close();
//            if (fin != null) fin.close();
//        } catch (IOException ex) {
//            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
//            isRes = false;
//        }
//        return isRes;
//    }
//
//    void startAlarmTask()
//    {
//        ArrayList<ReminderItem> listActualReminder = reminderCtrl.getListActualReminder();
//        if (listActualReminder.size() == 0) return;
//        adp.notifyDataSetChanged();
//        Collections.reverse(listActualReminder);
//        int index_last = listActualReminder.size() - 1;
//        ReminderItem item = listActualReminder.get(index_last);
//        MyReceiver.startNewAlarmTask(this, listActualReminder, item.getDate());
//        Toast.makeText(this, "Сигнализация установлена", Toast.LENGTH_SHORT).show();
//    }
//
//    private void DeleteItem() {
//
//        AlertDialog.Builder build = new AlertDialog.Builder(this);
//        build.setMessage(R.string.quest_del).setCancelable(false)
//        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
//                {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (reminderCtrl.delItemByIndex(curr_pos)) {
//                            adp.notifyDataSetChanged();
//                            if (curr_pos == listReminder.size()) {
//                                lv.requestFocusFromTouch();
//                                lv.setSelection(curr_pos - 1);
//                            }
//                            saveToFile(FILE_NAME);
//                            if( reminderCtrl.getListReminder().size() == 0 ){
//                                if( btn_start.isEnabled() ) btn_start.setEnabled(false);
//                                if( btn_del.isEnabled() ) btn_del.setEnabled(false);
//                            }
//                        }
//                    }
//                }
//
//        ).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        final AlertDialog dlg = build.create();
//        dlg.show();
//    }
//
//    private void stopAlarm(){
//        MyReceiver.stopAlarmTask(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.button_add:
//                Intent intent = new Intent(this, ActivityItem.class);
//                startActivityForResult(intent,  listReminder.size());
//                break;
//            case R.id.button_start:
//                startAlarmTask();
//                break;
//            case R.id.button_stop:
//                stopAlarm();
//                break;
//            case R.id.button_delete:
//                DeleteItem();
//                break;
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if( resultCode != RESULT_OK || data == null ) return;
//        ReminderItem item = (ReminderItem) data.getSerializableExtra(ReminderItem.class.getSimpleName());
//        if( requestCode < listReminder.size() ){
//          //  ReminderItem item_curr = listReminder.get(requestCode);
//            listReminder.set(requestCode, item);
//        }
//        else listReminder.add(item);
//        adp.notifyDataSetChanged();
//        saveToFile(FILE_NAME);
//        if( !btn_start.isEnabled() )  btn_start.setEnabled(true);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // TODO Auto-generated method stub
//        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
//        if( item.getItemId() == R.id.clear_settings){
//            reminderCtrl.deleteOldRecord();
//            adp.notifyDataSetChanged();
//            saveToFile(FILE_NAME);
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    class AdapterReminder extends ArrayAdapter<ReminderItem>
//    {
//        String myFormat = "MM/dd/yy HH:mm"; //In which you need put here
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
//        Date date = new Date();
//
//        public AdapterReminder(@NonNull Context context, ArrayList<ReminderItem> listReminder) {
//            super(context, R.layout.list_item, listReminder);
//        }
//
//        @NonNull
//        @Override
//        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//
//            ReminderItem item = getItem(position);
//            if (convertView == null) {
//                convertView = LayoutInflater.from(getContext())
//                        .inflate(/*android.R.layout.simple_list_item_2*/R.layout.list_item, null);
//            }
//            ((TextView) convertView.findViewById(R.id.text_name))
//                    .setText(item.getTitle());
//            Date dt = item.getDate();
//            if(dt != null) {
//                ((TextView) convertView.findViewById(R.id.text_date)).setText(sdf.format(dt));
//                ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView_2);
//                if(date.getTime() < dt.getTime()) imageView.setImageResource(android.R.drawable.star_big_on);
//                else imageView.setImageResource(android.R.drawable.star_big_off);
//            }
//            ImageView imageViewAudio = (ImageView) convertView.findViewById(R.id.imageView_1);
//            if( item.getAudio_file() != null && !item.getAudio_file().isEmpty()){
//                imageViewAudio.setImageResource(android.R.drawable.ic_btn_speak_now);
//            }
//            else  imageViewAudio.setImageResource(0);;
//
//            return convertView;
//            //  return super.getView(position, convertView, parent);
//        }
//    }
//}