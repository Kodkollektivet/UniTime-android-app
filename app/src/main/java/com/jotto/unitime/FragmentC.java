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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jotto.unitime.models.Course;
import com.jotto.unitime.models.Event;
import com.jotto.unitime.sessionhandler.SessionHandler;
import com.orm.StringUtil;
import com.sawyer.advadapters.widget.NFRolodexArrayAdapter;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by johanrovala on 18/06/15.
 */
public class FragmentC extends Fragment implements View.OnClickListener {

    private ExpandableListView expandableListView;
    private CourseAdapter adapter;
    private FragmentActivity myContext;
    private List<Course> courses;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_c, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button courseBtn = (Button) myContext.findViewById(R.id.get_course_btn);
        courseBtn.setOnClickListener(this);
        courses = new ArrayList<>();
        getCoursesFromDatabase();
        populateListView();
    }

    @Override
    public void onClick(View v) {
        EditText editText = (EditText) myContext.findViewById(R.id.course_code_text);
        String courseCode = editText.getText().toString();
        if (Course.find(Course.class, StringUtil.toSQLName("course_code") + " = ?", courseCode.toUpperCase()).isEmpty()) {
            new GetCourseTask().execute(courseCode);
        }
        else {
            Toast.makeText(myContext, "Course already added!", Toast.LENGTH_LONG).show();
        }

    }

    private void populateListView() {
        expandableListView = (ExpandableListView) myContext.findViewById(R.id.listView);
        adapter = new CourseAdapter(myContext, courses);
        expandableListView.setAdapter(adapter);
    }

    private class GetCourseTask extends AsyncTask<String, Object[], Object[]> {

        @Override
        protected void onPostExecute(Object[] o) {
            if ((boolean) o[0]) {
                getCoursesFromDatabase();
                adapter.clear();
                adapter.addAll(courses);
                adapter.notifyDataSetChanged();
                FragmentA.fragmentA.getEventsForCourse((String) o[1]);
            }
            else {
                Toast.makeText(myContext, "Invalid course code!", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected Object[] doInBackground(String... params) {
            SessionHandler sessionHandler = new SessionHandler();
            String url = "http://unitime.se/api/course/";
            HashMap<String, String> map = new HashMap<>();
            map.put("course", params[0]);
            boolean succeeded = sessionHandler.getCourse(url, map);
            return new Object[]{ succeeded, params[0] };
        }
    }

    private class CourseAdapter extends NFRolodexArrayAdapter<String, Course> {

        public CourseAdapter(Context activity, Collection<Course> items) {
            super(activity, items);
        }

        @Override
        public String createGroupFor(Course childItem) {
            //This is how the adapter determines what the headers are and what child items belong to it
            return "";
        }

        @Override
        public View getChildView(LayoutInflater inflater, int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            //Inflate your view
            View itemView = convertView;
            if (itemView == null) {
                inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = inflater.inflate(R.layout.course_view, parent, false);
            }

            //Gets the Event data for this view
            Course course = getChild(groupPosition, childPosition);

            //Fill view with event data
            ImageView imageView = (ImageView)itemView.findViewById(R.id.image_icon);
            imageView.setImageResource(R.drawable.ic_action_view_as_list);

            TextView nameText = (TextView) itemView.findViewById(R.id.course_name);
            nameText.setText(course.getName());

            TextView courseCodeText = (TextView) itemView.findViewById(R.id.course_code);
            courseCodeText.setText(course.getCourse_code());

            TextView semesterText = (TextView) itemView.findViewById(R.id.course_semester);
            semesterText.setText(course.getSemester() + "-" + course.getYear());

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

            //Fill view with date data
            TextView dateText = (TextView) headerView.findViewById(R.id.event_date);
            TextView dayText = (TextView) headerView.findViewById(R.id.event_date_day);

            dateText.setText("Added Courses");
            dayText.setText("");
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

    private void getCoursesFromDatabase() {
        if (doesDatabaseExist(myContext, "unitime.db")) {
            List<Course> retrievedEvents = Course.listAll(Course.class);
            courses.clear();
            courses.addAll(retrievedEvents);
        }
    }

}