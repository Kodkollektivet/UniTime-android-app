package com.jotto.unitime;

/**
 * Created by johanrovala on 18/06/15.
 */
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jotto.unitime.models.Course;
import com.jotto.unitime.models.CourseDataAC;
import com.jotto.unitime.models.Event;
import com.jotto.unitime.sessionhandler.SessionHandler;
import com.jotto.unitime.util.Network;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class FragmentC extends Fragment {

    private ListView listView;
    private CourseAdapter adapter;
    public static FragmentC fragmentC;
    private FragmentActivity myContext;
    private ArrayList<CourseDataAC> courses;
    private ArrayList<CourseDataAC> originalList = new ArrayList<>();
    private CourseDataAC selectedCourse;
    View longClickedView;
    View inflatedView;
    Network network = new Network();

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
        EditText editText = (EditText) myContext.findViewById(R.id.course_code_text);
        final int translateFrom = getResources().getColor(R.color.white);
        final int translateTo = getResources().getColor(R.color.fadeInColor);
        fragmentC = this;

        editText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        courses = new ArrayList<>();
        getCoursesFromDatabase();
        populateListView();

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                    final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), translateFrom, translateTo);
                    longClickedView = view;
                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            view.setBackgroundColor((Integer) animator.getAnimatedValue());
                        }

                    });
                    colorAnimation.setDuration(1000);
                    colorAnimation.start();

                    selectedCourse = courses.get(position);
                    adapter.notifyDataSetChanged();
                    onShowDialog();

                    return false;
                }
            });
        }

    private void populateListView() {
        listView = (ListView) myContext.findViewById(R.id.listView);
        adapter = new CourseAdapter(courses);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);
    }

    private class GetCourseTask extends AsyncTask<String, Object[], Object[]> {

        @Override
        protected void onPostExecute(Object[] o) {
            if (o[0].equals("true")) {
                getCoursesFromDatabase();
                adapter.notifyDataSetChanged();
                FragmentA.fragmentA.getEventsForCourse((String) o[1]);
                FragmentD.fragmentD.refreshAdapter();

            }
            else {
                Toast.makeText(myContext, (String) o[0], Toast.LENGTH_SHORT).show();
            }
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
            List<CourseDataAC> retrievedEvents = CourseDataAC.listAll(CourseDataAC.class);
            courses.clear();
            originalList.clear();
            courses.addAll(retrievedEvents);
            originalList.addAll(retrievedEvents);
        }
    }

    private class CourseAdapter extends ArrayAdapter<CourseDataAC> implements Filterable {

        private CourseFilter filter;
        public CourseAdapter(ArrayList<CourseDataAC> list) {
            super(myContext, R.layout.course_view, list);
            originalList.addAll(courses);
        }

        @Override
        public Filter getFilter() {
            if (filter == null){
                filter  = new CourseFilter();
            }
            return filter;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)

            CourseDataAC course = this.getItem(position);
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = (LayoutInflater) myContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                itemView = inflater.inflate(R.layout.course_view, parent, false);
            }

            ImageView imageView = (ImageView)itemView.findViewById(R.id.image_icon);
            imageView.setImageResource(R.drawable.ic_list_icon);

            TextView nameText = (TextView) itemView.findViewById(R.id.course_name);
            nameText.setText(course.getName_sv());

            TextView codeText = (TextView) itemView.findViewById(R.id.course_code);
            codeText.setText(course.getCourse_code());

            TextView semesterText = (TextView) itemView.findViewById(R.id.course_semester);
            semesterText.setText(course.getName_en());

            return itemView;
        }

        private class CourseFilter extends Filter
        {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults result = new FilterResults();
                if(constraint != null && constraint.toString().length() > 0)
                {
                    ArrayList<CourseDataAC> filteredItems = new ArrayList<CourseDataAC>();

                    for (final CourseDataAC g : originalList) {
                        if (g.getName_en().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                g.getName_sv().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                g.getCourse_code().toLowerCase().contains(constraint.toString().toLowerCase()))
                            filteredItems.add(g);
                    }
                    result.count = filteredItems.size();
                    result.values = filteredItems;
                }
                else
                {
                    synchronized(this)
                    {
                        result.values = originalList;
                        result.count = originalList.size();
                    }
                }
                return result;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                if (results.count > 0) {
                    courses.clear();
                    courses.addAll((ArrayList<CourseDataAC>) results.values);
                    adapter.notifyDataSetChanged();
                }
                else if (results.count == 0){
                    adapter.notifyDataSetInvalidated();
                }
            }
        }

    }
    /*
    PopupWindow to show information of events.
     */
    public void onShowDialog() {

        LayoutInflater layoutInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        inflatedView = layoutInflater.inflate(R.layout.add_course_popup, null,false);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);

        builder.setMessage("apowdkapowkd");
        builder.setTitle("Add Course");
        builder.setPositiveButton("I'll do it!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String courseCode = selectedCourse.getCourse_code().toUpperCase();
                longClickedView.setBackgroundResource(R.color.white);
                dialog.dismiss();
                if (CourseDataAC.find(Course.class, StringUtil.toSQLName("course_code") + " = ?", courseCode.toUpperCase()).isEmpty()) {
                    if (network.isOnline(myContext)) {
                        new GetCourseTask().execute(courseCode);
                    } else {
                        Toast.makeText(myContext, "No internet connection found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(myContext, "Course is already added!", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("No thanks!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                longClickedView.setBackgroundResource(R.color.white);
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                longClickedView.setBackgroundResource(R.color.white);
            }
        });
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.testBlueHeader));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.testBlueHeader));
            }
        });
        alertDialog.show();
    }

    public void refreshAdapter() {
        getCoursesFromDatabase();
        adapter.notifyDataSetChanged();
    }


}