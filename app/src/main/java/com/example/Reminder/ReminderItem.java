package com.example.Reminder;

import java.io.Serializable;
import java.util.Date;

public class ReminderItem implements Serializable
{
    private String title;
    private String description;
    private Date date;
    private boolean isReady = false;


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

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }
}
