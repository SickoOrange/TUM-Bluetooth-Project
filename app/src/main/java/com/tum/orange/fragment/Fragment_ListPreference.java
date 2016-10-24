package com.tum.orange.fragment;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.tum.orange.tum_lmt.R;

/**
 * Created by Orange on 20.10.2016.
 */

public class Fragment_ListPreference extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        addPreferencesFromResource(R.xml.pref);
        super.onCreate(savedInstanceState);
    }
}
