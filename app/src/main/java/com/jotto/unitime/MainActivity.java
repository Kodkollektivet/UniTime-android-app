package com.jotto.unitime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.jotto.unitime.util.KeyboardUtil;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);

        /*
        Add viewpager to the main window.
         */
        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyPageAdapter(getSupportFragmentManager()));

        /*
        Bind the tabs to the ViewPager and sets up the style
         */
        final PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setShouldExpand(true);
        tabs.setIndicatorColor(Color.argb(-2, 255, 255, 255));
        tabs.setUnderlineColor(Color.argb(255, 240, 240, 240));
        tabs.setBackgroundResource(R.color.testBlueHeader);
        tabs.setViewPager(pager);
        pager.setOffscreenPageLimit(4);
        pager.setCurrentItem(1);


        tabs.setOnPageChangeListener(new CustomOnPageChangeListener(tabs));

    }

    /*
    Implementation of tabs, to make the icons change color aswell as the indicator moving.
     */
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

        /*
        Hide soft keyboard when switching away from fragment C
         */
        @Override
        public void onPageSelected(int i) {
            //set the previous selected page to state_selected = false
            if (i != 0 && previousPage == 0) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(tabStrip.getApplicationWindowToken(), 0);
            }
            ((LinearLayout)tabStrip.getChildAt(0)).getChildAt(previousPage).setSelected(false);
            //set the selected page to state_selected = true
            ((LinearLayout)tabStrip.getChildAt(0)).getChildAt(i).setSelected(false);
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

        //If click on info show info dialog
        if (id == R.id.action_info) {
            onShowDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Show dialog with info about creators of the app
     */
    public void onShowDialog() {

        Context context = this;
        // inflate the custom popup layout
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);

        //Title for alertDialog
        TextView myTitle = new TextView(context);
        myTitle.setText("UniTime");
        myTitle.setGravity(Gravity.CENTER);
        myTitle.setTextSize(20);
        int pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        myTitle.setHeight(pixels);
        myTitle.setTypeface(null, Typeface.BOLD);
        myTitle.setTextColor(getResources().getColor(R.color.testBlueHeader));

        builder.setCustomTitle(myTitle);

        builder.setMessage(Html.fromHtml("<font color='#565656'>A service by Kodkollektivet.</font>"));


        builder.setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.testBlueHeader));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(null, Typeface.BOLD);
            }
        });
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }
}
