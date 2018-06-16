package practice.rimon.countdown

import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener{

    val FAB_addITEM_REQUEST_CODE=1
    val editItem_REQUEST_CODE=2
    val themeActivityCode=333
    lateinit var toggle:ActionBarDrawerToggle

    val mydata=ArrayList<Item>()
    val gridlayoutmanager= GridLayoutManager(this,1)
    lateinit var  myadapter:myAdapter
    private val itemDAO : ItemDAO by lazy { ItemDAO(applicationContext) }
    var stored_category=ArrayList<String>()
    var custom_category=false
    var categroy_now=-1
    var category_num=0

    override fun onCreate(savedInstanceState: Bundle?) {

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val themeID = prefs.getInt("theme", 0)
        when(themeID){
            0->setTheme(R.style.StartTheme)
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
        setContentView(R.layout.activity_main)

        setup()

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        menu!!.getItem(0).isVisible = custom_category
        menu.getItem(1).isVisible = custom_category
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == R.id.switchLayout){

            if(gridlayoutmanager.spanCount==1) {
                gridlayoutmanager.spanCount = 2
                //item.setIcon(R.drawable.icon_menu_1)
            }
            else{
                gridlayoutmanager.spanCount = 1
                //item.setIcon(R.drawable.icon_menu_2)
            }
            // recycler.adapter.notifyItemRangeChanged(0,mydata.size)
            // recycler.adapter.notifyDataSetChanged()

            return true
        }
        else if(item.itemId==R.id.delete_category){
            when (categroy_now) {
                -1,0,1,2,3,4->{}
                5->{
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("刪除分類")
                    builder.setMessage("所有在此分類的項目將被歸回未分類中")
                    builder.setPositiveButton( "確定",  {
                        dialogInterface, i ->

                        stored_category.removeAt(5)
                        saveArrayList(stored_category,"category",this)
                        if(itemDAO.category(5).size!=0){
                            //分類都變0
                            categoryTo(5,0)

                        }
                        if(itemDAO.category(6).size!=0){
                            //分類都變5
                            categoryTo(6,5)
                        }
                        if(itemDAO.category(7).size!=0){
                            //分類都變6
                            categoryTo(7,6)
                        }
                        //跳至未分類
                        saveToSharedPref(0)
                        val intent=Intent(this,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    })
                    builder.setNegativeButton( "取消",  {dialogInterface, i ->    })

                    val alert=builder.create()
                    alert.show()

                }
                6->{
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("刪除分類")
                    builder.setMessage("所有在此分類的項目將被歸回未分類中")
                    builder.setPositiveButton( "確定",  {
                        dialogInterface, i ->

                        stored_category.removeAt(6)
                        saveArrayList(stored_category,"category",this)
                        if(itemDAO.category(6).size!=0){
                            //分類都變0
                            categoryTo(6,0)

                        }

                        if(itemDAO.category(7).size!=0){
                            //分類都變6
                            categoryTo(7,6)
                        }
                        saveToSharedPref(0)
                        val intent=Intent(this,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    })
                    builder.setNegativeButton( "取消",  {dialogInterface, i ->    })

                    val alert=builder.create()
                    alert.show()

                }
                7->{
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("刪除分類")
                    builder.setMessage("所有在此分類的項目將被歸回未分類中")
                    builder.setPositiveButton( "確定",  {
                        dialogInterface, i ->

                        stored_category.removeAt(7)
                        saveArrayList(stored_category,"category",this)
                        if(itemDAO.category(7).size!=0){
                            //分類都變0
                            categoryTo(7,0)
                        }
                        saveToSharedPref(0)
                        val intent=Intent(this,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    })
                    builder.setNegativeButton( "取消",  {dialogInterface, i ->    })

                    val alert=builder.create()
                    alert.show()

                }


            }
            return true
        }
        else if(item.itemId==R.id.category_edit){
            when(categroy_now){
                -1,0,1,2,3,4->{}
                5->{editdialog(5)}
                6->{editdialog(6)}
                7->{editdialog(7)}
            }

        }
        if(toggle.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
    }
    private fun setup(){

        recyclerview.layoutManager=gridlayoutmanager

        // 如果資料庫是空的，就建立一些範例資料
        if (itemDAO.count == 0) {
            itemDAO.createSampleData()
        }

        myadapter=myAdapter(mydata,gridlayoutmanager,recyclerview,itemDAO,{ position:Int->
            Log.e("clicklistener", "Clicked on item $position")
            itemClickHandler(position)
        })

        val callback=ItemTouchHelperCallback(myadapter)
        val itemTouchHelper= ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerview)

        recyclerview.adapter= myadapter

        //載入自訂分類
        stored_category=getArrayList("category",this)
        println(stored_category)
        //如果有自訂的分類項目，加入左側選單
        category_num=stored_category.size
        if(category_num>5){
            val category_icons= arrayOf(R.drawable.ic_looks_one_black_24dp,R.drawable.ic_looks_two_black_24dp,R.drawable.ic_looks_3_black_24dp)
            for(i in 5 until  category_num){
                //groupId ,itemId ,order(weight) ,title    要定位到同個menu
                val item=navigation_view.menu.getItem(0).subMenu.add(R.id.group1,i,1,stored_category[i])
                item.setIcon(category_icons[i-5])

            }
            //要設成單選才能 highlight
            navigation_view.menu.getItem(0).subMenu.setGroupCheckable(R.id.group1,true,true)
        }

        navigation_view.setNavigationItemSelectedListener(this)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val toCategory = prefs.getInt("toCategory", -1)
        //預設分類  (載入全項目)
        this.onNavigationItemSelected(navigation_view.menu.getItem(0).subMenu.getItem(toCategory+1))
        println(toCategory)

        when (toCategory) {
            -1 -> navigation_view.setCheckedItem(R.id.category_all)
            0 -> navigation_view.setCheckedItem(R.id.category_unsorted)
            1 -> navigation_view.setCheckedItem(R.id.category1)
            2 -> navigation_view.setCheckedItem(R.id.category2)
            3 -> navigation_view.setCheckedItem(R.id.category3)
            4 -> navigation_view.setCheckedItem(R.id.category4)
            5 -> navigation_view.setCheckedItem(navigation_view.menu.getItem(0).subMenu.getItem(6).itemId)
            6 -> navigation_view.setCheckedItem(navigation_view.menu.getItem(0).subMenu.getItem(7).itemId)
            7 -> navigation_view.setCheckedItem(navigation_view.menu.getItem(0).subMenu.getItem(8).itemId)
        }
        //跳轉完清除等待下次recreate的跳轉指令
        prefs.edit().remove("toCategory").apply()

        FAB_additem.setOnClickListener(FabClickListener)

        //左上圖標
        toggle = ActionBarDrawerToggle(
                this, drawer_layout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //滾動時使fab消失
        recyclerview.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if ( dy >0  && FAB_additem.isShown)
                    FAB_additem.hide()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    FAB_additem.show()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    private val FabClickListener=View.OnClickListener {
        val intent=Intent("practice.rimon.countdown.ADD_ITEM")
        startActivityForResult(intent,FAB_addITEM_REQUEST_CODE)
    }


    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        Log.e("selectDrawerItem","on ${menuItem.itemId}")

        when(menuItem.itemId) {
            R.id.category_all->{

                mydata.clear()
                mydata.addAll(itemDAO.all)
                myadapter.notifyDataSetChanged()
                title="countdown"
                custom_category=false
                categroy_now=-1
            }
            R.id.category_unsorted->{

                mydata.clear()
                mydata.addAll(itemDAO.category(0))
                myadapter.notifyDataSetChanged()
                title="未分類"
                custom_category=false
                categroy_now=0
            }
            R.id.category1->{

                mydata.clear()
                mydata.addAll(itemDAO.category(1))
                myadapter.notifyDataSetChanged()
                title="生活"
                custom_category=false
                categroy_now=1
            }
            R.id.category2->{

                mydata.clear()
                mydata.addAll(itemDAO.category(2))
                myadapter.notifyDataSetChanged()
                title="工作"
                custom_category=false
                categroy_now=2
            }
            R.id.category3->{

                mydata.clear()
                mydata.addAll(itemDAO.category(3))
                myadapter.notifyDataSetChanged()
                title="考試"
                custom_category=false
                categroy_now=3
            }
            R.id.category4->{

                mydata.clear()
                mydata.addAll(itemDAO.category(4))
                myadapter.notifyDataSetChanged()
                title="紀念日"
                custom_category=false
                categroy_now=4
            }
            5->{
                mydata.clear()
                mydata.addAll(itemDAO.category(5))
                myadapter.notifyDataSetChanged()
                title=stored_category[5]
                custom_category=true
                categroy_now=5

            }
            6->{
                mydata.clear()
                mydata.addAll(itemDAO.category(6))
                myadapter.notifyDataSetChanged()
                title=stored_category[6]
                custom_category=true
                categroy_now=6

            }
            7->{
                mydata.clear()
                mydata.addAll(itemDAO.category(7))
                myadapter.notifyDataSetChanged()
                title=stored_category[7]
                custom_category=true
                categroy_now=7
            }
            R.id.settings-> {

                startActivity(Intent(this, PrefActivity::class.java))
            }
            R.id.theme-> {
                val intent=Intent(this,ThemeActivity::class.java)
                startActivityForResult(intent,themeActivityCode)
            }

        }
        //再生成一次actionbar  決定刪除分類鍵可不可見
        invalidateOptionsMenu()
        //menuItem.isChecked = true

        drawer_layout.closeDrawers()

        return true
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==FAB_addITEM_REQUEST_CODE || requestCode==editItem_REQUEST_CODE) {
            var toCategory = 0
            //新增
            if (requestCode == FAB_addITEM_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                // 讀取物件  (放裡面避免返回時data是null)
                val item = data!!.extras.getSerializable("countdown.Item") as Item
                // 新增資料到資料庫
                val itemNew: Item = itemDAO.insert(item)
                // 讀出物件的編號
                item.id = itemNew.id
                Log.e("itemID", "${itemNew.id}")
                // 加入新增的記事物件
                mydata.add(item)
                //設提醒
                setNotification(item)
                //讀出物件分類
                toCategory = item.category
            }
            else if (requestCode == editItem_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                // 讀取物件
                val item = data!!.extras.getSerializable("countdown.Item") as Item
                val position = data.extras.getInt("array_position")
                // 更新資料庫物件
                itemDAO.update(item)
                //

                // 加入的記事物件
                mydata.set(position, item)
                Log.e("有無設置提醒", "${item.alarmDatetime}")
                //設提醒
                setNotification(item)
                //讀出物件分類
                toCategory = item.category
            }
            // 通知資料改變
            myadapter.notifyDataSetChanged()

            //檢查是否有新增分類
            val newcategory_num = data!!.getIntExtra("category_num", 0)
            if (newcategory_num > category_num) {
                //重整後再跳轉
                saveToSharedPref(toCategory)
                val intent=Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()

                //recreate()
            }
            else {
                //跳轉至該分類  (分類的號碼+1(多了 全部))
                this.onNavigationItemSelected(navigation_view.menu.getItem(0).subMenu.getItem(toCategory + 1))
                when (toCategory + 1) {
                    1 -> navigation_view.setCheckedItem(R.id.category_unsorted)
                    2 -> navigation_view.setCheckedItem(R.id.category1)
                    3 -> navigation_view.setCheckedItem(R.id.category2)
                    4 -> navigation_view.setCheckedItem(R.id.category3)
                    5 -> navigation_view.setCheckedItem(R.id.category4)
                    6 -> navigation_view.setCheckedItem(navigation_view.menu.getItem(0).subMenu.getItem(6).itemId)
                    7 -> navigation_view.setCheckedItem(navigation_view.menu.getItem(0).subMenu.getItem(7).itemId)
                    8 -> navigation_view.setCheckedItem(navigation_view.menu.getItem(0).subMenu.getItem(8).itemId)
                }
            }
        }
        //改變主題顏色
        else if(requestCode==themeActivityCode && resultCode== Activity.RESULT_OK){
            Log.e("activity","從themeActivity返回")
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()

        }
    }

    private fun itemClickHandler(position:Int){
     println("item ${position+1} clicked")

        val intent=Intent("practice.rimon.countdown.EDIT_ITEM")
        val itemclicked=mydata[position]
        intent.putExtra("itemclicked",itemclicked)
        intent.putExtra("array_position",position)
        startActivityForResult(intent,editItem_REQUEST_CODE)

    }

    private fun setNotification(item:Item){
        //若有開啟提醒則設置提醒
        if(item.alarmDatetime!=0L){
            //檢查事件是否過期
            val intent = Intent(this, AlarmReceiver::class.java)
            intent.putExtra("title", item.item_title)
            intent.putExtra("item_id", item.id)
            intent.putExtra("item_eventDatetime",item.eventDatetime)
            val broadcastCODE = item.id.toInt()
            Log.e("提醒的requestcode", "$broadcastCODE")
            val pendingIntent = PendingIntent.getBroadcast(this, broadcastCODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            //註冊第一次提醒時間
            val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, item.alarmAt, AlarmManager.INTERVAL_DAY, pendingIntent)

            Log.e("第一次提醒時間", "${millsToCal(item.alarmAt).time}")
            //註冊提醒到期時間
            val cancellationIntent = Intent(this, CancelAlarmReceiver::class.java)
            cancellationIntent.putExtra("item_id", item.id)
            val cancellationPendingIntent = PendingIntent.getBroadcast(this, broadcastCODE, cancellationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            am.set(AlarmManager.RTC_WAKEUP, item.alarmDatetime, cancellationPendingIntent)
            Log.e("提醒到期時間", "${millsToCal(item.alarmDatetime).time}")

        }
        //沒提醒要把原本註冊的提醒關掉
        else{
            val calendar=Calendar.getInstance()
            val cancellationIntent = Intent(this, CancelAlarmReceiver::class.java)
            cancellationIntent.putExtra("item_id", item.id)
            val broadcastCODE = item.id.toInt()
            val cancellationPendingIntent = PendingIntent.getBroadcast(this, broadcastCODE, cancellationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, cancellationPendingIntent)
            Log.e("即刻註銷已註冊提醒", "${calendar.time}")
        }

    }

    private fun categoryTo(from:Int,newCategory:Int){
        val categoryfrom=itemDAO.category(from)
        for(i in 0 until categoryfrom.size){
            categoryfrom[i].category=newCategory
            itemDAO.update(categoryfrom[i])
        }
    }

    private fun editdialog(which:Int){
        val builder = AlertDialog.Builder(this)
        val edittext = EditText(this)
        edittext.setSingleLine(true)
        edittext.setText(stored_category[which])
        edittext.setSelection(edittext.text.length)
        builder.setTitle("修改分類名稱")
        builder.setView(edittext)

        builder.setPositiveButton("確定",null)
        builder.setNegativeButton("取消",null)
        val alertdialog=builder.show()
        val confirm=alertdialog.getButton(AlertDialog.BUTTON_POSITIVE)
        confirm.setOnClickListener {
            if(TextUtils.isEmpty(edittext.text.toString().trim())) {
                Toast.makeText(this, "請輸入分類名稱", Toast.LENGTH_LONG).show()
            }
            else {
                stored_category[which]=edittext.text.toString()
                saveArrayList(stored_category,"category",this)
                alertdialog.dismiss()
                saveToSharedPref(which)
                recreate()
            }
        }
    }

    private fun saveToSharedPref(which:Int){
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = prefs.edit()
        editor.putInt("toCategory", which)
        editor.apply()
    }
}
