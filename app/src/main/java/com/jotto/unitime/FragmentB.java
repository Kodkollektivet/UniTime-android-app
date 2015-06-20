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

import java.util.Calendar;


public class FragmentB extends Fragment {

    private FragmentActivity myContext;
    Calendar cal;

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
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        calDroid.setArguments(args);

        android.support.v4.app.FragmentTransaction t = myContext
                .getSupportFragmentManager().beginTransaction();
        t.replace(R.id.llCalendar, calDroid);
        t.commit();
    }
}
