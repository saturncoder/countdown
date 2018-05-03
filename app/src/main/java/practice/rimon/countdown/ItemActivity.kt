package practice.rimon.countdown

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_item.*
import net.danlew.android.joda.JodaTimeAndroid
import org.joda.time.Days
import org.joda.time.LocalDate
import java.util.*

class ItemActivity : AppCompatActivity() {
    //取得一個實例，時間為現在時間
    val calendar= Calendar.getInstance()
    val localdate_now= LocalDate.now()
    var item=Item()
    var daysbetween:Int=0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        JodaTimeAndroid.init(this)

        val action=intent.action //oncreate完才抓的到

        if(action=="practice.rimon.countdown.EDIT_ITEM"){
            val itemselected=intent.getSerializableExtra("itemclicked") as Item
           Log.e("item","$itemselected")
            item.id=itemselected.id

            editText_item_title.setText(itemselected.item_title)
            textView_item_daysbetween.text = itemselected.daysbetween.toString()
        }


        textView_item_eventTime.setOnClickListener(eventTimeOnClickListener)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.confirm) {
           additem()
            finish()
        return true
    }


        return super.onOptionsItemSelected(item)
    }

    private val eventTimeOnClickListener= View.OnClickListener { view ->
        DatePickerDialog(this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show()
    }
    private val dateSetListener= DatePickerDialog.OnDateSetListener{ view, year, month, day->
        // Int,Int,Int
        //month:0=一月
        //joda-time
        val selected_datetime= LocalDate(year,month+1,day)
        val days= Days.daysBetween(localdate_now,selected_datetime) //start,end   days是object 裡面的getdays才是天數
        daysbetween=days.days
        //format傳回字串型態
        textView_item_eventTime.text=selected_datetime.toString(" yyyy-MM-dd E")
        textView_item_daysbetween.text=daysbetween.toString()


    }
    private fun additem(){
        item.item_icon=R.drawable.test
        item.item_title=editText_item_title.text.toString()
        item.daysbetween=daysbetween
        println("press OK")
        intent.putExtra("countdown.Item", item)
        setResult(Activity.RESULT_OK, intent)
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //crash
            // 設定回應結果為取消
            setResult(Activity.RESULT_CANCELED, intent)
        }

        return super.onKeyDown(keyCode, event)
    }
}
