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
        return (position == 0)? "List View" : "Calendar" ;
    }
    @Override
    public int getCount() {
        return 2;
    }
    @Override
    public Fragment getItem(int position) {
        return (position == 0) ? new FragmentA() : new FragmentB();
    }
}
