package com.jotto.unitime;

/**
 * Created by johanrovala on 18/06/15.
 */
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jotto.unitime.models.Course;
import com.jotto.unitime.models.Event;
import com.jotto.unitime.sessionhandler.SessionHandler;
import com.orm.StringUtil;
import com.sawyer.advadapters.widget.NFRolodexArrayAdapter;
import com.sawyer.advadapters.widget.PatchedExpandableListAdapter;

import org.joda.time.DateTime;
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
public class FragmentC extends Fragment {

    private ListView listView;
    private CourseAdapter adapter;
    private FragmentActivity myContext;
    private ArrayList<Course> courses;
    private Button courseBtn;
    private Course selectedCourse;

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
        final EditText editText = (EditText) myContext.findViewById(R.id.course_code_text);
        courseBtn = (Button) myContext.findViewById(R.id.get_course_btn);
        courseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courseBtn.setEnabled(false);
                String courseCode = editText.getText().toString();
                if (Course.find(Course.class, StringUtil.toSQLName("course_code") + " = ?", courseCode.toUpperCase()).isEmpty()) {
                    new GetCourseTask().execute(courseCode);
                } else {
                    Toast.makeText(myContext, "Course already added!", Toast.LENGTH_SHORT).show();
                    courseBtn.setEnabled(true);
                }
                editText.setText("");
                adapter.notifyDataSetChanged();
            }
        });
        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    courseBtn.performClick();
                    return true;
                }
                return false;
            }
        });
        courses = new ArrayList<>();
        getCoursesFromDatabase();
        populateListView();

        final Button deleteBtn = (Button) myContext.findViewById(R.id.delete_course_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCourse != null) {
                    FragmentA.fragmentA.deleteEventsCourseRemoved(selectedCourse);
                    selectedCourse.delete();
                    courses.remove(selectedCourse);
                    adapter.notifyDataSetChanged();
                    //adapter.oldView.setBackgroundColor(getResources().getColor(R.color.caldroid_transparent));
                } else {
                    Toast.makeText(myContext, "Course is null!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedCourse == null) {
                    selectedCourse = courses.get(position);
                    adapter.notifyDataSetChanged();
                }
                else if(selectedCourse == courses.get(position)) {
                    selectedCourse = null;
                    adapter.notifyDataSetChanged();
                }
                else {
                    selectedCourse = courses.get(position);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void populateListView() {
        listView = (ListView) myContext.findViewById(R.id.listView);
        adapter = new CourseAdapter(courses);
        listView.setAdapter(adapter);
        //listView.setChildDivider(getResources().getDrawable(R.drawable.child_divider));
    }

    private class GetCourseTask extends AsyncTask<String, Object[], Object[]> {

        @Override
        protected void onPostExecute(Object[] o) {
            if (o[0].equals("true")) {
                getCoursesFromDatabase();
                adapter.notifyDataSetChanged();
                FragmentA.fragmentA.getEventsForCourse((String) o[1]);
            }
            else {
                Toast.makeText(myContext, (String) o[0], Toast.LENGTH_SHORT).show();
            }
            courseBtn.setEnabled(true);
        }

        @Override
        protected Object[] doInBackground(String... params) {
            SessionHandler sessionHandler = new SessionHandler();
            HashMap<String, String> map = new HashMap<>();
            map.put("course", params[0]);
            String message = sessionHandler.getCourse(map);
            return new Object[]{ message, params[0] };
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

    private class CourseAdapter extends ArrayAdapter<Course> {

        public CourseAdapter(ArrayList<Course> list) {
            super(myContext, R.layout.course_view, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)

            Course course = this.getItem(position);
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = (LayoutInflater) myContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                itemView = inflater.inflate(R.layout.course_view, parent, false);
            }

            ImageView imageView = (ImageView)itemView.findViewById(R.id.image_icon);
            imageView.setImageResource(R.drawable.ic_action_view_as_list);

            TextView nameText = (TextView) itemView.findViewById(R.id.course_name);
            nameText.setText(course.getName_sv());

            TextView codeText = (TextView) itemView.findViewById(R.id.course_code);
            codeText.setText(course.getCourse_code());

            TextView semesterText = (TextView) itemView.findViewById(R.id.course_semester);
            semesterText.setText(course.getSemester() + "-" + course.getYear());

            if (course == selectedCourse) {
                itemView.setBackgroundColor(getResources().getColor(R.color.darkerlightgrey));
            }
            else {
                itemView.setBackgroundColor(getResources().getColor(R.color.caldroid_transparent));
            }
            return itemView;
        }

    }

}