package practice.rimon.countdown

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_item.*
import net.danlew.android.joda.JodaTimeAndroid
import java.text.SimpleDateFormat
import java.util.*



class ItemActivity : AppCompatActivity() {

    //取得一個實例，時間為現在時間 
    val calendar= Calendar.getInstance()//現在時間
    val eventDate=Calendar.getInstance()//事件時間
    val reminder=Calendar.getInstance()//提醒時間
    //要用來回傳的空item
    var item=Item()

    var eventDate_mills:Long=0
    //顯示的日期格式 (若換地區有問題可能是這裡出問題)
    val timeFormat = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())
    val remindertimeFormat = SimpleDateFormat("HH:mm",Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        JodaTimeAndroid.init(this)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        registerListener()//注意順序 clickable etc

        //預設第一次提醒時間 隔天00:00
        reminder.roll(Calendar.DAY_OF_MONTH,1)
        reminder.set(Calendar.HOUR_OF_DAY,0)
        reminder.set(Calendar.MINUTE,0)
        reminder.set(Calendar.SECOND,0)
        item.alarmAt=reminder.timeInMillis//存入預設值
        Log.e("預設提醒時間(隔天凌晨)","${reminder.time}")

        //看是編輯還是新增
        val action=intent.action //oncreate完才抓的到
        if(action=="practice.rimon.countdown.EDIT_ITEM"){
            //如果是編輯，讀出該項目
            val itemselected=intent.getSerializableExtra("itemclicked") as Item
            Log.i("selected item","$itemselected")
            item.id=itemselected.id
            //預填上已儲存標題,時間
            editText_item_title.setText(itemselected.item_title)
            //讀出事件時間並以特定格式顯示
            val cal_eventTime=Calendar.getInstance()
            cal_eventTime.timeInMillis=itemselected.eventDatetime
            textView_item_eventTime.text=timeFormat.format(cal_eventTime.time)
            //以儲存時間推算事件剩餘天數
            val daysbetween =timeToDays(itemselected.eventDatetime)
            textView_item_daysbetween.text=daysbetween.toString()

            //若未更改時間，儲存原本時間
            item.eventDatetime=itemselected.eventDatetime
            //若未更改時間，事件時間就是reminder到期時間
            eventDate.timeInMillis=itemselected.eventDatetime
            Log.e("事件日期","${eventDate.time}")

            //判斷若已有設提醒
            //檢查提醒若已過期，則不須設置提醒
            Log.e("事件已儲存提醒日期","${itemselected.alarmDatetime}")
            if (itemselected.alarmDatetime!=0L && eventDate.timeInMillis-23L*60L*60L*1000L>calendar.timeInMillis){
                switch_reminder.isChecked=true
                item.alarmDatetime=itemselected.alarmDatetime
                reminder_interval.isClickable=true
                reminder_time.isClickable=true
                reminder_interval.setTextColor(Color.BLACK)
                reminder_time.setTextColor(Color.BLACK)
                //讀出已設定的提醒時間(日期可能是錯的)
                val selectedAlarmat=Calendar.getInstance()
                selectedAlarmat.timeInMillis=itemselected.alarmAt
                reminder_time.text=remindertimeFormat.format(selectedAlarmat.time)
                //若未更改就直接儲存
                item.alarmAt=itemselected.alarmAt
                //提醒預設值改為已儲存提醒時間
                reminder.set(Calendar.HOUR_OF_DAY,selectedAlarmat.get(Calendar.HOUR_OF_DAY))
                reminder.set(Calendar.MINUTE,selectedAlarmat.get(Calendar.MINUTE))

            }
        }


    }
    private fun registerListener(){
        textView_item_eventTime.setOnClickListener(eventTimeOnClickListener)
        reminder_interval.setOnClickListener(reminderIntervalOnClickListener)
        reminder_time.setOnClickListener(reminderTimeOnClickListener)
        switch_reminder.setOnCheckedChangeListener(reminderSwitchListener)
        reminder_interval.isClickable=false //xml設無效 還是能點
        reminder_time.isClickable=false
    }
    //右上menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.confirm) {
           comfirmItem()
            return true
        }
        else if(item.itemId==android.R.id.home){
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    //事件時間
    private val eventTimeOnClickListener= View.OnClickListener { view ->
        DatePickerDialog(this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show()
    }
    private val dateSetListener= DatePickerDialog.OnDateSetListener{ view, year, month, day->
        // Int,Int,Int
        //month:0=一月
        //事件時間
        eventDate.set(year,month,day,23,0,0)
        Log.e("事件日期改變eventDate","${eventDate.time}")//.time方法以預設格式顯示
        textView_item_eventTime.text=timeFormat.format(eventDate.time)
        //儲存事件時間(millis)
        eventDate_mills=eventDate.timeInMillis
        item.eventDatetime=eventDate_mills
        //更新剩餘天數
        val daysbetween= timeToDays(eventDate_mills)
        textView_item_daysbetween.text=daysbetween.toString()

    }

    private fun comfirmItem(){
        //檢查事件標題是否為空
        if(TextUtils.isEmpty(editText_item_title.text.toString())){
            Toast.makeText(this, "請輸入事件名稱", Toast.LENGTH_LONG).show()
        }
        //檢查是否選擇事件日期  (用預設值是0L來檢查)
        else if (item.eventDatetime==0L){
            Toast.makeText(this, "請選擇事件日期", Toast.LENGTH_LONG).show()
        }
        else {
            item.item_icon = R.drawable.test
            item.item_title = editText_item_title.text.toString()

            println("press OK button")
            intent.putExtra("countdown.Item", item)
            setResult(Activity.RESULT_OK, intent)
            //關掉頁面
            setNotification()
            finish()
        }
        println(item.alarmDatetime)
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //crash
            // 設定回應結果為取消
            setResult(Activity.RESULT_CANCELED, intent)
        }

        return super.onKeyDown(keyCode, event)
    }

    //提醒時間
    private val reminderTimeOnClickListener=View.OnClickListener {view->
        val timePickerDialog=TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog,timeSetListener,0,0,true)
        timePickerDialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        timePickerDialog.setTitle("選擇提醒時間:")
        timePickerDialog.show()
    }
    private val timeSetListener=TimePickerDialog.OnTimeSetListener{view,hourofday,minute->
        //第一次提醒時間 今天的XX:XX  預設是隔天00:00
        reminder.set(Calendar.HOUR_OF_DAY,hourofday)
        reminder.set(Calendar.MINUTE,minute)
        reminder.set(Calendar.SECOND,0)
        Log.e("使用者選擇的提醒時間(第一次)","${reminder.time}")
        reminder_time.text=remindertimeFormat.format(reminder.time)
        //存入
        item.alarmAt=reminder.timeInMillis
    }
    //提醒間距 還沒做
    private val reminderIntervalOnClickListener=View.OnClickListener {
        val reminderIntervalArray= resources.getStringArray(R.array.reminder_interval)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("選擇提醒頻率")
        builder.setSingleChoiceItems(R.array.reminder_interval,getSelectedItem(), { dialogInterface, i ->
            reminder_interval.text=reminderIntervalArray[i]
            saveSelectedItem(i)
            dialogInterface.dismiss()
        })

        val alertDialog=builder.create()
        alertDialog.show()
    }
    //保留使用者選擇
    private fun getSelectedItem(): Int {
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPreference.getInt("selectedInterval", 0) //第二個參數:default
    }
    private fun saveSelectedItem(i: Int) {
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
        val sharedPrefEditor=sharedPreference.edit()

        sharedPrefEditor.putInt("selectedInterval", i)
        sharedPrefEditor.apply()
    }
    //提醒開關，決定alarmDatetime是否為0(是否關閉)
    private val reminderSwitchListener= CompoundButton.OnCheckedChangeListener{buttonView,isChecked->
        if(isChecked){
            reminder_interval.isClickable=true
            reminder_time.isClickable=true
            reminder_interval.setTextColor(Color.BLACK)
            reminder_time.setTextColor(Color.BLACK)
            //設置提醒到期時間
            item.alarmDatetime=item.eventDatetime

            //檢查事件過期與否，過期向使用者說明並不會發出提醒(註銷時間比註冊時間早)
            if(eventDate.timeInMillis-23L*60L*60L*1000L<=calendar.timeInMillis){
                Toast.makeText(this,"過期事件無法設置提醒",Toast.LENGTH_LONG).show()
                switch_reminder.isChecked=false
                reminder_interval.isClickable=false
                reminder_time.isClickable=false
                reminder_interval.setTextColor(Color.GRAY)
                reminder_time.setTextColor(Color.GRAY)
                //關閉提醒
                item.alarmDatetime=0L
            }
        }
        else if(!isChecked){
            reminder_interval.isClickable=false
            reminder_time.isClickable=false
            reminder_interval.setTextColor(Color.GRAY)
            reminder_time.setTextColor(Color.GRAY)
            //關閉提醒
            item.alarmDatetime=0L
        }
    }
    private fun setNotification(){
        //若有開啟提醒則設置提醒
        if(item.alarmDatetime!=0L){
            //檢查事件是否過期

                val intent = Intent(this, AlarmReceiver::class.java)
                intent.putExtra("title", item.item_title)
                intent.putExtra("item_id", item.id)
                val broadcastCODE = item.id.toInt()
                Log.e("提醒的requestcode", "$broadcastCODE")
                val pendingIntent = PendingIntent.getBroadcast(this, broadcastCODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                //註冊第一次提醒時間
                val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, reminder.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
                Log.e("第一次提醒時間", "${reminder.time}")
                //註冊提醒到期時間
                val cancellationIntent = Intent(this, CancelAlarmReceiver::class.java)
                cancellationIntent.putExtra("item_id", item.id)
                val cancellationPendingIntent = PendingIntent.getBroadcast(this, broadcastCODE, cancellationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                am.set(AlarmManager.RTC_WAKEUP, item.alarmDatetime, cancellationPendingIntent)
                Log.e("提醒到期時間", "${eventDate.time}")

        }
        //沒提醒要把原本註冊的提醒關掉
        else{
            val cancellationIntent = Intent(this, CancelAlarmReceiver::class.java)
            cancellationIntent.putExtra("item_id", item.id)
            val broadcastCODE = item.id.toInt()
            val cancellationPendingIntent = PendingIntent.getBroadcast(this, broadcastCODE, cancellationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, cancellationPendingIntent)
            Log.e("註銷已註冊提醒", "${calendar.time}")
        }

    }
}
