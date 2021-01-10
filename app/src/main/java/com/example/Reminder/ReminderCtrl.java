package com.example.Reminder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Stack;

public class ReminderCtrl
{
    private ArrayList<ReminderItem> listReminder = null;

    static public void DeleteFile(String nameFile)
    {
        if(nameFile.isEmpty()) return;
        File file = new File(nameFile);
        if( file.exists()) file.delete();
    }

    public ArrayList<ReminderItem> getListReminder() {
        return listReminder;
    }

    public void setListReminder(ArrayList<ReminderItem> listReminder) {
        this.listReminder = listReminder;
    }

    public void sort(){
        if(listReminder.size() < 2) return;
        Collections.sort(listReminder, new Comparator<ReminderItem>() {
            @Override
            public int compare(ReminderItem o1, ReminderItem o2) {
                return o1.getDate().compareTo(o2.getDate()) ;
            }
        });
    }


    public boolean delItemByIndex(int index){
        if( index > -1 && index < listReminder.size() ){
            final String nameFile = listReminder.get(index).getAudio_file();
            if( nameFile != null) DeleteFile(nameFile);
            listReminder.remove(index);
            return true;
        }
        return false;
    }

    public ArrayList<ReminderItem> getListActualReminder(){
        sort();
        ArrayList<ReminderItem> listRes = new ArrayList<>();
        Date date = new Date();
        for (ReminderItem item: listReminder ) {
           // if( date.before(item.getDate()) ) listRes.add(item);
            if( date.getTime() < item.getDate().getTime() ) listRes.add(item);
        }
        return listRes;
    }
 /*   public Stack<ReminderItem> getStackActualReminder(){
        sort();
        Stack<ReminderItem> stackRes = new Stack<ReminderItem>();
        Date date = new Date();
        for (ReminderItem item: listReminder ) {
            // if( date.before(item.getDate()) ) listRes.add(item);
            if( date.getTime() < item.getDate().getTime() ) stackRes.push(item);
        }
        return stackRes;
    }*/

}
