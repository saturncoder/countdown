package practice.rimon.countdown

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    var notif_id=1
    override fun onReceive(context: Context, intent: Intent) {
        // 讀取記事標題
        val title = intent.getStringExtra("title")
        //讀取itemid
        val itemid=intent.getLongExtra("item_id",1L)
         notif_id=itemid.toInt()

        //讀取事件時間點
        val item_eventDatetime=intent.getLongExtra("item_eventDatetime",0L)
        val daysbetween =timeToDays(item_eventDatetime)


        notification(context,title,daysbetween)
        Log.e("Alarm","重複提醒中,項目id:$notif_id 標題:$title ,剩下$daysbetween 天")
    }
    fun notification(context: Context,title:String,daysbetween:Int){
        val notificationmanager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //>API26 Oreo 需要設定channel
            val mChannel = NotificationChannel("channelID", "reminder", NotificationManager.IMPORTANCE_HIGH)
            // mChannel.description = "description"

            notificationmanager.createNotificationChannel(mChannel)
        }
        val intent= Intent(context,MainActivity::class.java)
        val pendingIntent= PendingIntent.getActivity(context,2001,intent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, "channelID")
                .setSmallIcon(R.drawable.app_icon_notif2)
                //.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.penguin))
                .setContentTitle(title)
                .setContentText("剩下 $daysbetween 天")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
        notificationmanager.notify(notif_id, notification)
    }

}
