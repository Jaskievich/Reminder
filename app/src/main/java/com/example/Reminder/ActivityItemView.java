package com.example.Reminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityItemView extends AppCompatActivity {

    private Ringtone ringtone = null;
    private MediaPlayer mediaPlayer = null;

//    private void makeSoundDefault()
//    {
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
//        String alarmUriStr = sp.getString("ringtone_preference_1", null);
//        Uri alarmUri = null;
//        if( alarmUriStr == null ) alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        else alarmUri = Uri.parse(alarmUriStr);
//        if (alarmUri == null) {
//            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//        }
//        ringtone = RingtoneManager.getRingtone(this, alarmUri);
//        ringtone.play();
//    }

    private void makeSoundDefault()
    {

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }
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
            ReminderItem item = (ReminderItem) arguments.getSerializable(ReminderItem.class.getSimpleName());
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