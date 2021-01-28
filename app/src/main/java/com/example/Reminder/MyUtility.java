package com.example.Reminder;

import java.io.File;

public class MyUtility
{
    // Удалить файл
    static public void DeleteFile(String nameFile)
    {
        if(nameFile.isEmpty()) return;
        File file = new File(nameFile);
        if( file.exists()) file.delete();
    }
}
