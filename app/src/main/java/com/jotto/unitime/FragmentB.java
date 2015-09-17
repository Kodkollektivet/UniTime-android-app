package com.jotto.unitime;

/**
 * Created by johanrovala on 18/06/15.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jotto.unitime.models.Event;
import com.roomorama.caldroid.CaldroidFragment;
import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarCellView;
import com.squareup.timessquare.CalendarPickerView;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class FragmentB extends Fragment {


    // Comment this list of fields later and declare all of their uses
    private FragmentActivity myContext;
    public static FragmentB fragmentB;
    private Calendar cal;
    private ArrayList<Date> normalDates;
    private ArrayList<Date> importantDates;
    private Date date1;
    private Date clickedDate;
    CalendarPickerView calendar;
    private TextView selectedDateTextView;
    ArrayList<Event> events;
    private String importantEvents = "redovisning|tentamen|omtentamen|exam|examination|tenta|deadline|reexam|reexamination";
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
    public void onShowPopup(Date date) {
        ArrayList<Event> currentDateList = new ArrayList<>();
        for (Event e : events){
            DateTime dateTime = new DateTime(e.getStartdate());
            if(dateTime.toDate().equals(date)){
                currentDateList.add(e);
            }
        }

        LayoutInflater layoutInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        final View inflatedView = layoutInflater.inflate(R.layout.calendar_popup, null,false);

        LocalDate selectedLocalDate = new LocalDate(date);
        DateTimeFormatter dtf = DateTimeFormat.forPattern("EEEE d/M").withLocale(Locale.US);
        selectedDateTextView = (TextView) inflatedView.findViewById(R.id.popup_textview);
        selectedDateTextView.setText(dtf.print(selectedLocalDate));

        ListView listView = (ListView)inflatedView.findViewById(R.id.popup_listview);
        Collections.sort(currentDateList);
        MyListAdapter myListAdapter = new MyListAdapter(currentDateList);
        listView.setAdapter(myListAdapter);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);

        builder.setView(inflatedView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

    }

    /*
    If there is a database, fetch all of the events stored in event objects and add to arraylist
    At first glance this method does a lot more than just getting events from the database
    It first colors ALL events blue, then it iterates over the events again to check if any
    Event includes info matching any string in importantEvents[]. In that case it colors it red.
    */
    private void getEventsFromDatabase() {
        if (doesDatabaseExist(myContext, "unitime.db")) {
            List<Event> retrievedEvents = Event.listAll(Event.class);
            events = new ArrayList<>(retrievedEvents);
            recolorCalendar();
        }
    }

    // Check if database exists
    private static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_b, container, false);

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        /*
        Settings for android square times calendar.
         */
        fragmentB = this;
        date1 = new Date(System.currentTimeMillis());

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        calendar = (CalendarPickerView) myContext.findViewById(R.id.calendar_view);
        Date today = new Date();
        calendar.init(today, nextYear.getTime())
                .withSelectedDate(today);
        calendar.setBackgroundResource(R.color.caldroid_transparent);

        // testing cell touch
        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                onShowPopup(date);
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });

        getEventsFromDatabase();
    }

    /*
    Adapter for the listview within the popup that appears when clicking a date in the calendar.
     */
    private class MyListAdapter extends ArrayAdapter<Event> {


        public MyListAdapter(ArrayList<Event> list) {
            super(myContext, R.layout.calendar_popup, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)

            Event currentEvent = this.getItem(position);
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = (LayoutInflater) myContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                itemView = inflater.inflate(R.layout.event_view, parent, false);
            }

            /*
            Setting up the texts and icons in the listview.
             */
            ImageView imageView = (ImageView)itemView.findViewById(R.id.image_icon);

            if (importantEvents.contains(currentEvent.getInfo().toLowerCase())) {
                imageView.setImageResource(R.drawable.event_icon_important);
            }
            else {
                imageView.setImageResource(R.drawable.event_icon);
            }

            TextView teacherText = (TextView) itemView.findViewById(R.id.event_teacher);
            teacherText.setText(currentEvent.getTeacher().length() == 0 ? "No teacher" : currentEvent.getTeacher());

            TextView roomText = (TextView) itemView.findViewById(R.id.event_room);
            roomText.setText(currentEvent.getRoom().length() == 0 ? "No room" : currentEvent.getRoom());

            TextView infoText = (TextView) itemView.findViewById(R.id.event_info);
            infoText.setText(currentEvent.getDesc().length() < 2 ? currentEvent.getInfo() :
                    currentEvent.getInfo() + "( " + currentEvent.getDesc() + " )");

            TextView timeText = (TextView) itemView.findViewById(R.id.event_time);
            timeText.setText(currentEvent.getStarttime() + "-" + currentEvent.getEndtime());

            TextView courseText = (TextView) itemView.findViewById(R.id.event_course);
            courseText.setText(currentEvent.getName_en());

            return itemView;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

    }

    /*
    Method for updating and recoloring the events in the calendar.
     */
    public void updateList() {
        getEventsFromDatabase();
        recolorCalendar();
    }

    private void recolorCalendar() {
        if(!events.isEmpty()){
            importantDates = new ArrayList<>();
            normalDates = new ArrayList<>();
            for (Event e : events){
                DateTime dateTime = new DateTime(e.getStartdate());
                if (importantEvents.contains(e.getInfo().toLowerCase())) {
                    importantDates.add(dateTime.toDate());
                }
                else {
                    normalDates.add(dateTime.toDate());
                }
            }
            ArrayList<CalendarCellDecorator> decoratorList = new ArrayList<>();
            decoratorList.add(new paintDates());
            calendar.setDecorators(decoratorList);
        }
    }

    /*
    Private class to paint the CalendarCellViews of our current calendar.
    CellViews with dates that are not included in our Eventlists are colored back to white.
     */
    private class paintDates implements CalendarCellDecorator{

        @Override
        public void decorate(CalendarCellView calendarCellView, Date date) {
            if(importantDates.contains(date)){
                calendarCellView.setBackgroundResource(R.color.ui_red);
                calendarCellView.setTextColor(getResources().getColor(R.color.white));
            }else if(normalDates.contains(date)){
                calendarCellView.setBackgroundResource(R.color.testBlue);
                calendarCellView.setTextColor(getResources().getColor(R.color.white));
            }else{
                calendarCellView.setBackgroundResource(R.color.white);
                calendarCellView.setTextColor(getResources().getColor(R.color.darkergrey));
            }
        }
    }

}
