package com.jotto.unitime;

/**
 * Created by johanrovala on 18/06/15.
 */
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
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
import com.jotto.unitime.models.CourseDataAC;
import com.jotto.unitime.models.Event;
import com.jotto.unitime.models.Settings;
import com.jotto.unitime.sessionhandler.SessionHandler;
import com.jotto.unitime.widget.WidgetProvider;
import com.orm.SugarApp;
import com.orm.SugarCursorFactory;
import com.orm.SugarDb;
import com.sawyer.advadapters.widget.NFRolodexArrayAdapter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
    private Settings settings;
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
        settings = Settings.findById(Settings.class, (long) 1);

        if (settings == null) {
            settings = new Settings();
            settings.setDate(LocalDate.now().toString());
            settings.setContentLength(0);
            settings.save();
            new GetHeadinfo().execute();
            new RefreshEvents().execute();
        }
        else if (LocalDate.now().isAfter(LocalDate.parse(settings.getDate()))) {
            new GetHeadinfo().execute();
            new RefreshEvents().execute();
            settings.setDate(LocalDate.now().toString());
            settings.save();
            updateWidget();
        }

    }

    public void getEventsForCourse(String courseCode) {
        new GetCourseInfoTask().execute(courseCode);
    }

    private void getEventsFromDatabase() {
        if (doesDatabaseExist(myContext, "unitime.db")) {
            List<Event> retrievedEvents = Event.listAll(Event.class);
            events.clear();
            Collections.sort(retrievedEvents);
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
            imageView.setImageResource(R.drawable.ic_list_icon);

            TextView teacherText = (TextView) itemView.findViewById(R.id.event_teacher);
            teacherText.setText(event.getTeacher().length() == 0 ? "No teacher" : event.getTeacher());

            TextView roomText = (TextView) itemView.findViewById(R.id.event_room);
            roomText.setText(event.getRoom().length() == 0 ? "No room" : event.getRoom());

            TextView infoText = (TextView) itemView.findViewById(R.id.event_info);
            infoText.setText(event.getDesc().length() < 2 ? event.getInfo() : event.getInfo() + " (" + event.getDesc() + ")");

            TextView timeText = (TextView) itemView.findViewById(R.id.event_time);
            timeText.setText(event.getStarttime() + "-" + event.getEndtime());

            TextView courseText = (TextView) itemView.findViewById(R.id.event_course);
            courseText.setText(event.getName_sv());


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
            DateTimeFormatter dtf = DateTimeFormat.forPattern("EEEE - d/M").withLocale(Locale.US);
            DateTimeFormatter dtf2 = DateTimeFormat.forPattern(" - d/M").withLocale(Locale.US);
            DateTimeFormatter dtfWeek = DateTimeFormat.forPattern("'Week 'w").withLocale(Locale.US);
            TextView dateText = (TextView) headerView.findViewById(R.id.event_date);
            TextView dayText = (TextView) headerView.findViewById(R.id.event_date_day);
            if (localDate.equals(LocalDate.now())) {
                dayText.setText(dtfWeek.print(localDate));
                dateText.setText("Today" + dtf2.print(localDate));
                headerView.setBackgroundColor(getResources().getColor(R.color.todaygreen));
                dayText.setTextColor(getResources().getColor(R.color.grey));
                dateText.setTextColor((getResources().getColor(R.color.grey)));
            }
            else if (localDate.equals(LocalDate.now().plusDays(1))) {
                dayText.setText(dtfWeek.print(localDate));
                dateText.setText("Tomorrow" + dtf2.print(localDate));
                headerView.setBackgroundColor(getResources().getColor(R.color.white));
                dayText.setTextColor(getResources().getColor(R.color.grey));
                dateText.setTextColor((getResources().getColor(R.color.grey)));
            }
            else {
                dayText.setText(dtfWeek.print(localDate));
                dateText.setText(dtf.print(localDate));
                headerView.setBackgroundColor(getResources().getColor(R.color.white));
                dayText.setTextColor(getResources().getColor(R.color.testBlue));
                dateText.setTextColor((getResources().getColor(R.color.testBlue)));
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
            updateWidget();
            FragmentB.fragmentB.updateList();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            SessionHandler sessionHandler = new SessionHandler();
            sessionHandler.getEventsFromCourse(params[0].toString());
            return null;
        }
    }

    private class GetHeadinfo extends AsyncTask {

        @Override
        protected void onPostExecute(Object o) {
        }

        @Override
        protected Object doInBackground(Object[] params) {
            SessionHandler sh = new SessionHandler();
            sh.getHeadInfo();
            return true;
        }
    }

    private class RefreshEvents extends AsyncTask {

        @Override
        protected void onPostExecute(Object o) {
            getEventsFromDatabase();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Event.deleteAll(Event.class);
            List<Course> addedCourses = Course.listAll(Course.class);
            for (Course course : addedCourses) {
                getEventsForCourse(course.getCourse_code().toUpperCase());
            }
            return true;
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
        updateWidget();
        FragmentB.fragmentB.updateList();
    }

    private void refreshAdapter() {
        getEventsFromDatabase();
        adapter.clear();
        adapter.addAll(events);
        adapter.notifyDataSetChanged();
    }

    private void updateWidget() {
        Intent intent = new Intent(myContext, WidgetProvider.class);
        intent.setAction(WidgetProvider.UPDATE_ACTION);
        myContext.sendBroadcast(intent);
    }
}