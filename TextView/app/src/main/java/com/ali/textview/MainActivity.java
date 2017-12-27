package com.ali.textview;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(new TextPagerAdapter());

    }
    public class TextPagerAdapter extends PagerAdapter {
        @Override
        //一共包含几个页面
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //初始化显示布局
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView tv = new TextView(MainActivity.this);
            tv.setTextSize(20);
            tv.setText("ceshi"+position);
            container.addView(tv);
            return tv;
        }
        @Override
        public void destroyItem(ViewGroup container,int position,Object object){
            container.removeView((View)object);
        }

    }


}


