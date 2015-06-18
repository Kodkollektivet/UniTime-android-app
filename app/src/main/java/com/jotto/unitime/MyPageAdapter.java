package com.jotto.unitime;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by johanrovala on 18/06/15.
 */
public class MyPageAdapter extends FragmentPagerAdapter{

    public MyPageAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Course List";
        }
        else if (position == 1) {
            return "Schedule";
        }
        else if (position == 2){
            return "Calendar";
        }
        return null;
    }
    @Override
    public int getCount() {
        return 3;
    }
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FragmentA();
        }
        else if (position == 1) {
            return new FragmentC();
        }
        else {
            return new FragmentB();
        }
    }
}
