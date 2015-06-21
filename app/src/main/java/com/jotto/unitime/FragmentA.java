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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jotto.unitime.models.Event;
import com.jotto.unitime.sessionhandler.SessionHandler;

import org.joda.time.DateTime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by johanrovala on 18/06/15.
 */
public class FragmentA extends Fragment {

    ArrayList<Event> events;
    ArrayAdapter<Event> adapter;

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

        if (doesDatabaseExist(myContext, "unitime.db")) {
            getEventsFromDatabase();
            populateListView();
        }
        else {
            new GetCourseInfoTask().execute("1BD105");
        }


    }

    private void getEventsFromDatabase() {
        if (doesDatabaseExist(myContext, "unitime.db")) {
            List<Event> retrievedEvents = Event.listAll(Event.class);
            events = new ArrayList<>(retrievedEvents);
        }
    }

    private void populateListView() {
        adapter = new MyListAdapter();
        ListView list = (ListView) myContext.findViewById(R.id.listView2);
        list.setAdapter(adapter);
    }

    // Check if current event is past the current time
    private boolean getCurrentTime(Event event){

        DateTime dateTime = new DateTime(System.currentTimeMillis());
        dateTime = dateTime.plusDays(10);
        String beforeCut = event.getEndtime();
        // cut the string to get the hour of day
        String[] str = beforeCut.split(":");
        String hourOfDay = str[0];
        // make another datetime object to compare the day values
        DateTime dateTime1 = new DateTime(event.getStartdate());
        return Integer.parseInt(hourOfDay) < dateTime.getHourOfDay() || dateTime1.isBeforeNow();
    }

    private class MyListAdapter extends ArrayAdapter<Event> {
        public MyListAdapter() {
            super(myContext, R.layout.event_view, events);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = (LayoutInflater) myContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                itemView = inflater.inflate(R.layout.event_view, parent, false);
            }

            Event currentEvent = events.get(position);
            /*
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
            */

            if(getCurrentTime(currentEvent)){
                System.out.println("DET ÄR SANT IAF");
                ImageView imageView = (ImageView)itemView.findViewById(R.id.image_icon);
                imageView.setImageResource(R.drawable.ic_action_view_as_list);

                TextView teacherText = (TextView) itemView.findViewById(R.id.event_teacher);
                teacherText.setTextColor(getResources().getColor(R.color.blue));
                teacherText.setText(currentEvent.getTeacher());

                TextView roomText = (TextView) itemView.findViewById(R.id.event_room);
                roomText.setText(currentEvent.getRoom());

                TextView infoText = (TextView) itemView.findViewById(R.id.event_info);
                infoText.setText(currentEvent.getInfo());

                TextView timeText = (TextView) itemView.findViewById(R.id.event_time);
                timeText.setText(currentEvent.getStarttime() + "-" + currentEvent.getEndtime());
            }else{

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
            }

            return itemView;
        }
    }

    private static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    private class GetCourseInfoTask extends AsyncTask {

        protected void onPostExecute() {
            getEventsFromDatabase();
            populateListView();
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