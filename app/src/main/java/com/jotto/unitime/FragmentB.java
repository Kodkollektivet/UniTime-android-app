package com.jotto.unitime;

/**
 * Created by johanrovala on 18/06/15.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roomorama.caldroid.CaldroidFragment;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;


public class FragmentB extends Fragment {

    private FragmentActivity myContext;
    Calendar cal;
    Date date;

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b, container, false);

        return inflater.inflate(R.layout.fragment_b,container,false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CaldroidFragment calDroid = new CaldroidFragment();
        Bundle args = new Bundle();
        cal = Calendar.getInstance();
        date = new Date(System.currentTimeMillis());
        calDroid.setMinDate(date);
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        calDroid.setArguments(args);


        /*
        Testing different colored cells
         */


        // first date
        DateTime dateTime = new DateTime(System.currentTimeMillis());
        dateTime = dateTime.plusDays(3);

        // second date
        DateTime secondDateTime = new DateTime(System.currentTimeMillis());
        secondDateTime = secondDateTime.plusDays(5);

        // third date
        DateTime thirdDateTime = new DateTime(System.currentTimeMillis());
        thirdDateTime = thirdDateTime.plusDays(6);

        // assigning new backgroundresorces for three cells (dates)
        calDroid.setBackgroundResourceForDate(R.color.blue, dateTime.toDate());
        calDroid.setBackgroundResourceForDate(R.color.darkgreen, secondDateTime.toDate());
        calDroid.setBackgroundResourceForDate(R.color.blue, thirdDateTime.toDate());

        android.support.v4.app.FragmentTransaction t = myContext
                .getSupportFragmentManager().beginTransaction();
        t.replace(R.id.llCalendar, calDroid);
        t.commit();
    }
}
