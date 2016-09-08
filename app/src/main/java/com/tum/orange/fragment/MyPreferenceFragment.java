package com.tum.orange.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.tum.orange.constants.ConstansForBluetoothService;
import com.tum.orange.tum_lmt.DeviceListActivity;
import com.tum.orange.tum_lmt.MainActivity;
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
    private Preference device_management_preference;
    private MainActivity mActivity;
    private CheckBoxPreference send_data_preference;
    private SwitchPreference emulator_mode_preference;

    @Override
    public void onAttach(Context context) {
        mActivity = (MainActivity) context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.pref, s);
        System.out.println("onCreatePreferences");
        file = new File(Environment.getExternalStorageDirectory(), "/TUM_MEASURE");

        filelist = (ListPreference) findPreference("filelist_preference");
        device_management_preference = findPreference("device_management_preference");
        emulator_mode_preference = (SwitchPreference) findPreference("emulator_mode_preference");
        send_data_preference = (CheckBoxPreference) findPreference("send_data_preference");

        emulator_mode_preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Boolean flag = (Boolean) o;
                if (!flag) {
                    send_data_preference.setChecked(false);
                }
                return true;
            }
        });

        send_data_preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Boolean flag = (Boolean) o;
                if (flag) {
                    emulator_mode_preference.setChecked(false);
                }
                System.out.println("send_data_preference:" + o);
                return true;
            }
        });


        device_management_preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivityForResult(new Intent(mActivity.getApplicationContext(), DeviceListActivity.class), ConstansForBluetoothService.REQUEST_DEVICE_INFO);
                return true;
            }
        });

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
