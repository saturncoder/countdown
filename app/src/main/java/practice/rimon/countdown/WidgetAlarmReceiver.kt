package practice.rimon.countdown

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class WidgetAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val mAppWidgetId=intent.getIntExtra("WG_id",0)
        val label=intent.getStringExtra("WG_label")
        val appWidgetManager = AppWidgetManager.getInstance(context)
        if (label==context.resources.getString(R.string.Widget_4to1)) {
            ItemAppWidget4to1.updateAppWidget(context, appWidgetManager, mAppWidgetId)
        }
        else if(label==context.resources.getString(R.string.Widget_1to1)){
            ItemAppWidget1to1.updateAppWidget(context, appWidgetManager, mAppWidgetId)
        }

        Log.e("WG_alarm","onReceive id:$mAppWidgetId")
    }
}
