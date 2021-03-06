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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.jotto.unitime.models.Course;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Delayed;

public class FragmentD extends Fragment {

    private ListView listView;
    public static FragmentD fragmentD;
    private MyCoursesAdapter adapter;
    private FragmentActivity myContext;
    public ArrayList<Course> courses;
    private Course selectedCourse;


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
        final int translateTo = getResources().getColor(R.color.grey);
        fragmentD = this;
        courses = new ArrayList<>();
        getCoursesFromDatabase();
        populateListView();

        /*
        Show delete course popup on click.
         */
        /*
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
        });*/
        }

    /*
    Set up the adapter for the listview.
     */
    private void populateListView() {
        listView = (ListView) myContext.findViewById(R.id.listViewMyCourses);
        adapter = new MyCoursesAdapter(courses);
        listView.setAdapter(adapter);
    }

    /*
    Checks if database exists.
     */
    private static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    /*
    Get all added courses from the database and add them to the list.
     */
    private void getCoursesFromDatabase() {
        if (doesDatabaseExist(myContext, "unitime.db")) {
            List<Course> retrievedCourses = Course.listAll(Course.class);
            courses.clear();
            courses.addAll(retrievedCourses);
        }
    }

    /*
    Adapter implementation for the added courses listview.
     */
    private class MyCoursesAdapter extends ArrayAdapter<Course> {

        public MyCoursesAdapter(ArrayList<Course> list) {
            super(myContext, R.layout.course_view, list);
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)

            Course course = this.getItem(position);
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = (LayoutInflater) myContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                itemView = inflater.inflate(R.layout.my_courses_view, parent, false);
            }

            /*
            Sets up the text and icons for the course views
             */
            ImageView imageView = (ImageView)itemView.findViewById(R.id.image_icon);
            imageView.setImageResource(R.drawable.course_icon);

            TextView nameText = (TextView) itemView.findViewById(R.id.course_name);
            nameText.setText(course.getName_en());

            TextView codeText = (TextView) itemView.findViewById(R.id.course_code);
            codeText.setText(course.getCourse_code());

            TextView semesterText = (TextView) itemView.findViewById(R.id.course_semester);
            semesterText.setText(course.getName_sv());

            TextView locationText = (TextView) itemView.findViewById(R.id.course_location);
            locationText.setText(course.getCourse_location());

            ImageButton removeCourseButton = (ImageButton) itemView.findViewById(R.id.remove_icon);
            removeCourseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedCourse = courses.get(position);
                    adapter.notifyDataSetChanged();
                    onShowDialog();
                }
            });

            removeCourseButton.setAlpha(0.8f);

            return itemView;
        }
    }

    /*
    Refreshes the adapter.
     */
    public void refreshAdapter() {
        getCoursesFromDatabase();
        adapter.notifyDataSetChanged();
    }

    /*
    Set up and show the delete course popup dialog
     */
    public void onShowDialog() {

        // inflate the custom popup layout
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);

        //Title for alertDialog
        TextView myTitle = new TextView(myContext);
        myTitle.setText("Delete course");
        myTitle.setGravity(Gravity.CENTER);
        myTitle.setTextSize(20);
        int pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        myTitle.setHeight(pixels);
        myTitle.setTypeface(null, Typeface.BOLD);
        myTitle.setTextColor(getResources().getColor(R.color.testBlueHeader));

        builder.setCustomTitle(myTitle);

        builder.setMessage(Html.fromHtml("<font color='#565656'>Do you want to delete this course?</font>"));

        /*
        Delete the course on positive button click.
         */
        builder.setPositiveButton("I'll do it!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // longClickedView.setBackgroundResource(R.color.white);
                dialog.dismiss();
                Course.deleteAll(Course.class);
                Course.executeQuery("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'COURSE'");
                courses.remove(selectedCourse);
                for (Course course : courses) {
                    course.setId(null);
                    course.save();
                }
                FragmentA.fragmentA.deleteEventsCourseRemoved(selectedCourse);
                FragmentC.fragmentC.updateAddedCoursesList();
                adapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("No thanks!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // longClickedView.setBackgroundResource(R.color.white);
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //longClickedView.setBackgroundResource(R.color.white);
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

}