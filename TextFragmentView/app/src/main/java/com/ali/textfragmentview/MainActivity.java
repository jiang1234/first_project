package com.ali.textfragmentview;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/7.
 */

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private List<Fragment> list = new ArrayList<Fragment>();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        list.add(new FirstFragment());
        list.add(new SecondFragment());
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(new TextFragmentPagerAdapter(getSupportFragmentManager(),list));
    }


}
class TextFragmentPagerAdapter extends FragmentPagerAdapter{
    private List<Fragment> list;
    public TextFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    public TextFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position){
        return list.get(position);
    }
    @Override
    public int getCount(){
        return list.size();
    }
}