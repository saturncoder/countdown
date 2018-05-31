package practice.rimon.countdown

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(){

    val FAB_addITEM_REQUEST_CODE=1
    val editItem_REQUEST_CODE=2
    lateinit var toggle:ActionBarDrawerToggle

    val mydata=ArrayList<Item>()
    val gridlayoutmanager= GridLayoutManager(this,1)
    lateinit var  myadapter:myAdapter
    private val itemDAO : ItemDAO by lazy { ItemDAO(applicationContext) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setup()
        //要定位到同個menu
        //navigation_view.menu.getItem(0).subMenu.add(R.id.group1,Menu.NONE,1,"2222")
        val stored_category=getArrayList("category",this)
        println(stored_category)
        println(stored_category.size)
        //如果有自訂的分類項目
        if(stored_category.size>5){
            for(i in 5 until  stored_category.size){
                //groupId ,itemId ,order ,title
                navigation_view.menu.getItem(0).subMenu.add(R.id.group1,i,1,stored_category[i])
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
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

            println(itemDAO.count)
        }
        //載入database所有item
        mydata.addAll(itemDAO.all)

        myadapter=myAdapter(mydata,gridlayoutmanager,recyclerview,itemDAO,{ position:Int->
            Log.e("clicklistener", "Clicked on item $position")
            itemClickHandler(position)
        })
        val callback=ItemTouchHelperCallback(myadapter)
        val itemTouchHelper= ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerview)

        recyclerview.adapter= myadapter

        FAB_additem.setOnClickListener(FabClickListener)
        //左上圖標
        toggle = ActionBarDrawerToggle(
                this, drawer_layout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        navigation_view.setNavigationItemSelectedListener(navigationItemSelectedListener)
        //預設分類
        navigation_view.setCheckedItem(R.id.category1)
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
    private val navigationItemSelectedListener = NavigationView.OnNavigationItemSelectedListener { menuItem ->
        Log.e("selectDrawerItem","on ${menuItem.itemId}")

        when(menuItem.itemId) {
            R.id.category_all->{

                mydata.clear()
                mydata.addAll(itemDAO.all)
                myadapter.notifyDataSetChanged()
                title="countdown"
            }
            R.id.category_unsorted->{

                mydata.clear()
                mydata.addAll(itemDAO.category(0))
                myadapter.notifyDataSetChanged()
                title="未分類"
            }
            R.id.category1->{

                mydata.clear()
                mydata.addAll(itemDAO.category(1))
                myadapter.notifyDataSetChanged()
                title="生活"
            }
            R.id.category2->{

                mydata.clear()
                mydata.addAll(itemDAO.category(2))
                myadapter.notifyDataSetChanged()
                title="工作"
            }
            R.id.category3->{

                mydata.clear()
                mydata.addAll(itemDAO.category(3))
                myadapter.notifyDataSetChanged()
                title="考試"
            }
            R.id.category4->{

                mydata.clear()
                mydata.addAll(itemDAO.category(4))
                myadapter.notifyDataSetChanged()
                title="紀念日"
            }
            5->{
                mydata.clear()
                mydata.addAll(itemDAO.category(5))
                myadapter.notifyDataSetChanged()
                title="5"
            }
            6->{
                mydata.clear()
                mydata.addAll(itemDAO.category(6))
                myadapter.notifyDataSetChanged()
                title="6"
            }
            7->{
                mydata.clear()
                mydata.addAll(itemDAO.category(7))
                myadapter.notifyDataSetChanged()
                title="7"
            }
            R.id.settings-> {

                startActivity(Intent(this, PrefActivity::class.java))
            }
            R.id.theme-> {
                startActivity(Intent(this, ThemeActivity::class.java))
            }

        }

        //menuItem.isChecked = true

        drawer_layout.closeDrawers()

        true

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //新增
        if(requestCode==FAB_addITEM_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            // 讀取物件  (放裡面避免返回時data是null)
            val item = data!!.extras.getSerializable("countdown.Item") as Item
            // 新增資料到資料庫
            val itemNew : Item = itemDAO.insert(item)
            // 讀出物件的編號
            item.id = itemNew.id
           Log.e("itemID","${itemNew.id}")
            // 加入新增的記事物件
            mydata.add(item)
            //設提醒
            setNotification(item)

        }
        else if (requestCode==editItem_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            // 讀取物件
            val item = data!!.extras.getSerializable("countdown.Item") as Item
            val position=data.extras.getInt("array_position")
            // 更新資料庫物件
            itemDAO.update(item)
            //

            // 加入的記事物件
            mydata.set(position,item)
            Log.e("有無設置提醒","${item.alarmDatetime}")
            //設提醒
            setNotification(item)
        }
        // 通知資料改變
        myadapter.notifyDataSetChanged()
        recreate()
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

}
