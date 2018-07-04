package practice.rimon.countdown

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.widget_configure.*
import java.util.*

/**
 * The configuration screen for the [ItemAppWidget] AppWidget.
 */
class ItemAppWidgetConfigureActivity : Activity() {
    internal var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID


    val mydata=ArrayList<Item>()
    val gridlayoutmanager= GridLayoutManager(this,1)
    lateinit var  myadapter:myAdapterWG
    private val itemDAO : ItemDAO by lazy { ItemDAO(applicationContext) }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(Activity.RESULT_CANCELED)
        setContentView(R.layout.widget_configure)
        //顯示item列表給使用者選擇
        widget_recyclerview.layoutManager=gridlayoutmanager
        mydata.addAll(itemDAO.all)
        myadapter=myAdapterWG(mydata) { position:Int->
            Log.e("widget", "選中項目 $position")
            itemClickedHandler(position)
        }
        widget_recyclerview.adapter=myadapter

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }
        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

    }

    companion object {

        private val PREFS_NAME = "practice.rimon.countdown.ItemAppWidget"
        private val PREFIX_KEY_TITLE = "appwidget_title_"
        private val PREFIX_KEY_TIME= "appwidget_time_"

        internal fun saveTitlePref(context: Context, appWidgetId: Int, title: String, eventDatetime: Long) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.putString(PREFIX_KEY_TITLE + appWidgetId, title)
            prefs.putLong(PREFIX_KEY_TIME + appWidgetId,eventDatetime)
            prefs.apply()
        }


        internal fun loadTitlePref(context: Context, appWidgetId: Int): itemInfo {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val title = prefs.getString(PREFIX_KEY_TITLE + appWidgetId, "default")
            val time=prefs.getLong(PREFIX_KEY_TIME + appWidgetId,0L)
            return itemInfo(title,time)
        }

        internal fun deleteTitlePref(context: Context, appWidgetId: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.remove(PREFIX_KEY_TITLE + appWidgetId)
            prefs.remove(PREFIX_KEY_TIME + appWidgetId)
            prefs.apply()
        }
    }

    private fun itemClickedHandler(position:Int){
        val title=mydata[position].item_title
        val eventDatetime= mydata[position].eventDatetime
        saveTitlePref(this,mAppWidgetId,title,eventDatetime)
        // It is the responsibility of the configuration activity to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(this)

        //判斷是新增了哪種大小的widget
        val label=AppWidgetManager.getInstance(this).getAppWidgetInfo(mAppWidgetId).loadLabel(this.packageManager)
        Log.e("label",label)
        if (label==resources.getString(R.string.Widget_4to1)) {
            ItemAppWidget4to1.updateAppWidget(this, appWidgetManager, mAppWidgetId)
        }
        else if(label==resources.getString(R.string.Widget_1to1)){
            ItemAppWidget1to1.updateAppWidget(this, appWidgetManager, mAppWidgetId)
        }
        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        //註冊更新鬧鐘
        val intent = Intent(this, WidgetAlarmReceiver::class.java)
        intent.putExtra("WG_id",mAppWidgetId )
        intent.putExtra("WG_label",label)
        val pendingIntent = PendingIntent.getBroadcast(this, mAppWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        //註冊第一次提醒時間 午夜
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val now=Calendar.getInstance()
        now.roll(Calendar.DAY_OF_MONTH,1)
        now.set(Calendar.HOUR_OF_DAY,0)
        now.set(Calendar.MINUTE,0)
        now.set(Calendar.SECOND,0)

        Log.e("註冊小工具更新時間","${now.time}")
        am.setRepeating(AlarmManager.RTC_WAKEUP, now.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
        finish()
    }

}

