package com.jotto.unitime;

/**
 * Created by johanrovala on 18/06/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jotto.unitime.models.Event;
import com.jotto.unitime.sessionhandler.SessionHandler;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class FragmentB extends Fragment {


    // Comment this list of fields later and declare all of their uses
    private FragmentActivity myContext;
    public static FragmentB fragmentB;
    private Calendar cal;
    private Date date1;
    private Date clickedDate;
    private PopupWindow popupWindow;
    ArrayList<Event> events;
    ArrayAdapter<Event> adapter;
    String[] importantEvents = {"Redovisning", "Tentamen", "Omtentamen"};
    CaldroidFragment calDroid;

    // i dont know how to describe what this does yet
    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }


    /*
    PopupWindow to show information of events.
     */
    public void onShowPopup(View v) {
        ArrayList<Event> currentDateList = new ArrayList<>();
        for (Event e : events){
            DateTime dateTime = new DateTime(e.getStartdate());
            if(dateTime.toDate().equals(clickedDate)){
                currentDateList.add(e);
            }
        }

        LayoutInflater layoutInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        final View inflatedView = layoutInflater.inflate(R.layout.calendar_popup, null,false);

        ListView listView = (ListView)inflatedView.findViewById(R.id.popup_listview);
        MyListAdapter myListAdapter = new MyListAdapter(currentDateList);
        listView.setAdapter(myListAdapter);

        // get device size
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);


        // set height depends on the device size
        popupWindow = new PopupWindow(inflatedView, size.x - 100 ,size.y / 2, true );
        // set a background drawable with rounders corners
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_background));
        // make it focusable to show the keyboard to enter in `EditText`
        popupWindow.setFocusable(true);
        // make it outside touchable to dismiss the popup window
        popupWindow.setOutsideTouchable(false);

        popupWindow.setAnimationStyle(R.style.PopupAnimation);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 100);

    }


    // If there is a database, fetch all of the events stored in event objects and add to arraylist
    // At first glance this method does a lot more than just getting events from the database
    // It first colors ALL events blue, then it iterates over the events again to check if any
    // Event includes info matching any string in importantEvents[]. In that case it colors it red.
    private void getEventsFromDatabase() {
        if (doesDatabaseExist(myContext, "unitime.db")) {
            List<Event> retrievedEvents = Event.listAll(Event.class);
            events = new ArrayList<>(retrievedEvents);
            if(events.size() != 0){
                for (int i = 0; i < events.size(); i++){
                    DateTime dateTime = new DateTime(events.get(i).getStartdate());
                    calDroid.setBackgroundResourceForDate(R.color.blue, dateTime.toDate());
                }
                for (int i = 0; i < events.size(); i++) {
                    DateTime dateTime = new DateTime(events.get(i).getStartdate());
                    for(int j = 0; j < importantEvents.length; j++){
                        if(events.get(i).getInfo().equals(importantEvents[j])){
                            System.out.println("TRUE");
                            calDroid.setBackgroundResourceForDate(R.color.caldroid_light_red, dateTime.toDate());
                        }
                    }
                }
            }
        }
    }

    // Check if database exists
    private static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_b,container,false);
    }

    private void getDate(Date date){
        clickedDate = date;
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentB = this;
        calDroid = new CaldroidFragment();
        Bundle args = new Bundle();
        cal = Calendar.getInstance();
        date1 = new Date(System.currentTimeMillis());
        calDroid.setMinDate(date1);
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        calDroid.setArguments(args);
        
        // testing cell touch
        final CaldroidListener caldroidListener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                getDate(date);
                onShowPopup(view);
            }
        };
        calDroid.setCaldroidListener(caldroidListener);
        android.support.v4.app.FragmentTransaction t = myContext
                .getSupportFragmentManager().beginTransaction();
        t.replace(R.id.llCalendar, calDroid);
        t.commit();

        getEventsFromDatabase();

        /*
        else {
            new GetCourseInfoTask().execute("1BD105");
        }*/
    }
    private class MyListAdapter extends ArrayAdapter<Event> {

        ArrayList<Event> list;

        public MyListAdapter(ArrayList<Event> list) {
            super(myContext, R.layout.calendar_popup, list);
            this.list = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)

            Event currentEvent = list.get(position);
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = (LayoutInflater) myContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                itemView = inflater.inflate(R.layout.event_view, parent, false);
            }

            ImageView imageView = (ImageView)itemView.findViewById(R.id.image_icon);
            imageView.setImageResource(R.drawable.ic_action_view_as_list);

            TextView teacherText = (TextView) itemView.findViewById(R.id.event_teacher);
            teacherText.setText(currentEvent.getTeacher());

            TextView roomText = (TextView) itemView.findViewById(R.id.event_room);
            roomText.setText(currentEvent.getRoom());

            TextView infoText = (TextView) itemView.findViewById(R.id.event_info);
            infoText.setText(currentEvent.getInfo());

            TextView timeText = (TextView) itemView.findViewById(R.id.event_time);
            timeText.setText(currentEvent.getStarttime() + "-" + currentEvent.getEndtime());

            return itemView;
        }
    }

    public void updateList(ArrayList<Event> list) {
        this.events = list;
    }

}
