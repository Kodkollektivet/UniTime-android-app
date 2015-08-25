
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
    private ArrayList<String> importantEvents = new ArrayList<String>(Arrays.asList("Redovisning", "Tentamen", "Omtentamen",
            "Examen", "Examination", "Tenta"));

    public ListProvider(Context context, Intent intent)
    {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        //Log.d("AppWidgetId", String.valueOf(appWidgetId));
        //dbhelper = new DBHelper(this.context);
    }

    private void updateWidgetListView()
    {
        this.widgetList = Event.find(Event.class, "STARTDATE = ?", LocalDate.now().toString());
        Collections.sort(this.widgetList);
    }

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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position)
    {
        //Log.d("WidgetCreatingView", "WidgetCreatingView");
        RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                R.layout.event_view_widget);

        //Log.d("Loading", widgetList.get(position));
        Event event = widgetList.get(position);
        remoteView.setTextViewText(R.id.event_course_widget, event.getName_sv());
        remoteView.setTextViewText(R.id.event_room_widget, event.getRoom());
        remoteView.setTextViewText(R.id.event_info_widget, event.getInfo());
        remoteView.setTextViewText(R.id.event_time_widget, event.getStarttime() + "-" + event.getEndtime());

        if (importantEvents.contains(event.getInfo())) {
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
        // TODO Auto-generated method stub
        return 1;
    }

    @Override
    public boolean hasStableIds()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        updateWidgetListView();
    }

    @Override
    public void onDataSetChanged()
    {
        // TODO Auto-generated method stub
        updateWidgetListView();
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        widgetList.clear();
        //dbhelper.close();
    }
}