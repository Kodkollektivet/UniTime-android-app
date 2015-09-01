package com.jotto.unitime;

/**
 * Created by johanrovala on 18/06/15.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.jotto.unitime.models.Course;
import com.jotto.unitime.models.Event;
import com.jotto.unitime.models.Settings;
import com.jotto.unitime.sessionhandler.SessionHandler;
import com.jotto.unitime.util.Network;
import com.jotto.unitime.widget.WidgetProvider;
import com.sawyer.advadapters.widget.NFRolodexArrayAdapter;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;


public class FragmentA extends Fragment {

    public static FragmentA fragmentA;
    ExpandableListView expandableListView;
    ArrayList<Event> events;
    EventDateAdapter adapter;
    private Settings settings;
    private FragmentActivity myContext;
    PtrClassicFrameLayout mPtrFrame;
    Network network = new Network();
    static ProgressDialog dialog;
    boolean isRefreshing = false;
    private String importantEvents = "redovisning|tentamen|omtentamen|exam|examination|tenta|deadline";



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

        /*
        Pull to refresh config
         */
        mPtrFrame = (PtrClassicFrameLayout) myContext.findViewById(R.id.ptr_frame);
        ((TextView) myContext.findViewById(R.id.ptr_classic_header_rotate_view_header_title)).setTextSize(16);
        ImageView iv = ((ImageView) myContext.findViewById(R.id.ptr_classic_header_rotate_view));
        iv.setMaxHeight(100);
        iv.setMaxWidth(100);
        iv.setMinimumHeight(100);
        iv.setMinimumWidth(100);
        ProgressBar pb = (ProgressBar) myContext.findViewById(R.id.ptr_classic_header_rotate_view_progressbar);
        pb.getLayoutParams().height = 45;
        pb.getLayoutParams().width = 45;
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.disableWhenHorizontalMove(true);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.0f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        // default is false
        mPtrFrame.setPullToRefresh(false);
        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);

        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                frame.post(new Runnable() {
                    @Override
                    public void run() {
                        if (network.isOnline(myContext)) {
                            new RefreshEvents().execute();
                        } else {
                            Toast.makeText(myContext, "No internet connection found.", Toast.LENGTH_SHORT).show();
                            mPtrFrame.refreshComplete();
                        }
                    }
                });
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header) && !isRefreshing && !events.isEmpty();
            }

        });


        settings = Settings.findById(Settings.class, (long) 1);

        if (network.isOnline(myContext)) {
            if (settings == null) {
                settings = new Settings();
                settings.setDate(LocalDate.now().toString());
                settings.setContentLength(0);
                settings.save();
                new GetHeadinfo().execute();
                new RefreshEvents().execute();
                updateWidget();
            } else if (LocalDate.now().isAfter(LocalDate.parse(settings.getDate()))) {
                new GetHeadinfo().execute();
                new RefreshEvents().execute();
                settings.setDate(LocalDate.now().toString());
                settings.save();
                updateWidget();
            }
        }
        else {
            Toast.makeText(myContext, "No internet connection found.", Toast.LENGTH_SHORT).show();
        }

    }

    public void getEventsForCourse(String courseCode, String location) {
        if (network.isOnline(myContext)) {
            new GetCourseInfoTask().execute(courseCode, location);
        }
        else {
            Toast.makeText(myContext, "No internet connection found.", Toast.LENGTH_SHORT).show();
        }
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

        expandableListView.setEmptyView(myContext.findViewById(R.id.empty_list_item));
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
            final int position = childPosition;
            View itemView = convertView;
            if (itemView == null) {
                inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = inflater.inflate(R.layout.event_view, parent, false);
            }

            //Gets the Event data for this view
            final Event event = getChild(groupPosition, childPosition);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.image_icon);

            if (importantEvents.contains(event.getInfo().toLowerCase())) {
                imageView.setImageResource(R.drawable.event_icon_important);
            }
            else {
                imageView.setImageResource(R.drawable.event_icon);
            }

            TextView teacherText = (TextView) itemView.findViewById(R.id.event_teacher);
            teacherText.setText(event.getTeacher().length() == 0 ? "No teacher" : event.getTeacher());

            TextView roomText = (TextView) itemView.findViewById(R.id.event_room);
            roomText.setText(event.getRoom().length() == 0 ? "No room" : event.getRoom());

            TextView infoText = (TextView) itemView.findViewById(R.id.event_info);
            infoText.setText(event.getDesc().length() < 2 ? event.getInfo() : event.getInfo() + " (" + event.getDesc() + ")");

            TextView timeText = (TextView) itemView.findViewById(R.id.event_time);
            timeText.setText(event.getStarttime() + "-" + event.getEndtime());

            TextView courseText = (TextView) itemView.findViewById(R.id.event_course);
            courseText.setText(event.getName_en());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onShowEventInfoDialog(event);
                }
            });

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
                headerView.setBackgroundColor(getResources().getColor(R.color.white));
                dayText.setTextColor(getResources().getColor(R.color.testBlue));
                dateText.setTextColor((getResources().getColor(R.color.testBlue)));
            }
            else if (localDate.equals(LocalDate.now().plusDays(1))) {
                dayText.setText(dtfWeek.print(localDate));
                dateText.setText("Tomorrow" + dtf2.print(localDate));
                headerView.setBackgroundColor(getResources().getColor(R.color.white));
                dayText.setTextColor(getResources().getColor(R.color.testBlue));
                dateText.setTextColor((getResources().getColor(R.color.testBlue)));
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
            sessionHandler.getEventsFromCourse(params[0].toString(), params[1].toString());
            return null;
        }
    }

    private class GetHeadinfo extends AsyncTask {

        @Override
        protected void onPostExecute(Object o) {
            FragmentC.fragmentC.refreshAdapter();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            SessionHandler sh = new SessionHandler();
            sh.getHeadInfo();
            return true;
        }
    }

    private class RefreshEvents extends AsyncTask {
        SessionHandler sessionHandler = new SessionHandler();
        @Override
        protected void onPostExecute(Object o) {
            refreshAdapter();
            updateWidget();
            FragmentB.fragmentB.updateList();
            expandableListView.post(new Runnable() {
                @Override
                public void run() {
                    notifyPTR();
                }
            });
            isRefreshing = false;

        }

        @Override
        protected Object doInBackground(Object[] params) {
            isRefreshing = true;
            Event.deleteAll(Event.class);
            Event.executeQuery("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'EVENT'");
            List<Course> addedCourses = Course.listAll(Course.class);
            for (Course course : addedCourses) {
                sessionHandler.getEventsFromCourse(course.getCourse_code().toUpperCase(), course.getCourse_location());
            }
            return true;
        }
    }

    public void deleteEventsCourseRemoved(Course course){
        new RefreshEvents().execute();
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

    private void notifyPTR() {
        mPtrFrame.refreshComplete();
    }

    private void showProgressDialog() {
        dialog = new ProgressDialog(myContext);
        dialog.setCancelable(true);
        dialog.setMessage(myContext.getString(R.string.update_courses));
        // set the progress to be horizontal
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // reset the bar to the default value of 0
        dialog.setProgress(0);
        dialog.setCanceledOnTouchOutside(false);
        // set the maximum value
        // display the progressbar
        dialog.show();
    }

    public static void updateProgressBar(int progress) {
        dialog.setProgress(progress);
        if (progress == dialog.getMax()) {
            dialog.dismiss();
        }
    }

    public static void setProgressBarMax(int max) {
        dialog.setMax(max);
    }

    public void showProgressDialogWindow() {
        myContext.runOnUiThread(new Runnable() {
            public void run() {
                showProgressDialog();
            }
        });
    }

    public void onShowEventInfoDialog(Event event) {

        // inflate the custom popup layout
        final AlertDialog.Builder builder = new AlertDialog.Builder(myContext, AlertDialog.THEME_HOLO_LIGHT);

        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.event_info_popup, null);

        TextView popupName_en = (TextView) view.findViewById(R.id.event_info_popup_name_en);
        popupName_en.setText(event.getName_en());

        TextView popupName_sv = (TextView) view.findViewById(R.id.event_info_popup_name_sv);
        popupName_sv.setText(event.getName_sv());

        TextView popupTime = (TextView) view.findViewById(R.id.event_info_popup_time);
        popupTime.setText(event.getStarttime() + "-" + event.getEndtime());

        TextView popupRoom = (TextView) view.findViewById(R.id.event_info_popup_room);
        popupRoom.setText(event.getRoom());

        TextView popupTeacher = (TextView) view.findViewById(R.id.event_info_popup_teacher);
        popupTeacher.setText(event.getTeacher());

        TextView popupInfo = (TextView) view.findViewById(R.id.event_info_popup_info);
        popupInfo.setText(event.getDesc().length() < 2 ? event.getInfo() : event.getInfo() + " (" + event.getDesc() + ")");

        //Title for popup
        TextView myTitle = new TextView(myContext);
        myTitle.setText("Event Info");
        myTitle.setGravity(Gravity.CENTER);
        myTitle.setTextSize(20);
        int pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        myTitle.setHeight(pixels);
        myTitle.setTypeface(null, Typeface.BOLD);
        myTitle.setTextColor(getResources().getColor(R.color.testBlueHeader));

        builder.setCustomTitle(myTitle);
        builder.setView(view);

        builder.setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.testBlueHeader));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(null, Typeface.BOLD);
            }
        });
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        int pixelsInfo = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 415, getResources().getDisplayMetrics());
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.height = pixelsInfo;
        alertDialog.getWindow().setAttributes(lp);
    }
}