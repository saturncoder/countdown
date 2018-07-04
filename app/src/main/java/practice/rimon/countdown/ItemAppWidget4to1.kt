package practice.rimon.countdown

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [ItemAppWidgetConfigureActivity]
 */
class ItemAppWidget4to1 : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.e("小工具","onUpdate")
        Log.e("小工具","個數:${appWidgetIds.size}")
        //Toast.makeText(context, "Widget has been updated! ", Toast.LENGTH_SHORT).show()
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }

    }


    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {//被刪掉的，一次只刪一個，所以array size=1
            ItemAppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId)
            //取消註冊
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, WidgetAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            am.cancel(pendingIntent)
            Log.e("小工具","ondelete cancel:appWidgetId $appWidgetId")
        }


    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        Log.e("收到","onenabled")
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {
            Log.e("widget","update function called")
            val itemInfo = ItemAppWidgetConfigureActivity.loadTitlePref(context, appWidgetId)
            // Construct the RemoteViews object
            val title=itemInfo.title
            val days= timeToDays(itemInfo.eventDatetime)

            val views = RemoteViews(context.packageName, R.layout.item_app_widget41)
            views.setTextViewText(R.id.appwidget_title, title)
            views.setTextViewText(R.id.appwidget_days,days.toString())
            // Instruct the widget manager to update the widget

            // 點選小工具畫面的記事標題後，啟動記事應用程式
            val intent = Intent(context, MainActivity::class.java)
            val pending = PendingIntent.getActivity(
                    context, 0, intent, 0)
            views.setOnClickPendingIntent(R.id.widgetLL, pending)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

