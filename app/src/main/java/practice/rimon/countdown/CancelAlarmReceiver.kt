package practice.rimon.countdown

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class CancelAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val intent_alarm = Intent(context, AlarmReceiver::class.java)
        val REQUESTCODE=intent.getLongExtra("item_id",1L).toInt()
        Log.i("itemID","$REQUESTCODE")
        val pendingIntent =PendingIntent.getBroadcast(context,REQUESTCODE, intent_alarm, PendingIntent.FLAG_UPDATE_CURRENT)
        pendingIntent.cancel()
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pendingIntent)
        Log.i("Alarm","提醒到期 關掉了")
    }
}
