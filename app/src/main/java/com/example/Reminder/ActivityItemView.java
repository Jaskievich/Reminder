package com.example.Reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        final TextView text_title = (TextView) findViewById(R.id.textView_title);
        final TextView text_Descript = (TextView) findViewById(R.id.TextViewMultiLine);

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            ReminderItem item = (ReminderItem) arguments.getSerializable(ReminderItem.class.getSimpleName());
            if (item != null) {
                text_title.setText(item.getTitle());
                updateLabelDate(item.getDate());
                text_Descript.setText(item.getDescription());
                makeSoundDefault();
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

    @Override
    protected void onDestroy() {
        if( ringtone != null ) ringtone.stop();
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