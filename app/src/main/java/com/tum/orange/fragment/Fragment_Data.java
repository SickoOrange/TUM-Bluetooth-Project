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
public class Fragment_Data extends Fragment {
    private View mViewContent; // 缓存视图内容
    private TextView tv_data;
    /**
     * 定义一个handler对象 用于与activity进行交互 此时
     * activity拿到handler的对象 可以发送消息给fragment_data
     */
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                System.out.println("得到消息");
            } else if (msg.what == 2) {
                System.out.println("得到消息+22");
            }
        }
    };
    private MainActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mViewContent == null) {
            mViewContent = inflater.inflate(R.layout.fragment_data, null);
        }

        // 缓存View判断是否含有parent, 如果有需要从parent删除, 否则发生已有parent的错误.
        ViewGroup parent = (ViewGroup) mViewContent.getParent();
        if (parent != null) {
            parent.removeView(mViewContent);
        }
        tv_data = (TextView) mViewContent.findViewById(R.id.tv_data);
        Log.e("main", "onCreateView1");
        return mViewContent;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 显示Fragment的Tag信息s
        tv_data.setVisibility(View.VISIBLE);
        tv_data.setText("Fragment:" + getTag());
        System.out.println(getTag());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("main", "onDestroyView1");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("main", "OnAttach1");
        mActivity = (MainActivity) context;
        mActivity.setHandler(handler);

    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("main", "onCreate1");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("main", "onResume1");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("main", "onPause1");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("main", "onDestroy1");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("main", "onDetach1");
    }

    public void printfragment1() {
        System.out.println("this is fragment1");
    }
}

