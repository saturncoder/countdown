package practice.rimon.countdown

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_item_list.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by rimon on 2018/4/26.
 */

class myAdapterWG(val mydata:ArrayList<Item>,val clickListener: (Int) -> Unit):
                                        RecyclerView.Adapter<myAdapterWG.viewholder>() {

val currenttime=Calendar.getInstance()
val timeFormat = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())


    override fun getItemCount(): Int {
        return mydata.size
    }

    override fun onBindViewHolder(holder: myAdapterWG.viewholder, position: Int) {

        holder.bindinfo(mydata[position])
        //position 從0開始編號
        holder.itemView.setOnClickListener{clickListener(holder.adapterPosition)}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myAdapterWG.viewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_list, parent, false)
        return viewholder(view)
    }

    inner class viewholder(view: View): RecyclerView.ViewHolder(view){
        fun bindinfo(item:Item){

                    itemView.textView_itemTitle_list.text=item.item_title
                    val daysbetween= timeToDays(item.eventDatetime)
                    itemView.textView_itemDaysBetween_list.text=daysbetween.toString()
                    //事件時間
                    //讀出事件時間並以特定格式顯示
                    val eventTime=Calendar.getInstance()
                    eventTime.timeInMillis=item.eventDatetime
                    itemView.textView_eventTime_list.text=timeFormat.format(eventTime.time)
                    //已過期提醒不顯示
                    if(item.alarmDatetime!=0L&& item.alarmDatetime-23L*60L*60L*1000L>currenttime.timeInMillis){
                        itemView.notif_icon.visibility=View.VISIBLE
                    }
                    else{itemView.notif_icon.visibility=View.INVISIBLE}

                    if(item.item_icon.isEmpty()){
                        Log.e("沒設icon","顯示預設圖示")
                    }
                    else if(item.item_icon.isNotEmpty()){
                        val img=BitmapFactory.decodeByteArray(item.item_icon,0,item.item_icon.size)
                        itemView.imageView_itemIcon.setImageBitmap(img)
                    }

                }


        }
    }
