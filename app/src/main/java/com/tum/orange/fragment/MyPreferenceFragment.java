package com.tum.orange.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tum.orange.bluetoothmanagement.EmulatorThread;
import com.tum.orange.constants.Constant;
import com.tum.orange.javabean.MyDataBean;
import com.tum.orange.tum_lmt.DataResultShowActivity;
import com.tum.orange.tum_lmt.DeviceListActivity;
import com.tum.orange.tum_lmt.MainActivity;
import com.tum.orange.tum_lmt.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Preference setting
 * Created by Orange on 2016/9/3.
 */
public class MyPreferenceFragment extends PreferenceFragmentCompat {
    private String[] list;
    private CharSequence[] filename_entry;
    private CharSequence[] filename_entryValue;
    private File file;
    private SwitchPreference emulator_mode_preference;
    private Preference auto_connect_preference;
    private Handler fragment_data_handler;
    private EmulatorThread emulatorThread;
    private MainActivity mainActivity;
    private ListPreference deletefile_preference;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("main", "OnAttach2");
        mainActivity = (MainActivity) context;
        fragment_data_handler = mainActivity.fragment_data_handler;
        emulatorThread = new EmulatorThread(fragment_data_handler);
        emulatorThread.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.pref, s);
        System.out.println("onCreatePreferences");
        file = new File(Environment.getExternalStorageDirectory(), Constant.serializablePath);
        if (!file.exists()) {
            file.mkdir();
        }

        Preference deviceManagement = findPreference("deviceManagement");
        deviceManagement.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivityForResult(new Intent(mainActivity, DeviceListActivity.class), Constant.REQUEST_DEVICE_INFO);
                return false;
            }
        });

        ListPreference filelist = (ListPreference) findPreference("filelist_preference");
        auto_connect_preference = findPreference("auto_connect_preference");
        emulator_mode_preference = (SwitchPreference) findPreference("emulator_mode_preference");
        deletefile_preference = (ListPreference) findPreference
                ("deletefile_preference");

        /**
         * show the all delete data file in the ListPreference
         * so you can delete these files
         */
        deletefile_preference.setOnPreferenceClickListener(new Preference
                .OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if ((preference instanceof ListPreference) && (preference.getKey().equals
                        ("deletefile_preference"))) {
                    ListPreference deleteList = (ListPreference) preference;
                    if (file.isDirectory()) {
                        list = file.list();
                        if (list.length != 0) {
                            filename_entry = new CharSequence[list.length];
                            filename_entryValue = new CharSequence[list.length];
                            for (int i = 0; i < list.length; i++) {
                                filename_entry[i] = list[i];
                                filename_entryValue[i] = Integer.toString(i);
                            }
                        } else {
                            filename_entry = new CharSequence[0];
                            filename_entryValue = new CharSequence[0];

                        }
                    }
                    deleteList.setEntries(filename_entry);
                    deleteList.setEntryValues(filename_entryValue);
                    return true;
                }
                return false;
            }
        });
        /**
         * delete the selected data file,if the data file exist!
         */
        deletefile_preference.setOnPreferenceChangeListener(new Preference
                .OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                int index = Integer.parseInt((String) o);
                CharSequence[] entries = deletefile_preference.getEntries();
                String deleteName = (String) entries[index];
                String path = Environment.getExternalStorageDirectory().toString() + Constant
                        .serializablePath;
                File file = new File(path, deleteName);
                if (file.exists()) {
                    boolean isDeleted = file.delete();
                    if (isDeleted) {
                        Toast.makeText(mainActivity, "delete successfully", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        Toast.makeText(mainActivity, "delete failed", Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });

/**
 * show all data files in the ListPreference
 * so you can check these files next step with a CardView or Chart!
 */
        filelist.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if ((preference instanceof ListPreference) && (preference.getKey().equals
                        ("filelist_preference"))) {

                    ListPreference fileList = (ListPreference) preference;

                    if (file.isDirectory()) {
                        list = file.list();
                        if (list.length != 0) {
                            filename_entry = new CharSequence[list.length];
                            filename_entryValue = new CharSequence[list.length];
                            for (int i = 0; i < list.length; i++) {
                                filename_entry[i] = list[i];
                                filename_entryValue[i] = Integer.toString(i);
                            }
                        } else {
                            filename_entry = new CharSequence[0];
                            filename_entryValue = new CharSequence[0];

                        }
                    }
                    fileList.setEntries(filename_entry);
                    fileList.setEntryValues(filename_entryValue);
                    return true;
                }
                return false;
            }
        });

        filelist.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //点击list列表的每一个Item触发此点击事件
                int index = Integer.parseInt((String) o);
                ListPreference filelist = (ListPreference) preference;
                CharSequence[] entries = filelist.getEntries();
                String name = (String) entries[index];
                //  antiSerializable(name, mainActivity);
                //跳转到数据展示页面
                Intent intent = new Intent(mainActivity, DataResultShowActivity.class);
                intent.putExtra("FileName", name);
                startActivity(intent);
                return true;
            }
        });

        emulator_mode_preference.setOnPreferenceChangeListener(new Preference
                .OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Boolean Emulator_Mode = (Boolean) o;
                if (Emulator_Mode) {
                    emulatorThread.isFinished = false;
                } else {
                    emulatorThread.isFinished = true;
                }
                return true;
            }
        });

    }

    private void antiSerializable(String name, MainActivity activity) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(file,
                    name)));
            ArrayList<MyDataBean> dataList = (ArrayList<MyDataBean>) ois.readObject();
            View inflate = mainActivity.getLayoutInflater().inflate(R.layout.datacardlayout, null);
            LinearLayout layout = (LinearLayout) inflate.findViewById(R.id.ll_layout);
            for (int i = 0; i < dataList.size(); i++) {
                ImageView view = new ImageView(mainActivity);
                view.setImageResource(R.mipmap.ic_launcher);
                CardView cardView = new CardView(mainActivity);
                cardView.addView(view);
                cardView.setBackgroundColor(Color.RED);
                layout.addView(cardView);
            }
            new AlertDialog.Builder(mainActivity).setTitle("HELLO").setView(inflate)
                    .setNegativeButton(
                            "OK", null).create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        System.out.println("Preference被关闭了");
        emulatorThread.isFinished = true;
        super.onDestroy();
    }
}
