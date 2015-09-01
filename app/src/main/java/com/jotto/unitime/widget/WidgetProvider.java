package com.jotto.unitime.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.RemoteViews;
import com.jotto.unitime.R;
import com.jotto.unitime.models.Course;
import com.jotto.unitime.models.Event;
import com.jotto.unitime.sessionhandler.SessionHandler;
import com.jotto.unitime.util.Network;

import java.util.List;

public class WidgetProvider extends AppWidgetProvider {

    /*
    Strings for separating the different update methods
     */
    public static String UPDATE_ACTION = "UPDATE_ACTION";
    public static String UPDATE_ACTION_ALARM = "unitime.widget.UPDATE_ALARM";
    Network network = new Network();
    Context context;

    @Override
    public void onDeleted(Context context, int[] appWidgetIds)
    {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context)
    {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context)
    {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        String action = intent.getAction();

        /*
        Init the widget, create update via alarmmanager every 3 hours and respond to the appropriate
        action string.
         */
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {

            final AppWidgetManager manager = AppWidgetManager.getInstance
                    (context);
            onUpdate(context, manager, manager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class)));

            Intent updateIntent = new Intent(UPDATE_ACTION_ALARM);
            PendingIntent pendingUpdateIntent = PendingIntent.getBroadcast(context, 0, updateIntent, 0);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 10800000, pendingUpdateIntent);
        }
        else if (action.equals(UPDATE_ACTION)) {
            final AppWidgetManager manager = AppWidgetManager.getInstance
                    (context);
            onUpdate(context, manager, manager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class)));
        }
        else if (action.equals(UPDATE_ACTION_ALARM)) {
            if (network.isOnline(context)) {
                new RefreshEvents().execute();
            }
        }
        /*
        Cancel the alarmmanager
         */
        else if (action.equals(AppWidgetManager.ACTION_APPWIDGET_DISABLED)) {
            Intent updateIntent = new Intent(UPDATE_ACTION_ALARM);
            PendingIntent pendingUpdateIntent = PendingIntent.getBroadcast(context, 0, updateIntent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingUpdateIntent);
        }
        else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds)
    {
        /*
        Update, set up the adapter for the widget listview, set the empty view and set open app
        on click.
         */
        for (int appWidgetId : appWidgetIds) {
            RemoteViews rv = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);

            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            rv.setRemoteAdapter(R.id.widget_listview, intent);
            rv.setEmptyView(R.id.widget_listview, R.id.empty_view_widget);
            //set on click on view open app activity
            Intent configIntent = new Intent(context, com.jotto.unitime.MainActivity.class);

            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
            rv.setOnClickPendingIntent(R.id.widget_layout, configPendingIntent);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview);
            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    /*
    Removes all events and event ids from the database, gets new ones from sessionhandler and
    send update action to widget
     */
    private class RefreshEvents extends AsyncTask {
        SessionHandler sessionHandler = new SessionHandler();

        @Override
        protected void onPostExecute(Object o) {
            final AppWidgetManager manager = AppWidgetManager.getInstance
                    (context);
            onUpdate(context, manager, manager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class)));
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Event.deleteAll(Event.class);
            Event.executeQuery("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'EVENT'");
            List<Course> addedCourses = Course.listAll(Course.class);
            for (Course course : addedCourses) {
                sessionHandler.getEventsFromCourse(course.getCourse_code().toUpperCase(), course.getCourse_location());
            }
            return true;
        }
    }
}