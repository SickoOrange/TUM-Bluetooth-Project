package com.tum.orange.fragment;

import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.tum.orange.tum_lmt.R;

/**
 * Created by Orange on 2016/9/3.
 */
public class MyPreferenceFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.pref, s);
        ListPreference downloadType = (ListPreference) findPreference("downloadType");


    }

}
