package com.example.Reminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActivityItemView extends AppCompatActivity {

    private Ringtone ringtone = null;
    private MediaPlayer mediaPlayer = null;
    private ReminderItem item = null;
    private Button btn_postpone ;
    private int postpone_val = 10;

    private void makeSoundDefault() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String alarmUriStr = sp.getString("ringtone_preference_1", null);
        Uri alarmUri = null;
        if (alarmUriStr != null) alarmUri = Uri.parse(alarmUriStr);
        if (alarmUri == null) alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (ringtone != null) ringtone.stop();
        ringtone = RingtoneManager.getRingtone(this, alarmUri);
        ringtone.play();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);
        getWindow().addFlags( WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        final TextView text_title = (TextView) findViewById(R.id.textView_title);
        final TextView text_Descript = (TextView) findViewById(R.id.TextViewMultiLine);

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            item = (ReminderItem) arguments.getSerializable(ReminderItem.class.getSimpleName());
            if (item != null) {
                text_title.setText(item.getTitle());
                updateLabelDate(item.getDate());
                text_Descript.setText(item.getDescription());
                if(item.getAudio_file() == null || item.getAudio_file().isEmpty()) makeSoundDefault();
                else playAudio(item.getAudio_file());
            }
        }

        final Button btn_close = (Button) findViewById(R.id.button_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        String[] time_interval = { "10 мин", "20 мин", "30 мин", "60 мин"};
        final Spinner spinner = (Spinner) findViewById(R.id.spinner2);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, time_interval);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        spinner.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Получаем выбранный объект
                String item = (String)parent.getItemAtPosition(position);
                btn_postpone.setText("Отложить на "+ item);
                String arr[] = item.split(" ");
                postpone_val = Integer.parseInt(arr[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinner.setOnItemSelectedListener(itemSelectedListener);

        btn_postpone = (Button) findViewById(R.id.button_postp);
        btn_postpone.setText("Отложить на "+ postpone_val + " мин.");
        btn_postpone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( item != null){
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, postpone_val);
                    item.setDate(calendar.getTime());
                    RemindDBHelper remindDBHelper = new RemindDBHelper(ActivityItemView.this);
                    remindDBHelper.updateItem(item);

                    ReminderItem item_actual = remindDBHelper.getActualFistItem();
                    if( item_actual != null )
                        MyReceiver.startNewAlarmTask(ActivityItemView.this, item_actual, item_actual.getDate());
                    finish();
                }
            }
        });
    }

    private void playAudio(String audio_file) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(audio_file);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if( ringtone != null ) ringtone.stop();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    private void updateLabelDate(Date date)
    {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        final TextView text_Date = (TextView) findViewById(R.id.TextViewDate);
        text_Date.setText(sdf.format(date));
        final TextView text_time = (TextView) findViewById(R.id.TextViewTime);
        sdf.applyPattern("HH:mm");
        text_time.setText(sdf.format(date));
    }
}