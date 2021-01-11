package com.example.Reminder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.transition.Transition;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ActivityItem extends AppCompatActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private EditText editTitle, editDescript, editDate, editTime;
    final private Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog datePickerDialog = null;
    private TimePickerDialog timePickerDialog = null;
    private MyDialogAudio dialogAudio = null;
    private EditText textFileAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        editTitle = (EditText)findViewById(R.id.editText_title) ;
        editDescript = (EditText)findViewById(R.id.editTextMultiLine_descr);
        editDate = (EditText) findViewById(R.id.editText_date);
        editTime = (EditText) findViewById(R.id.editTextTime);
        textFileAudio = (EditText) findViewById(R.id.editTextFileAudio);
        final Button btn_app = (Button) findViewById(R.id.button_app);
        final ImageButton btn_date = (ImageButton)findViewById(R.id.imageButton_date);
        final Button btn_cancel = (Button) findViewById(R.id.button_cancel);
        final ImageButton btn_del_file = (ImageButton) findViewById(R.id.imageButton_del_File);
        btn_date.setOnClickListener(this);
        btn_app.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_del_file.setOnClickListener(this);
        editDate.setOnClickListener(this);
        editTime.setOnClickListener(this);
        final ImageButton btn_audio = (ImageButton) findViewById(R.id.img_btn_audio);
        btn_audio.setOnClickListener(this);

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            ReminderItem item = (ReminderItem) arguments.getSerializable(ReminderItem.class.getSimpleName());
            if (item != null) {
                editTitle.setText(item.getTitle());
                updateLabelDate(item.getDate());
                editDescript.setText(item.getDescription());
                myCalendar.setTime(item.getDate());
                if( item.getAudio_file()!=null && !item.getAudio_file().isEmpty() ) {
                    File file = new File(item.getAudio_file());
                    if( file.exists() )  textFileAudio.setText(item.getAudio_file());
                }
            }
        }
    }

    private void updateLabelDate(Date date)
    {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        editDate.setText(sdf.format(date));
        sdf.applyPattern("HH:mm");
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
        if( dialogAudio!=null ) {
            item.setAudio_file(dialogAudio.getPathName());
        }
        Intent intent = new Intent();
        intent.putExtra(ReminderItem.class.getSimpleName(), item);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void ShowDateDialog()
    {
        if( datePickerDialog == null )
            datePickerDialog = new DatePickerDialog(this, this, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private String absolutePathAudiFile(String fileName)
    {
        final String dir = Environment.getExternalStorageDirectory().toString();
        StringBuilder str = new StringBuilder();
        if (!fileName.startsWith(dir)) {
            str.append(dir);
            str.append("/");
        }
        str.append(fileName);
        if (!fileName.endsWith(".3gpp")) str.append(".3gpp");
        return str.toString();
    }

    private String getDateTimeAsNameFile()
    {
        StringBuilder strb = new StringBuilder();
        strb.append("f");
        strb.append(editDate.getText().toString());
        strb.append("-");
        strb.append(editTime.getText().toString());
        for(int i = 0; i < strb.length(); ++i) {
            char ch = strb.charAt(i);
            if ( ch == '/' || ch == '-' || ch == ':') strb.setCharAt(i, '_');
        }
        return strb.toString();
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
                ShowDateDialog();
                break;
            case R.id.editText_date:
                ShowDateDialog();
                break;
            case R.id.editTextTime:
                if(timePickerDialog == null)
                    timePickerDialog = new TimePickerDialog(this, this,  myCalendar.get(Calendar.HOUR),
                        myCalendar.get(Calendar.MINUTE),/* DateFormat.is24HourFormat(this)*/true);
                timePickerDialog.show();
                break;
            case R.id.img_btn_audio:
                if( dialogAudio == null ) dialogAudio = new MyDialogAudio(this);
                String fileName = textFileAudio.getText().toString();
                if(fileName.isEmpty()){
                    fileName = getDateTimeAsNameFile();
                }
                dialogAudio.setPathName(absolutePathAudiFile(fileName));
                dialogAudio.show(getFragmentManager());
                break;
            case R.id.imageButton_del_File:
                final String nameFile = textFileAudio.getText().toString();
                ReminderCtrl.DeleteFile(nameFile);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
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

    @SuppressLint("ValidFragment")
    public class MyDialogAudio extends DialogFragment
    {
        private boolean isShowDialog = false;
        private MediaRecorder recorder = null;
        private Context context;

        public String getPathName() {
            return pathName;
        }

        public void setPathName(String pathName) {
            this.pathName = pathName;
        }

        private String pathName;


        @SuppressLint("ValidFragment")
        public MyDialogAudio(Context context){
            this.context = context;
        }

        private void releaseRecorder() {
            if (recorder != null) {
                recorder.release();
                recorder = null;
            }
        }

        private void startRecord() throws IllegalStateException, IOException {
            releaseRecorder();
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(pathName);
            recorder.prepare();
            recorder.start();   // Recording is now started
        }

        private boolean stopRecord()  {
            if( recorder != null){
                try {
                    recorder.stop();
                    //   recorder.reset();   // You can reuse the object by going back to setAudioSource() step
                    recorder.release(); // Now the object cannot be reused
                } catch (IllegalStateException e){
                    Toast.makeText(context, "Ошибка записи аудио-файла", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
            return false;
        }

        private void cancelRecord()
        {
            // Остановить
            // Удалить файл
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Говорите")
                    .setPositiveButton(R.string.stop, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                            if( stopRecord() ) textFileAudio.setText(pathName);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            cancelRecord();
                        }
                    });
            //builder.setView(R.);
            // Create the AlertDialog object and return it
            return builder.create();
        }

        public void show(FragmentManager fr_mng){
            if( !isShowDialog ) {
                try {
                    startRecord();
                    isShowDialog = true;
                    show(fr_mng, "dialogAudio");
                } catch (IllegalStateException | IOException e) {
                    e.printStackTrace();
                    pathName = "";
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onCancel(DialogInterface dialog)
        {
            cancelRecord();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            isShowDialog = false;
            super.onDismiss(dialog);
        }
    }
}