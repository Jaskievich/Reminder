package com.example.Reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ActivityItem extends AppCompatActivity {

    private EditText editTitle, editDescript;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        final Button btn_app = (Button) findViewById(R.id.button_app);
        editTitle = (EditText)findViewById(R.id.editText_title) ;
        editDescript = (EditText)findViewById(R.id.editTextMultiLine_descr);
        btn_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( editTitle.length() == 0) {
                    return ;
                }
                ReminderItem item = new ReminderItem();
                item.setTitle(editTitle.getText().toString());
                item.setDescription(editDescript.getText().toString());
             //   item.setDate();
                Intent intent = new Intent();
                intent.putExtra(ReminderItem.class.getSimpleName(), item);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


}