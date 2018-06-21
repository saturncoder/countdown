package practice.rimon.countdown

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import kotlinx.android.synthetic.main.activity_item.*
import net.danlew.android.joda.JodaTimeAndroid
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ItemActivity : AppCompatActivity(){



    //取得一個實例，時間為現在時間 
    val calendar= Calendar.getInstance()//現在時間
    val eventDate=Calendar.getInstance()//事件時間
    val reminder=Calendar.getInstance()//提醒時間
    //要用來回傳的空item
    var item=Item()
    //要用來讀出已儲存分類的陣列
    var stored_category=ArrayList<String>()
    val CAMERA_RCODE=33
    val GALLERY_RCODE=44
    val PERMISSION_RCODE=55
    var eventDate_mills:Long=0
    //顯示的日期格式 (若換地區有問題可能是這裡出問題)
    val timeFormat = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())
    val remindertimeFormat = SimpleDateFormat("HH:mm",Locale.getDefault())
    //是否用預設圖示
    var changeIcon=false
    //照相的話原檔的儲存路徑
    var myIconPath : File? =null
    var selectedDays= arrayListOf(0,1,2,3,4,5,6)
    lateinit var reminderIntervalArray:Array<String>
    lateinit var alarmDaysBooleanArray:BooleanArray
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
        alarmDaysBooleanArray= booleanArrayOf(true,true,true,true,true,true,true)
        reminderIntervalArray= resources.getStringArray(R.array.reminder_interval)
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

            //載入已儲存icon
            if(itemselected.item_icon.isNotEmpty()){
                val stored_icon= BitmapFactory.decodeByteArray(itemselected.item_icon,0,itemselected.item_icon.size)
                imageView_item_icon.setImageBitmap(stored_icon)
                //沒改的話直接存
                item.item_icon=itemselected.item_icon
            }
            //自訂提醒日
            val alarmDays=itemselected.alarmDays

            alarmDaysBooleanArray= booleanArrayOf(
                    alarmDays.contains("0"),alarmDays.contains("1"),alarmDays.contains("2"),alarmDays.contains("3"),
                    alarmDays.contains("4"),alarmDays.contains("5"),alarmDays.contains("6"))
            item.alarmDays=itemselected.alarmDays

            //若未更改提醒間隔，儲存原來提醒間隔
            item.alarmInterval=itemselected.alarmInterval
            if(itemselected.alarmInterval.toInt()==2){
                var days=""
                val daysStringArray=resources.getStringArray(R.array.reminder_days)
                selectedDays.clear()
                for(i in 0 until alarmDaysBooleanArray.size){
                    if(alarmDaysBooleanArray[i]){
                        days=days+" "+daysStringArray[i]
                        selectedDays.add(i)
                    }
                }
                reminder_interval.text =days

            }
            else {
                reminder_interval.text = reminderIntervalArray[itemselected.alarmInterval.toInt()]
            }

            if(itemselected.alarmInterval.toInt()==1 ){
                selectedDays= arrayListOf(0,1,2,3,4)
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
        imageView_item_icon.setOnClickListener(itemIconOnClickListener)
        imageView_item_icon.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    //overlay is black with transparency of 0x77 (119)
                    imageView_item_icon.drawable.setColorFilter(0x33000000, PorterDuff.Mode.SRC_ATOP)
                    view.invalidate()
                    return@setOnTouchListener false
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    //clear the overlay
                    imageView_item_icon.drawable.clearColorFilter()
                    view.invalidate()
                    return@setOnTouchListener false
                }
                else->return@setOnTouchListener false

            }


        }
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
            if(changeIcon) {//跟當次比，編輯項目若沒改已直接存，不經過這裡
                //val bitmap= BitmapFactory.decodeResource(resources,R.drawable.grid_bg)
                val bitmapDrawable = imageView_item_icon.drawable as BitmapDrawable
                val bitmap = bitmapDrawable.bitmap
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)//100表沒壓縮，png本身就是lostless
                val img = stream.toByteArray()
                stream.close()
                item.item_icon = img
            }
            //沒換icon的話就是預設 空值: bytearrayof() ,在recyclerview 載入預設圖示
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

        val builder = AlertDialog.Builder(this)
        builder.setTitle("選擇提醒頻率")
        builder.setSingleChoiceItems(R.array.reminder_interval,item.alarmInterval.toInt()) { dialogInterface, i ->
            var intervalDays=""
            reminder_interval.text=reminderIntervalArray[i]
            when(i){
                0->{item.alarmInterval=0
                    intervalDays="0123456"
                    item.alarmDays=intervalDays
                }
                1->{item.alarmInterval=1
                    intervalDays="01234"
                    item.alarmDays=intervalDays
                }
                2->{item.alarmInterval=2
                    val innerbuilder = AlertDialog.Builder(this)

                        innerbuilder.setMultiChoiceItems(R.array.reminder_days,
                            alarmDaysBooleanArray){ dialog, which, isChecked ->

                            if(isChecked){selectedDays.add(which)}
                            else if(selectedDays.contains(which)){
                                selectedDays.remove(which)
                            }
                        println(selectedDays)
                        }

                    innerbuilder.setPositiveButton("確定"){dialog, which ->
                        selectedDays.forEach { i->
                            i.toString()
                            intervalDays += i

                        }
                        println(intervalDays)
                        item.alarmDays=intervalDays
                    //轉成文字顯示
                        alarmDaysBooleanArray= booleanArrayOf(intervalDays.contains("0"),intervalDays.contains("1"),intervalDays.contains("2"),intervalDays.contains("3"),
                                intervalDays.contains("4"),intervalDays.contains("5"),intervalDays.contains("6"))
                        var days=""
                        val daysStringArray=resources.getStringArray(R.array.reminder_days)
                        for( _i in 0 until alarmDaysBooleanArray.size){
                            if(alarmDaysBooleanArray[_i]){
                                days=days+" "+daysStringArray[_i]
                            }
                        }
                        reminder_interval.text =days

                    }


                    innerbuilder.setNegativeButton("取消"){dialog, which ->  }
                    innerbuilder.create().show()
                }
            }



            dialogInterface.dismiss()
        }

        val alertDialog=builder.create()
        alertDialog.show()
    }

    //按下選擇分類鍵
    private val categoryOnClickListener=View.OnClickListener{

        val builder = AlertDialog.Builder(this)
        builder.setTitle("選擇分類")
        builder.setIcon(R.drawable.ic_tag_grey_24dp)
        //用來給選單顯示的adapter
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice)
        //加入已儲存分類
        for(i in stored_category){
            arrayAdapter.add(i)
        }
        arrayAdapter.add("新增分類...")

        builder.setSingleChoiceItems(arrayAdapter,item.category) { dialogInterface, i ->
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

        }

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

    private val itemIconOnClickListener=View.OnClickListener {

        val view = layoutInflater.inflate(R.layout.sheet_main, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
        //相機鍵
        val camera: TextView = view.findViewById(R.id.camera)
        camera.setOnClickListener {
            //檢查設備有無相機
            val pm=this.packageManager
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                //要求使用權限
                val PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (!hasPermissions(this, *PERMISSIONS)) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_RCODE)
                }
                else {//開啟相機
                    takePictureIntent()
                    bottomSheetDialog.dismiss()
                }
            }
            else{
                Toast.makeText(this,"No camera detected in this device!",Toast.LENGTH_SHORT).show()
            }
        }
        //相簿鍵
        val gallery: TextView = view.findViewById(R.id.gallery)
        gallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, GALLERY_RCODE)
            bottomSheetDialog.dismiss()
        }
        //圖示鍵
        val icons:TextView=view.findViewById(R.id.icons)
        icons.setOnClickListener {
            showIconsCollection()
            bottomSheetDialog.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
           println(data)
        if (requestCode == CAMERA_RCODE && resultCode == Activity.RESULT_OK ) {
            //data=null 沒用到沒關係
            changeIcon=true
           // val bitmap = data.extras.get("data") as Bitmap
           // imageView_item_icon.setImageBitmap(bitmap)
            val resolver = this.contentResolver
            val uri = Uri.fromFile(myIconPath)
            val bitmap = MediaStore.Images.Media.getBitmap(resolver, uri)
            //壓縮
            setAndCompressPic(bitmap)
        }
        else if (requestCode == GALLERY_RCODE && resultCode == Activity.RESULT_OK && data != null) {
            changeIcon=true

            val resolver = this.contentResolver
            val uri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(resolver, uri)
            if(bitmap==null){//選擇了不是image類別檔案
                Toast.makeText(this,"Not supported image datatype!",Toast.LENGTH_LONG).show()
            }
            else {
                //壓縮
                setAndCompressPic(bitmap)
            }
        }
    }

    private fun setAndCompressPic(bitmap: Bitmap) {
        // 目標長寬
        val targetW = imageView_item_icon.width.toFloat()
        val targetH = imageView_item_icon.height.toFloat()
        // bitmap長寬
        val photoW = bitmap.width
        val photoH = bitmap.height
        // 比例不變下應縮放倍率
        val scaleFactor = Math.max(photoW / targetW, photoH / targetH)
            println(scaleFactor)
        val finalW=Math.round(photoW/scaleFactor)
        val finalH=Math.round(photoH/scaleFactor)
        // 縮放至imageview大小
        imageView_item_icon.setImageBitmap(Bitmap.createScaledBitmap(bitmap,finalW,finalH,false))
    }


    private fun takePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null)
        {
            // 照片儲存路徑
            var photoFile:File? =null
            try
            {
                //檔名
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val imageFileName = "JPEG_" + timeStamp + "_"
                val storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                 photoFile = File.createTempFile(
                        imageFileName, /* prefix */
                        ".jpg", /* suffix */
                        storageDir /* directory */
                )
               myIconPath= photoFile.absoluteFile
                println(myIconPath)
            }
            catch (ex: IOException) {}// Error occurred while creating the File
            // Continue only if the File was successfully created
            if (photoFile != null)
            {
                val photoURI = FileProvider.getUriForFile(this,
                        "practice.rimon.countdown.fileprovider",
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, CAMERA_RCODE)

                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                val file = myIconPath
                val contentUri = Uri.fromFile(file)
                mediaScanIntent.data = contentUri
                this.sendBroadcast(mediaScanIntent)
            }
        }
    }

    //檢查權限
    private fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

private fun showIconsCollection(){

    val builder = AlertDialog.Builder(this)

    val view_icons= LayoutInflater.from(this).inflate(R.layout.icons_grid,null)
    val gridView: GridView =view_icons.findViewById(R.id.gridview)

    val icons_array= arrayListOf(
            R.drawable.app_icon,
            R.drawable.app_icon_red,
            R.drawable.app_icon_org,
            R.drawable.app_icon_green,
            R.drawable.app_icon_yellow,
            R.drawable.app_icon_purple,
            R.drawable.app_icon_navy,
            R.drawable.app_icon_grey,
            R.drawable.app_icon_pink
    )
    gridView.adapter = ImageAdapter(this,icons_array)

    builder.setView(view_icons)
    builder.setTitle("圖示集")
    val icon_dialog=builder.show()

    gridView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        changeIcon=true
        imageView_item_icon.setImageResource(icons_array[position])
        icon_dialog.dismiss()
    }
}
}
