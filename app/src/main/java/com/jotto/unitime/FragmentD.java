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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jotto.unitime.models.Course;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by johanrovala on 18/06/15.
 */
public class FragmentD extends Fragment {

    private ListView listView;
    public static FragmentD fragmentD;
    private MyCoursesAdapter adapter;
    private FragmentActivity myContext;
    private ArrayList<Course> courses;
    private Course selectedCourse;
    private PopupWindow popupWindowDeleteCourse;
    private Button okButton;
    private Button noButton;
    View longClickedView;
    View inflatedView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_d, container, false);

    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final int translateFrom = getResources().getColor(R.color.white);
        final int translateTo = getResources().getColor(R.color.blue);
        fragmentD = this;
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
                    onShowPopup(view);

                    return false;
                }
            });
        }

    private void populateListView() {
        listView = (ListView) myContext.findViewById(R.id.listViewMyCourses);
        adapter = new MyCoursesAdapter(courses);
        listView.setAdapter(adapter);
    }

    private static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    private void getCoursesFromDatabase() {
        if (doesDatabaseExist(myContext, "unitime.db")) {
            List<Course> retrievedCourses = Course.listAll(Course.class);
            courses.clear();
            courses.addAll(retrievedCourses);
        }
    }

    private class MyCoursesAdapter extends ArrayAdapter<Course> {

        public MyCoursesAdapter(ArrayList<Course> list) {
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
            imageView.setImageResource(R.drawable.ic_list_icon);

            TextView nameText = (TextView) itemView.findViewById(R.id.course_name);
            nameText.setText(course.getName_sv());

            TextView codeText = (TextView) itemView.findViewById(R.id.course_code);
            codeText.setText(course.getCourse_code());

            TextView semesterText = (TextView) itemView.findViewById(R.id.course_semester);
            semesterText.setText(course.getName_en());

            return itemView;
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
        popupWindowDeleteCourse = new PopupWindow(inflatedView, size.x - 100 ,size.y / 4, true );
        // set a background drawable with rounders corners
        popupWindowDeleteCourse.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_background_textview));
        // make it focusable to show the keyboard to enter in `EditText`
        popupWindowDeleteCourse.setFocusable(true);
        // make it outside touchable to dismiss the popup windowpopupWindowAddCourse.setOutsideTouchable(false);

        popupWindowDeleteCourse.setAnimationStyle(R.style.PopupAnimation);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popupWindowDeleteCourse.showAtLocation(v, Gravity.CENTER, 0, 100);

        okButton = (Button) inflatedView.findViewById(R.id.doItButton);

        okButton.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindowDeleteCourse.dismiss();
                longClickedView.setBackgroundResource(R.color.white);
                FragmentA.fragmentA.deleteEventsCourseRemoved(selectedCourse);
                selectedCourse.delete();
                courses.remove(selectedCourse);
                adapter.notifyDataSetChanged();
            }
        });

    }

    public void refreshAdapter() {
        getCoursesFromDatabase();
        adapter.notifyDataSetChanged();
    }
}