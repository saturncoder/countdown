package practice.rimon.countdown

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

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
        // 這是為了方便測試用的，完成應用程式以後可以拿掉
        if (itemDAO.count == 0) {
            itemDAO.createSampleData()
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
    }

    private val FabClickListener=View.OnClickListener {
        val intent=Intent("practice.rimon.countdown.ADD_ITEM")
        startActivityForResult(intent,FAB_addITEM_REQUEST_CODE)
    }
    private val navigationItemSelectedListener = NavigationView.OnNavigationItemSelectedListener { menuItem ->
        Log.e("selectDrawerItem","on ${menuItem.itemId}")


        when(menuItem.itemId) {
            R.id.item1-> { }

        }

        menuItem.setChecked(true)

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
            println(item.id)
            // 加入新增的記事物件
            mydata.add(item)

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

        }
        // 通知資料改變
        myadapter.notifyDataSetChanged()

    }

    private fun itemClickHandler(position:Int){
     println("item ${position+1} clicked")

        val intent=Intent("practice.rimon.countdown.EDIT_ITEM")
        val itemclicked=mydata[position]
        intent.putExtra("itemclicked",itemclicked)
        intent.putExtra("array_position",position)
        startActivityForResult(intent,editItem_REQUEST_CODE)

    }
}
