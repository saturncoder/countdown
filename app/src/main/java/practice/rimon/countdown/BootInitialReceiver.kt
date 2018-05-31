package practice.rimon.countdown

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

class BootInitialReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // 建立資料庫物件
        val itemDAO = ItemDAO(context.applicationContext)
        // 讀取資料庫所有記事資料
        val items = itemDAO.all
        // 讀取目前時間
        val current = Calendar.getInstance().timeInMillis
        val eventDate=Calendar.getInstance()
        val firstnotify=Calendar.getInstance()//隔天的xx:xx
        firstnotify.roll(Calendar.DAY_OF_MONTH,1)
        firstnotify.set(Calendar.SECOND,0)

        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var i=0
        for (item in items) {
            // 如果有設定提醒而且提醒還沒有過期
            if (item.alarmDatetime != 0L && item.alarmDatetime-23L*60L*60L*1000 > current) {

                // 設定提醒
                val alarmIntent = Intent(context, AlarmReceiver::class.java)
                    alarmIntent.putExtra("title", item.item_title)
                    alarmIntent.putExtra("item_id", item.id)
                    alarmIntent.putExtra("item_eventDatetime",item.eventDatetime)
                val broadcastCODE = item.id.toInt()

                val pendingIntent = PendingIntent.getBroadcast(context, broadcastCODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                //註冊第一次提醒時間
                val read=Calendar.getInstance()
                read.timeInMillis=item.alarmAt//只有小時分鐘對
                firstnotify.set(Calendar.HOUR_OF_DAY,read.get(Calendar.HOUR_OF_DAY))
                firstnotify.set(Calendar.MINUTE,read.get(Calendar.MINUTE))
                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstnotify.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
                Log.e("第一次提醒時間", "${item.item_title}:${firstnotify.time}")

                //註冊提醒到期時間
                val cancellationIntent = Intent(context, CancelAlarmReceiver::class.java)
                cancellationIntent.putExtra("item_id", item.id)
                val cancellationPendingIntent = PendingIntent.getBroadcast(context, broadcastCODE, cancellationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                am.set(AlarmManager.RTC_WAKEUP, item.alarmDatetime, cancellationPendingIntent)

                eventDate.timeInMillis=item.alarmDatetime
                Log.e("提醒到期時間", "${item.item_title}:${eventDate.time}")
                i += 1
            }
        }
        Log.e("開機註冊提醒數量", "$i")

    }
}
