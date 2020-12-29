package com.example.Reminder;

import java.io.Serializable;
import java.util.Date;

public class ReminderItem implements Serializable
{
    private String title;
    private String description;
    private Date date;
    private String audio_file;

    ReminderItem(){}

    ReminderItem(String title, String description, Date date){
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAudio_file() {
        return audio_file;
    }
    public void setAudio_file(String audio_file) {
        this.audio_file = audio_file;
    }
}
