package practice.rimon.countdown

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_item.*
import net.danlew.android.joda.JodaTimeAndroid
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ItemActivity : AppCompatActivity() {

    //取得一個實例，時間為現在時間 
    val calendar= Calendar.getInstance()//現在時間
    val eventDate=Calendar.getInstance()//事件時間
    val reminder=Calendar.getInstance()//提醒時間
    //要用來回傳的空item
    var item=Item()
    //要用來讀出已儲存分類的陣列
    var stored_category=ArrayList<String>()


    var eventDate_mills:Long=0
    //顯示的日期格式 (若換地區有問題可能是這裡出問題)
    val timeFormat = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())
    val remindertimeFormat = SimpleDateFormat("HH:mm",Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val themeID = prefs.getInt("theme", 0)
        when(themeID){
            0->setTheme(R.style.AppTheme)
            1->setTheme(R.style.brownTheme)
            2->setTheme(R.style.greenTheme)
            3->setTheme(R.style.redTheme)
            4->setTheme(R.style.yellowTheme)
            5->setTheme(R.style.orgTheme)
            6->setTheme(R.style.blueTheme)
            7->setTheme(R.style.greyTheme)
            8->setTheme(R.style.purpleTheme)
            9->setTheme(R.style.pinkTheme)
            10->setTheme(R.style.indigoTheme)
            11->setTheme(R.style.blackTheme)
            12->setTheme(R.style.silverTheme)
            13->setTheme(R.style.skinTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        JodaTimeAndroid.init(this)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //讀出已儲存 分類陣列
        stored_category=getArrayList("category",this)
        println(stored_category)


        registerListener()//注意順序 clickable etc
        //清空category
        //PreferenceManager.getDefaultSharedPreferences(this).edit().remove("category").apply()
        //預設第一次提醒時間 隔天00:00
        reminder.roll(Calendar.DAY_OF_MONTH,1)
        reminder.set(Calendar.HOUR_OF_DAY,0)
        reminder.set(Calendar.MINUTE,0)
        reminder.set(Calendar.SECOND,0)
        item.alarmAt=reminder.timeInMillis//存入預設值
        Log.e("預設提醒時間(隔天凌晨) alarmAt","${reminder.time}")

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
            textView_item_daysbetween.text="$daysbetween 天"
            //讀出已儲存分類
            textView_item_category.text=stored_category[itemselected.category]
            //讀出已儲存備忘錄
            editText_memo.setText(itemselected.memo)

            //若未更改時間，儲存原本時間
            item.eventDatetime=itemselected.eventDatetime
            //若未更改時間，事件時間就是reminder到期時間
            eventDate.timeInMillis=itemselected.eventDatetime
            Log.e("事件日期","${eventDate.time}")
            //若未更改分類，儲存原來分類
            item.category=itemselected.category

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
        textView_item_category.setOnClickListener(categoryOnClickListener)
        reminder_interval.setOnClickListener(reminderIntervalOnClickListener)
        reminder_time.setOnClickListener(reminderTimeOnClickListener)
        switch_reminder.setOnCheckedChangeListener(reminderSwitchListener)
        reminder_interval.isClickable=false //xml設無效 還是能點
        reminder_time.isClickable=false
        editText_memo.imeOptions=EditorInfo.IME_ACTION_DONE
        editText_memo.setRawInputType(InputType.TYPE_CLASS_TEXT)
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
            setResult(Activity.RESULT_CANCELED, intent)
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
        //若提醒已開且日期未過期，要存提醒時間
        if(switch_reminder.isChecked) {
            item.alarmDatetime = item.eventDatetime
            //若選擇的日期是過去的時間，則不能設提醒
            if(item.alarmDatetime-23L*60L*60L*1000L<=calendar.timeInMillis){
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
        //更新剩餘天數
        val daysbetween= timeToDays(eventDate_mills)
        textView_item_daysbetween.text="$daysbetween 天"

        if(switch_reminder.isChecked) {
            switch_reminder.isChecked = false
        }
    }

    private fun comfirmItem(){
        //檢查事件標題是否為空
        if(TextUtils.isEmpty(editText_item_title.text.toString().trim())){
            Toast.makeText(this, "請輸入事件名稱", Toast.LENGTH_LONG).show()
        }
        //檢查是否選擇事件日期  (用預設值是0L來檢查)
        else if (item.eventDatetime==0L){
            Toast.makeText(this, "請選擇事件日期", Toast.LENGTH_LONG).show()
        }
        else {
            item.item_icon = R.drawable.test
            item.item_title = editText_item_title.text.toString()
            item.memo=editText_memo.text.toString()

            println("press OK button")
            intent.putExtra("countdown.Item", item)
            intent.putExtra("category_num",stored_category.size)
            setResult(Activity.RESULT_OK, intent)
            //關掉頁面

            finish()
        }
        println(item.alarmDatetime)
    }
    //下面實體鍵返回
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
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
    //按下選擇分類鍵
    private val categoryOnClickListener=View.OnClickListener{

        val builder = AlertDialog.Builder(this)
        builder.setTitle("選擇分類")
        builder.setIcon(R.drawable.baseline_local_offer_black_24dp)
        //用來給選單顯示的adapter
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice)
        //加入已儲存分類
        for(i in stored_category){
            arrayAdapter.add(i)
        }
        arrayAdapter.add("新增分類...")

        builder.setSingleChoiceItems(arrayAdapter,item.category,{dialogInterface, i ->
            //如果選擇最後一個(新增分類...)
            if(i==arrayAdapter.count-1){
                if(stored_category.size>=8){
                    val builderalert = AlertDialog.Builder(this)
                    builderalert.setTitle("提醒")
                    builderalert.setMessage("您只能新增3個自訂分類")
                    builderalert.setPositiveButton("確定",{_ ,_->})
                    builderalert.create().show()

                }
                else {
                    //未滿3個跳出新增視窗
                    val builderinner = AlertDialog.Builder(this)
                    val edittext = EditText(this)
                    edittext.setSingleLine(true)
                    builderinner.setTitle("新增分類名稱")
                    builderinner.setView(edittext)

                    builderinner.setPositiveButton("確定",null)
                    builderinner.setNegativeButton("取消",null)

                    val alertdialog=builderinner.show()
                    val confirm=alertdialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    confirm.setOnClickListener {
                        if(TextUtils.isEmpty(edittext.text.toString().trim())) {
                            //edittext.error="請輸入分類名稱"
                            Toast.makeText(this, "請輸入分類名稱", Toast.LENGTH_LONG).show()
                        }
                        else {
                            val newcategory = edittext.text.toString()
                            //插到倒數第二個
                            arrayAdapter.insert(newcategory, arrayAdapter.count - 1)
                            arrayAdapter.notifyDataSetChanged()
                            //存入自訂分類陣列
                            stored_category.add(newcategory)
                            //存入sharedpref
                            saveArrayList(stored_category, "category", this)
                            alertdialog.dismiss()
                        }
                    }
                }
            }
            else{
                textView_item_category.text=arrayAdapter.getItem(i).toString()
                //存入item
                item.category=i
                dialogInterface.dismiss()
            }

        })

        builder.show()


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





}
