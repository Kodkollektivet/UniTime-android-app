package com.jotto.unitime;

/**
 * Created by johanrovala on 18/06/15.
 */
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jotto.unitime.models.Event;
import com.jotto.unitime.sessionhandler.SessionHandler;
import com.sawyer.advadapters.widget.NFRolodexArrayAdapter;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by johanrovala on 18/06/15.
 */
public class FragmentA extends Fragment {

    public static FragmentA fragmentA;
    ExpandableListView expandableListView;
    ArrayList<Event> events;
    EventDateAdapter adapter;

    private FragmentActivity myContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_a,container,false);

    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentA = this;
        events = new ArrayList<>();
        getEventsFromDatabase();
        populateListView();
    }

    public void getEventsForCourse(String courseCode) {
        new GetCourseInfoTask().execute(courseCode);
        FragmentB.fragmentB.updateList(events);
    }

    private void getEventsFromDatabase() {
        if (doesDatabaseExist(myContext, "unitime.db")) {
            List<Event> retrievedEvents = Event.listAll(Event.class);
            events.clear();
            events.addAll(retrievedEvents);
        }
    }

    private void populateListView() {
        expandableListView = (ExpandableListView) myContext.findViewById(R.id.listView2);
        adapter = new EventDateAdapter(myContext, events);
        expandableListView.setAdapter(adapter);
    }

    // Check if current event is past the current time
    private boolean getCurrentTime(Event event){
        String beforeCut = event.getEndtime();
        // cut the string to get the hour of day
        String[] str = beforeCut.split(":");
        String hourOfDay = str[0];
        String minuteOfDay = str[1];

        // Create new datetime object with our event start date
        DateTime compareDate = new DateTime(event.getStartdate());
        // add the hour of day
        compareDate = compareDate.withHourOfDay(Integer.parseInt(hourOfDay));
        // add the minute of hour
        compareDate = compareDate.withMinuteOfHour(Integer.parseInt(minuteOfDay));
        // make another datetime object to compare the day values
        return compareDate.isBeforeNow();
    }


    private class EventDateAdapter extends NFRolodexArrayAdapter<LocalDate, Event> {

        public EventDateAdapter(Context activity, Collection<Event> items) {
            super(activity, items);
        }

        @Override
        public LocalDate createGroupFor(Event childItem) {
            //This is how the adapter determines what the headers are and what child items belong to it
            return LocalDate.parse(childItem.getStartdate());
        }

        @Override
        public View getChildView(LayoutInflater inflater, int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            //Inflate your view
            View itemView = convertView;
            if (itemView == null) {
                inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = inflater.inflate(R.layout.event_view, parent, false);
            }

            //Gets the Event data for this view
            Event event = getChild(groupPosition, childPosition);

            //Fill view with event data
            if(getCurrentTime(event)) {
                ImageView imageView = (ImageView) itemView.findViewById(R.id.image_icon);
                imageView.setImageResource(R.drawable.ic_action_view_as_list);

                TextView teacherText = (TextView) itemView.findViewById(R.id.event_teacher);
                teacherText.setText(event.getTeacher());
                teacherText.setTextColor(getResources().getColor(R.color.darkturcoise));

                TextView roomText = (TextView) itemView.findViewById(R.id.event_room);
                roomText.setText(event.getRoom());

                TextView infoText = (TextView) itemView.findViewById(R.id.event_info);
                infoText.setText(event.getInfo());

                TextView timeText = (TextView) itemView.findViewById(R.id.event_time);
                timeText.setText(event.getStarttime() + "-" + event.getEndtime());

                return itemView;
            }else{
                ImageView imageView = (ImageView)itemView.findViewById(R.id.image_icon);
                imageView.setImageResource(R.drawable.ic_action_view_as_list);

                TextView teacherText = (TextView) itemView.findViewById(R.id.event_teacher);
                teacherText.setText(event.getTeacher());

                TextView roomText = (TextView) itemView.findViewById(R.id.event_room);
                roomText.setText(event.getRoom());

                TextView infoText = (TextView) itemView.findViewById(R.id.event_info);
                infoText.setText(event.getInfo());

                TextView timeText = (TextView) itemView.findViewById(R.id.event_time);
                timeText.setText(event.getStarttime() + "-" + event.getEndtime());

                return itemView;
            }
        }

        @Override
        public View getGroupView(LayoutInflater inflater, int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            //Inflate your header view
            View headerView = convertView;
            if (headerView == null) {
                inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                headerView = inflater.inflate(R.layout.header_view, parent, false);
            }
            //Gets the Date for this view
            LocalDate localDate = getGroup(groupPosition);

            //Fill view with date data
            DateTimeFormatter dtf = DateTimeFormat.forPattern("EEEE");
            TextView dateText = (TextView) headerView.findViewById(R.id.event_date);
            TextView dayText = (TextView) headerView.findViewById(R.id.event_date_day);
            if (localDate.equals(LocalDate.now())) {
                dayText.setText(localDate.getDayOfMonth() + "/" + localDate.getMonthOfYear());
                dateText.setText("Today");
            }
            else if (localDate.equals(LocalDate.now().plusDays(1))) {
                dayText.setText(localDate.getDayOfMonth() + "/" + localDate.getMonthOfYear());
                dateText.setText("Tomorrow");
            }
            else {
                dayText.setText(localDate.getDayOfMonth() + "/" + localDate.getMonthOfYear());
                dateText.setText(dtf.print(localDate));
            }
            return headerView;
        }

        @Override
        public boolean hasAutoExpandingGroups() {
            //This forces our group views (headers) to always render expanded.
            //Even attempting to programmatically collapse a group will not work.
            return true;
        }

        @Override
        public boolean isGroupSelectable(int groupPosition) {
            //This prevents a user from seeing any touch feedback when a group (header) is clicked.
            return false;
        }
    }

    private static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    private class GetCourseInfoTask extends AsyncTask {

        @Override
        protected void onPostExecute(Object o) {
            getEventsFromDatabase();
            adapter.clear();
            adapter.addAll(events);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            SessionHandler sessionHandler = new SessionHandler();
            sessionHandler.getEventsFromCourse(params[0].toString());
            return null;
        }
    }
}