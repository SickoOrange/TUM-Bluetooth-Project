package com.tum.orange.tum_lmt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity {
    private int[] mImages = {R.drawable.image1, R.drawable.image2, R.drawable
            .image3, R.drawable.image4, R.drawable.image5,R.drawable.image6};
    private List<View> mList = new ArrayList<View>();
    private ViewPager vp;
    private ArrayList<View> viewPoints;
    private LinearLayout guide_ll;
    private int lastPosition = 0;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
        initData();
        guide_ll.getChildAt(0).setEnabled(true);
        ImageAdapter adapter = new ImageAdapter(mList, this);
        vp.setAdapter(adapter);

    }

    private void initData() {
        View view;
        viewPoints = new ArrayList<View>();
        View point;
        for (int i = 0; i < mImages.length; i++) {
            view = getImageView(i);
            mList.add(view);

            point = new View(this);
            point.setBackgroundResource(R.drawable.indicator_selector);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            if (i != 0) {
                params.leftMargin = 10;
            }
            point.setEnabled(false);
            guide_ll.addView(point, params);
        }
    }

    public Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    private void initView() {
        btn = (Button) findViewById(R.id.finishGuide);
        vp = (ViewPager) findViewById(R.id.guide_ViewPager);
        guide_ll = (LinearLayout) findViewById(R.id.guide_ll);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                guide_ll.getChildAt(position).setEnabled(true);
                guide_ll.getChildAt(lastPosition).setEnabled(false);
                lastPosition = position;
                System.out.println(lastPosition);
                if (position == mList.size() - 1) {
                    btn.setVisibility(View.VISIBLE);
                } else {
                    btn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set the isFirst False, avoid to into GuideActivity
                SharedPreferences defaultSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(GuideActivity.this);
                defaultSharedPreferences.edit().putBoolean("isFirstStart", false).apply();
                //go into MainActivity
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(GuideActivity.this, MainActivity.class));
                        finish();
                    }
                }).start();
            }
        });
    }

    private View getImageView(int index) {
        View view = getLayoutInflater().inflate(R.layout.guideview, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.guide_Image);
        //imageView.setImageResource(mImages[index]);
        //imageView.setBackgroundResource(mImages[index]);
        Bitmap bitmap = readBitMap(this, mImages[index]);
        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
        // imageView.setImageBitmap(readBitMap(this, mImages[index]));
        imageView.setBackground(drawable);
        return view;
    }

    class ImageAdapter extends PagerAdapter {
        private List<View> list;

        public ImageAdapter(List<View> list, Context mContext) {
            this.list = list;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //super.destroyItem(container, position, object);
            container.removeView(list.get(position));
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
