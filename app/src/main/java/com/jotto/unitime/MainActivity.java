package com.jotto.unitime;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;


public class MainActivity extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyPageAdapter(getSupportFragmentManager()));

        // Bind the tabs to the ViewPager
        final PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setShouldExpand(true);
        tabs.setIndicatorColor(Color.argb(-17, 40, 171, 227));
        tabs.setUnderlineColor(Color.argb(-17, 40, 171, 227));
        tabs.setViewPager(pager);
        pager.setOffscreenPageLimit(4);
        pager.setCurrentItem(1);


        tabs.setOnPageChangeListener(new CustomOnPageChangeListener(tabs));

    }

    private class CustomOnPageChangeListener implements ViewPager.OnPageChangeListener{

        private PagerSlidingTabStrip tabStrip;
        private int previousPage = 1;
        //Constructor initiate with TapStrip
        //
        public CustomOnPageChangeListener(PagerSlidingTabStrip tab){
            tabStrip=tab;
            //Set the first image button in tabStrip to selected,
            ((LinearLayout)tabStrip.getChildAt(0)).getChildAt(1).setSelected(true);
        }
        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int i) {
            //set the previous selected page to state_selected = false
            ((LinearLayout)tabStrip.getChildAt(0)).getChildAt(previousPage).setSelected(false);
            //set the selected page to state_selected = true
            ((LinearLayout)tabStrip.getChildAt(0)).getChildAt(i).setSelected(true);
            //remember the current page
            previousPage=i;
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
