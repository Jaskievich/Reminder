 package com.example.Reminder;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

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

        private Ringtone ringtone = null;
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
                String id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX);
                String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
                list_entries.add(title);
                String uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);
                list_entryValues.add(uri + "/" + id);
                // Do something with the title and the URI of ringtone
            }
            CharSequence[] entries =  list_entries.toArray(new CharSequence[list_entries.size()]);
            CharSequence[] entryValues =  list_entryValues.toArray(new CharSequence[list_entryValues.size()]);
            listPreference.setEntries(entries);
            listPreference.setEntryValues(entryValues);
            listPreference.setSummary(GetNameRington());

            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue(
                            (String) newValue)]);
                    listPreference.setValue(newValue.toString());
                    Uri alarmUri = Uri.parse(newValue.toString());

                    if(ringtone != null) ringtone.stop();
                    ringtone = RingtoneManager.getRingtone(getContext(), alarmUri);
                    ringtone.play();
                    return false;
                }
            });

        }

        private String GetNameRington(){

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
            String alarmUriStr = sp.getString("ringtone_preference_1", null);
            Uri  alarmUri = null;
            if( alarmUriStr == null ) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            }
            else alarmUri = Uri.parse(alarmUriStr);
            Ringtone ringtone  =  RingtoneManager.getRingtone(getContext(), alarmUri);
            return ringtone.getTitle(getContext());
        }

        @Override
        public void onStop() {
            if(ringtone != null) ringtone.stop();
            super.onStop();
        }
    }
}