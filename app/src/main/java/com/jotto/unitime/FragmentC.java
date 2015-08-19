package com.jotto.unitime;

/**
 * Created by johanrovala on 18/06/15.
 */
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
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

/**
 * Created by johanrovala on 18/06/15.
 */
public class FragmentC extends Fragment {

    private ListView listView;
    private CourseAdapter adapter;
    private FragmentActivity myContext;
    private ArrayList<CourseDataAC> courses;
    private Button courseBtn;
    private CourseDataAC selectedCourse;
    private PopupWindow popupWindowAddCourse;
    private Button okButton;
    private Button noButton;
    View longClickedView;
    View inflatedView;

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
        final int translateFrom = getResources().getColor(R.color.white);
        final int translateTo = getResources().getColor(R.color.blue);

        editText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        /*editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    courseBtn.performClick();
                    InputMethodManager imm = (InputMethodManager) myContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    return true;
                }
                return false;
            }
        });*/
        courses = new ArrayList<>();
        getCoursesFromDatabase();
        populateListView();


            /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (selectedCourse == null) {
                        selectedCourse = courses.get(position);
                        adapter.notifyDataSetChanged();
                    } else if (selectedCourse == courses.get(position)) {
                        selectedCourse = null;
                        adapter.notifyDataSetChanged();
                    } else {
                        selectedCourse = courses.get(position);
                        adapter.notifyDataSetChanged();
                    }
                }

            });*/

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
                    onShowPopup(view);

                    return false;
                }
            });
        }

    private void populateListView() {
        listView = (ListView) myContext.findViewById(R.id.listView);
        adapter = new CourseAdapter(courses);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);
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
            courses.addAll(retrievedEvents);
        }
    }

    private class CourseAdapter extends ArrayAdapter<CourseDataAC> implements Filterable {

        private ArrayList<CourseDataAC> originalList;
        private ArrayList<CourseDataAC> courseDataACList;
        private CourseFilter filter;
        public CourseAdapter(ArrayList<CourseDataAC> list) {
            super(myContext, R.layout.course_view, list);
            this.courseDataACList = new ArrayList<CourseDataAC>();
            this.courseDataACList.addAll(courses);
            this.originalList = new ArrayList<CourseDataAC>();
            this.originalList.addAll(courseDataACList);
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
            imageView.setImageResource(R.drawable.ic_action_view_as_list);

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
    public void onShowPopup(View v) {

        LayoutInflater layoutInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        inflatedView = layoutInflater.inflate(R.layout.add_course_popup, null,false);

        // get device size
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);


        // set height depends on the device size
        popupWindowAddCourse = new PopupWindow(inflatedView, size.x - 100 ,size.y / 4, true );
        // set a background drawable with rounders corners
        popupWindowAddCourse.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_background_textview));
        // make it focusable to show the keyboard to enter in `EditText`
        popupWindowAddCourse.setFocusable(true);
        // make it outside touchable to dismiss the popup window
        popupWindowAddCourse.setOutsideTouchable(false);

        popupWindowAddCourse.setAnimationStyle(R.style.PopupAnimation);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popupWindowAddCourse.showAtLocation(v, Gravity.CENTER, 0, 100);

        okButton = (Button) inflatedView.findViewById(R.id.doItButton);

        okButton.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseCode = selectedCourse.getCourse_code().toUpperCase();
                popupWindowAddCourse.dismiss();
                longClickedView.setBackgroundResource(R.color.white);
                System.out.println(StringUtil.toSQLName("course_code"));
                if (CourseDataAC.find(Course.class, StringUtil.toSQLName("course_code") + " = ?", courseCode.toUpperCase()).isEmpty()) {
                    new GetCourseTask().execute(courseCode);
                } else {
                    Toast.makeText(myContext, "Course is already added!", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }
        });

    }


}