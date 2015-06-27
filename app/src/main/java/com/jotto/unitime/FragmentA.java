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

import com.jotto.unitime.models.Course;
import com.jotto.unitime.models.Event;
import com.jotto.unitime.sessionhandler.SessionHandler;
import com.sawyer.advadapters.widget.NFRolodexArrayAdapter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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

            ImageView imageView = (ImageView)itemView.findViewById(R.id.image_icon);
            imageView.setImageResource(R.drawable.ic_action_view_as_list);

            TextView teacherText = (TextView) itemView.findViewById(R.id.event_teacher);
            teacherText.setText(event.getTeacher());

            TextView roomText = (TextView) itemView.findViewById(R.id.event_room);
            roomText.setText(event.getRoom());

            TextView infoText = (TextView) itemView.findViewById(R.id.event_info);
            infoText.setText(event.getDesc().length() < 2 ? event.getInfo() : event.getInfo()+"( "+event.getDesc()+" )");

            TextView timeText = (TextView) itemView.findViewById(R.id.event_time);
            timeText.setText(event.getStarttime() + "-" + event.getEndtime());

            TextView courseText = (TextView) itemView.findViewById(R.id.event_course);
            courseText.setText(event.getCourse_name());


            itemView.setEnabled(false);
            itemView.setFocusable(false);
            itemView.setSelected(false);
            itemView.setClickable(false);
            itemView.setFocusableInTouchMode(false);

            return itemView;
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
            DateTimeFormatter dtf = DateTimeFormat.forPattern("EEEE").withLocale(Locale.US);
            TextView dateText = (TextView) headerView.findViewById(R.id.event_date);
            TextView dayText = (TextView) headerView.findViewById(R.id.event_date_day);
            if (localDate.equals(LocalDate.now())) {
                dayText.setText(localDate.getDayOfMonth() + "/" + localDate.getMonthOfYear());
                dateText.setText("Today");
                headerView.setBackgroundColor(getResources().getColor(R.color.todaygreen));
                dayText.setTextColor(getResources().getColor(R.color.grey));
                dateText.setTextColor((getResources().getColor(R.color.grey)));
            }
            else if (localDate.equals(LocalDate.now().plusDays(1))) {
                dayText.setText(localDate.getDayOfMonth() + "/" + localDate.getMonthOfYear());
                dateText.setText("Tomorrow");
                headerView.setBackgroundColor(getResources().getColor(R.color.lightturcoise));
                dayText.setTextColor(getResources().getColor(R.color.grey));
                dateText.setTextColor((getResources().getColor(R.color.grey)));
            }
            else {
                dayText.setText(localDate.getDayOfMonth() + "/" + localDate.getMonthOfYear());
                dateText.setText(dtf.print(localDate));
                headerView.setBackgroundColor(getResources().getColor(R.color.lightturcoise));
                dayText.setTextColor(getResources().getColor(R.color.grey));
                dateText.setTextColor((getResources().getColor(R.color.grey)));
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
            refreshAdapter();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            SessionHandler sessionHandler = new SessionHandler();
            sessionHandler.getEventsFromCourse(params[0].toString());
            return null;
        }
    }

    public void deleteEventsCourseRemoved(Course course){
        for (Iterator<Event> iterator = events.iterator(); iterator.hasNext();) {
            Event event = iterator.next();
            if (event.getCourse_code().equals(course.getCourse_code())) {
                event.delete();
                iterator.remove();
            }
        }
        refreshAdapter();
    }

    private void refreshAdapter() {
        getEventsFromDatabase();
        adapter.clear();
        adapter.addAll(events);
        adapter.notifyDataSetChanged();
    }
}