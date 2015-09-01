package com.jotto.unitime;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.astuetz.PagerSlidingTabStrip.IconTabProvider;

/**
 * Created by johanrovala on 18/06/15.
 */
public class MyPageAdapter extends FragmentPagerAdapter implements IconTabProvider {

    /*
    Icons used for the tabs
     */
    private int tabIcons[] = {R.drawable.search_tab_icon, R.drawable.list_tab_icon, R.drawable.event_tab_icon, R.drawable.person_tab_icon};

    public MyPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 4;
    }
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FragmentC();
        }
        else if (position == 1) {
            return new FragmentA();
        }
        else if (position == 2){
            return new FragmentB();
        }
        else {
            return new FragmentD();
        }
    }

    @Override
    public int getPageIconResId(int i) {
        return tabIcons[i];
    }


}
