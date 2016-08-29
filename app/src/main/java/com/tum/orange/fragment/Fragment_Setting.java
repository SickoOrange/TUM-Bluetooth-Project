package com.tum.orange.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tum.orange.tum_lmt.MainActivity;
import com.tum.orange.tum_lmt.R;

/**
 * Created by Orange on 2016/8/22.
 */
public class Fragment_Setting extends Fragment {

    private TextView tv_data_1;
    private View mViewContent; // 缓存视图内容
    public Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mViewContent == null) {
            mViewContent = inflater.inflate(R.layout.fragment_setting, container, false);
        }

        // 缓存View判断是否含有parent, 如果有需要从parent删除, 否则发生已有parent的错误.
        ViewGroup parent = (ViewGroup) mViewContent.getParent();
        if (parent != null) {
            parent.removeView(mViewContent);
        }
        Log.e("main", "onCreateView2");
        tv_data_1 = (TextView) mViewContent.findViewById(R.id.tv_data_1);
        mViewContent.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.what = 2;
                handler.sendMessage(msg);


            }
        });
        return mViewContent;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 显示Fragment的Tag信息
        tv_data_1.setText("Fragment:" + getTag());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("main", "onDestroyView2");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("main", "OnAttach2");
        MainActivity mainActivity = (MainActivity) context;
        handler = mainActivity.fragment_data_handler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("main", "onCreate2");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("main", "onResume2");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("main", "onPause2");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("main", "onDestroy2");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("main", "onDetach2");
    }

    public void printfragment2() {
        System.out.println("this is fragment2");
    }
}

