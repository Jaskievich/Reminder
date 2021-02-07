package com.example.Reminder;

import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            final ListPreference listPreference = (ListPreference) findPreference("ringtone_preference_1");
            ArrayList<CharSequence> list_entries = new ArrayList<>();
            ArrayList<CharSequence> list_entryValues = new ArrayList<>();
            RingtoneManager manager = new RingtoneManager(getContext());
            manager.setType(RingtoneManager.TYPE_RINGTONE);
            Cursor cursor = manager.getCursor();
            while (cursor.moveToNext()) {
                String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
                list_entries.add(title);
                String uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);
                list_entryValues.add(uri);
                // Do something with the title and the URI of ringtone
            }
            CharSequence[] entries =  list_entries.toArray(new CharSequence[list_entries.size()]);
            CharSequence[] entryValues =  list_entryValues.toArray(new CharSequence[list_entryValues.size()]);
            listPreference.setEntries(entries);
            listPreference.setEntryValues(entryValues);
       //     listPreference.setDefaultValue("1");

        }
    }
}