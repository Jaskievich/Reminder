package com.example.Reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ActivityItem extends AppCompatActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private EditText editTitle, editDescript, editDate, editTime;
    final private Calendar myCalendar = Calendar.getInstance();
 //   final private Calendar myCalendar = new GregorianCalendar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        final Button btn_app = (Button) findViewById(R.id.button_app);
        editTitle = (EditText)findViewById(R.id.editText_title) ;
        editDescript = (EditText)findViewById(R.id.editTextMultiLine_descr);
        editDate = (EditText) findViewById(R.id.editText_date);
        editTime = (EditText) findViewById(R.id.editTextTime);
        final ImageButton btn_date = (ImageButton)findViewById(R.id.imageButton_date);
        final Button btn_cancel = (Button) findViewById(R.id.button_cancel);
        btn_date.setOnClickListener(this);
        btn_app.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        editDate.setOnClickListener(this);
        editTime.setOnClickListener(this);

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            ReminderItem item = (ReminderItem) arguments.getSerializable(ReminderItem.class.getSimpleName());
            if (item != null) {
                editTitle.setText(item.getTitle());
                updateLabelDate(item.getDate());
                editDescript.setText(item.getDescription());
                myCalendar.setTime(item.getDate());
            }
        }
    }

    private void updateLabelDate(Date date) {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        editDate.setText(sdf.format(date));
        sdf.applyPattern("HH:MM");
        editTime.setText(sdf.format(date));
    }

    private void apple()
    {
        if( editTitle.length() == 0) {
            Toast.makeText(this, "Введите название заголовка", Toast.LENGTH_LONG).show();
            return ;
        }
        if( editDate.length()==0 || editTime.length() == 0){
            Toast.makeText(this, "Введите дату и время", Toast.LENGTH_LONG).show();
            return;
        }
        ReminderItem item = new ReminderItem();
        item.setTitle(editTitle.getText().toString());
        item.setDescription(editDescript.getText().toString());
        item.setDate(myCalendar.getTime());
        Intent intent = new Intent();
        intent.putExtra(ReminderItem.class.getSimpleName(), item);
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public void onClick(View v)
    {
        switch(v.getId()){
            case R.id.button_app:
                apple();
                break;
            case R.id.button_cancel:
                finish();
                break;
            case R.id.imageButton_date:
                new DatePickerDialog(this, this, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.editText_date:
                new DatePickerDialog(this, this, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.editTextTime:
                new TimePickerDialog(this, this,  myCalendar.get(Calendar.HOUR),
                        myCalendar.get(Calendar.MINUTE),/* DateFormat.is24HourFormat(this)*/true).show();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, month);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        int hour = myCalendar.get(Calendar.HOUR);
        int minute = myCalendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, this, hour, minute,
               /* DateFormat.is24HourFormat(this)*/true);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
        int year = myCalendar.get(Calendar.YEAR);
        int month = myCalendar.get(Calendar.MONTH);
        int dayOfMonth = myCalendar.get(Calendar.DAY_OF_MONTH);
        myCalendar.set(year, month, dayOfMonth, hourOfDay, minute);
        updateLabelDate(myCalendar.getTime());
    }
}