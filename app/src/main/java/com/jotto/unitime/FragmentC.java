package com.jotto.unitime;

/**
 * Created by johanrovala on 18/06/15.
 */
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Typeface;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jotto.unitime.models.Course;
import com.jotto.unitime.models.CourseDataAC;
import com.jotto.unitime.sessionhandler.SessionHandler;
import com.jotto.unitime.util.KeyboardUtil;
import com.jotto.unitime.util.Network;
import com.orm.StringUtil;
import java.io.File;
import java.util.ArrayList;
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
    Network network = new Network();
    EditText editText;


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
        editText = (EditText) myContext.findViewById(R.id.course_code_text);
        final int translateFrom = getResources().getColor(R.color.white);
        final int translateTo = getResources().getColor(R.color.grey);
        fragmentC = this;
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    KeyboardUtil.hideSoftKeyboard(myContext, editText);
                }
                return false;
            }
        });

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), translateFrom, translateTo);
                longClickedView = view;
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        view.setBackgroundColor((Integer) animator.getAnimatedValue());
                    }

                });
                colorAnimation.setDuration(300);
                colorAnimation.start();

                selectedCourse = courses.get(position);
                adapter.notifyDataSetChanged();
                onShowDialog();
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
                FragmentA.fragmentA.getEventsForCourse((String) o[1], (String) o[2]);
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
            return new Object[]{ message, params[0], params[1] };
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
            imageView.setImageResource(R.drawable.course_icon);

            TextView nameText = (TextView) itemView.findViewById(R.id.course_name);
            nameText.setText(course.getName_en());

            TextView codeText = (TextView) itemView.findViewById(R.id.course_code);
            codeText.setText(course.getCourse_code());

            TextView semesterText = (TextView) itemView.findViewById(R.id.course_semester);
            semesterText.setText(course.getName_sv());

            TextView locationText = (TextView) itemView.findViewById(R.id.course_location);
            locationText.setText(course.getLocation());

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
                                g.getCourse_code().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filteredItems.add(g);
                        }
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

        // inflate the custom popup layout
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);

        //Title for alertDialog
        TextView myTitle = new TextView(myContext);
        myTitle.setText("Add course");
        myTitle.setGravity(Gravity.CENTER);
        myTitle.setTextSize(20);
        int pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        myTitle.setHeight(pixels);
        myTitle.setTypeface(null, Typeface.BOLD);
        myTitle.setTextColor(getResources().getColor(R.color.testBlueHeader));

        builder.setCustomTitle(myTitle);

        builder.setMessage(Html.fromHtml("<font color='#565656'>Do you want to add this course?</font>"));


        builder.setPositiveButton("I'll do it!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String courseCode = selectedCourse.getCourse_code().toUpperCase();
                String location = selectedCourse.getLocation();
                longClickedView.setBackgroundResource(R.color.white);
                editText.setText("Search...");
                dialog.dismiss();

                if (Course.find(Course.class, StringUtil.toSQLName("course_code") + " = ? and " +
                        StringUtil.toSQLName("course_location") + " = ?", courseCode, location).isEmpty()) {
                    if (network.isOnline(myContext)) {
                        new GetCourseTask().execute(courseCode, location);
                    } else {
                        Toast.makeText(myContext, "No internet connection found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(myContext, "Course is already added!", Toast.LENGTH_SHORT).show();
                }
                editText.setText("");
                KeyboardUtil.hideSoftKeyboard(myContext, editText);
                adapter.notifyDataSetChanged();

            }
        });

        builder.setNegativeButton("No thanks!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                longClickedView.setBackgroundResource(R.color.white);
                KeyboardUtil.hideSoftKeyboard(myContext, editText);
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
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(null, Typeface.BOLD);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(null, Typeface.BOLD);
            }
        });
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

    public void refreshAdapter() {
        getCoursesFromDatabase();
        adapter.notifyDataSetChanged();
    }


}