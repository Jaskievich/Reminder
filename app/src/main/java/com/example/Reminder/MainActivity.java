package com.example.Reminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

   private ArrayList<ReminderItem> listReminder = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView lv = (ListView) findViewById(R.id.ltv);
        AdapterReminder adp = new AdapterReminder(this);
        lv.setAdapter(adp);
    }

    class AdapterReminder extends ArrayAdapter<ReminderItem>
    {
        public AdapterReminder(@NonNull Context context) {
            super(context, android.R.layout.simple_list_item_2);
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
            ((TextView) convertView.findViewById(android.R.id.text2))
                    .setText(item.getDate().toString());
            return convertView;
          //  return super.getView(position, convertView, parent);
        }
    }
}