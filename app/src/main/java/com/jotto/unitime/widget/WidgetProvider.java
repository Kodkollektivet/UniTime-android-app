package com.jotto.unitime.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.jotto.unitime.MainActivity;
import com.jotto.unitime.R;
import com.jotto.unitime.widget.WidgetService;

public class WidgetProvider extends AppWidgetProvider {

    public static String UPDATE_ACTION = "UPDATE_ACTION";

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
        String action = intent.getAction();

        if (action != null && action.equals(UPDATE_ACTION)) {
            final AppWidgetManager manager = AppWidgetManager.getInstance
                    (context);
            onUpdate(context, manager, manager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class)));
        }
        else {
            super.onReceive(context, intent);
        }
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds)
    {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews rv = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);

            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            rv.setRemoteAdapter(R.id.widget_listview, intent);
            rv.setEmptyView(R.layout.event_view_widget, R.layout.empty_event_view_widget);

            //set on click on view open app activity
            Intent configIntent = new Intent(context, com.jotto.unitime.MainActivity.class);

            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
            rv.setOnClickPendingIntent(R.id.widget_layout, configPendingIntent);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview);
            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}