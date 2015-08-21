package com.jotto.unitime;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.astuetz.PagerSlidingTabStrip.IconTabProvider;

/**
 * Created by johanrovala on 18/06/15.
 */
public class MyPageAdapter extends FragmentPagerAdapter implements IconTabProvider {

    private int tabIcons[] = {R.mipmap.ic_action_search, R.mipmap.ic_action_view_as_list, R.mipmap.ic_action_event};
    private int currentPageSelected = 0;


    public MyPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 3;
    }
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FragmentC();
        }
        else if (position == 1) {
            return new FragmentA();
        }
        else {
            return new FragmentB();
        }
    }

    @Override
    public int getPageIconResId(int i) {
        return tabIcons[i];
    }
}
