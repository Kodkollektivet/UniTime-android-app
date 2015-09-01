
package com.jotto.unitime.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.jotto.unitime.R;
import com.jotto.unitime.models.Event;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListProvider implements RemoteViewsService.RemoteViewsFactory
{
    private Context context = null;
    private int appWidgetId;
    private List<Event> widgetList = new ArrayList<>();
    private String importantEvents = "redovisning|tentamen|omtentamen|exam|examination|tenta|deadline|reexamination|reexam";

    public ListProvider(Context context, Intent intent)
    {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        //Log.d("AppWidgetId", String.valueOf(appWidgetId));
        //dbhelper = new DBHelper(this.context);
    }

    // Gets the events from the database that is for today, sorts them and then add them to the list
    private void updateWidgetListView()
    {
        this.widgetList = Event.find(Event.class, "STARTDATE = ?", LocalDate.now().toString());
        Collections.sort(this.widgetList);
    }

    //Returns the size of the list of events
    @Override
    public int getCount()
    {
        return widgetList.size();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public RemoteViews getLoadingView()
    {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position)
    {
        //Gets the view
        RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                R.layout.event_view_widget);

        /*
        Gets the event from the list and sets the appropriate texts
         */
        Event event = widgetList.get(position);
        remoteView.setTextViewText(R.id.event_course_widget, event.getName_en());
        remoteView.setTextViewText(R.id.event_room_widget, event.getRoom());
        remoteView.setTextViewText(R.id.event_info_widget, event.getInfo());
        remoteView.setTextViewText(R.id.event_time_widget, event.getStarttime() + "-" + event.getEndtime());

        /*
        If the event is important change the icon to the important icon
         */
        if (importantEvents.contains(event.getInfo().toLowerCase())) {
            remoteView.setImageViewResource(R.id.image_icon_widget, R.drawable.event_icon_important);
        }
        else {
            remoteView.setImageViewResource(R.id.image_icon_widget, R.drawable.event_icon);
        }

        return remoteView;
    }

    @Override
    public int getViewTypeCount()
    {
        return 1;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public void onCreate()
    {
        updateWidgetListView();
    }

    @Override
    public void onDataSetChanged()
    {
        updateWidgetListView();
    }

    @Override
    public void onDestroy()
    {
        widgetList.clear();
    }
}