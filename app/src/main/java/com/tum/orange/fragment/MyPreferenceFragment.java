package com.tum.orange.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.tum.orange.tum_lmt.R;

import java.io.File;

/**
 * Created by Orange on 2016/9/3.
 */
public class MyPreferenceFragment extends PreferenceFragmentCompat {
    private String[] list;
    private CharSequence[] filename_entry;
    private CharSequence[] filename_entryValue;
    private ListPreference filelist;
    private File file;


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.pref, s);
        System.out.println("onCreatePreferences");
        filelist = (ListPreference) findPreference("filelist_preference");
        file = new File(Environment.getExternalStorageDirectory(), "/TUM_MEASURE");

        filelist.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                System.out.println("onPreferenceChange");
                int index = Integer.parseInt((String) o);
                ListPreference filelist = (ListPreference) preference;
                CharSequence[] entries = filelist.getEntries();
                System.out.println(entries[index]);
                filelist.setDefaultValue(index);
                return true;
            }
        });

        filelist.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                System.out.println("onPreferenceTreeClick");
                if ((preference instanceof ListPreference) && (preference.getKey().equals("filelist_preference"))) {

                    ListPreference filelist = (ListPreference) preference;

                    if (file.isDirectory()) {
                        list = file.list();
                        filename_entry = new CharSequence[list.length];
                        filename_entryValue = new CharSequence[list.length];
                    }
                    for (int i = 0; i < list.length; i++) {
                        filename_entry[i] = list[i];
                        filename_entryValue[i] = Integer.toString(i);
                    }

                    filelist.setEntries(filename_entry);
                    filelist.setEntryValues(filename_entryValue);
                    filelist.setDefaultValue("0");
                    return true;
                }
                return false;
            }
        });
    }
}
