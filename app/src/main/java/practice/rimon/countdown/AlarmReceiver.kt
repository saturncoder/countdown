package practice.rimon.countdown

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    var notif_id=1

    override fun onReceive(context: Context, intent: Intent) {
        val today=Calendar.getInstance()
        var dayOfWeek=today.get(Calendar.DAY_OF_WEEK)//Sunday(1) Monday(2)....7
        dayOfWeek -= 2 //Sunday(-1) Monday(0)....5
        if(dayOfWeek==-1){dayOfWeek=6} // Monday(0)....5 Sunday(6)
        val xxxDay=dayOfWeek.toString()
        Log.e("今天星期","${dayOfWeek+1}")
        // 讀取記事標題
        val title = intent.getStringExtra("title")
        //讀取itemid
        val itemid=intent.getLongExtra("item_id",1L)
         notif_id=itemid.toInt()

        //讀取事件時間點
        val item_eventDatetime=intent.getLongExtra("item_eventDatetime",0L)
        val daysbetween =timeToDays(item_eventDatetime)
        //讀取間隔類別
        val intervalType=intent.getLongExtra("item_alarmInterval",0L).toInt()
        //讀取提醒日字串
        val days=intent.getStringExtra("item_alarmdays")

        if(intervalType==0) {//每天
            notification(context, title, daysbetween)
        }
        else if (intervalType==1 || intervalType==2)//周一~五 or 自訂
        {
            if (days.contains(xxxDay)){ //包含今天的話就通知
                Log.e("Alarm","今天提醒")
                notification(context, title, daysbetween)
            }
            else{
                Log.e("Alarm","今天沒設提醒，不通知")
            }
        }

        Log.e("Alarm","重複提醒中,項目id:$notif_id 標題:$title ,剩下$daysbetween 天,於星期$days 提醒")
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

        val left=context.resources.getString(R.string.left)
        val left_en=context.resources.getString(R.string.left_en)
        val day_string=context.resources.getString(R.string.day_unit)

        val notification = NotificationCompat.Builder(context, "channelID")
                .setSmallIcon(R.drawable.app_icon_notif2)
                //.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.penguin))
                .setContentTitle(title)
                .setContentText("$left $daysbetween $day_string $left_en")
                //.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
        notificationmanager.notify(notif_id, notification)
    }

}
