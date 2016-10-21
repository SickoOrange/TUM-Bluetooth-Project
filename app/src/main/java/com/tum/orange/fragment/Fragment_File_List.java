package com.tum.orange.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tum.orange.constants.Constant;
import com.tum.orange.tum_lmt.DataResultShowActivity;
import com.tum.orange.tum_lmt.MainActivity;
import com.tum.orange.tum_lmt.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Orange on 21.10.2016.
 */

public class Fragment_File_List extends Fragment {

    private View view;
    private ListView listView;
    private File file;
    private String[] list;
    private MainActivity mActivity;
    private ArrayAdapter<String> adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
        file = new File(Environment.getExternalStorageDirectory(), Constant.serializablePath);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void setFileName() {
        ArrayList<String> arrayList = new ArrayList<String>();
        System.out.println("upDataFileName");

        if (file.isDirectory()) {
            list = file.list();
            if (list.length != 0) {
                for (int i = 0; i < list.length; i++) {
                    arrayList.add(list[i]);
                }
            }
        }
        adapter = new ArrayAdapter<String>(mActivity, R.layout.fine_name_textview, arrayList);
        listView.setAdapter(adapter);
    }

    public void upData() {
        setFileName();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_filelist, null);
        listView = (ListView) view.findViewById(R.id.file_ListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(listView.getAdapter().getItem(position));
                String FileName= (String) listView.getAdapter().getItem(position);
                Intent intent = new Intent(mActivity, DataResultShowActivity.class);
                intent.putExtra("FileName", FileName);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
