package com.example.Reminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_add;
    private ArrayList<ReminderItem> listReminder = new ArrayList<>();
    private AdapterReminder adp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView lv = (ListView) findViewById(R.id.ltv);
        adp = new AdapterReminder(this, listReminder);
        lv.setAdapter(adp);
        btn_add = (Button) findViewById(R.id.button_add);
        btn_add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add:
                Intent intent = new Intent(this, ActivityItem.class);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode != RESULT_OK || data == null ) return;
        ReminderItem item = (ReminderItem) data.getSerializableExtra(ReminderItem.class.getSimpleName());
        listReminder.add(item);
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