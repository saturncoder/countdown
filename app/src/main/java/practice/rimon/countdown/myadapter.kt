package practice.rimon.countdown

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_item_grid.view.*
import kotlinx.android.synthetic.main.layout_item_list.view.*
import java.util.*

/**
 * Created by rimon on 2018/4/26.
 */

class myAdapter(val mydata:ArrayList<Item>,
                val layoutmanager: GridLayoutManager,
                val recyclerView: RecyclerView,
                val itemDAO: ItemDAO,
                val clickListener: (Int) -> Unit):
                                        RecyclerView.Adapter<myAdapter.viewholder>(),ItemTouchHelperAdapter {
val currenttime=Calendar.getInstance()
    //滑動時的行為
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (viewHolder is myAdapter.viewholder) {

            // backup of removed item for undo purpose
            val deletedItem = mydata[viewHolder.getAdapterPosition()]
            val deletedIndex = viewHolder.getAdapterPosition()

            // remove the item from recycler view
            onItemDissmiss(viewHolder.getAdapterPosition())
            Log.e("adapter","移除項目$deletedIndex ,adapter剩下:${mydata.size}個")
            // showing snack bar with Undo option
            val snackbar = Snackbar.make(recyclerView, "Item removed !", Snackbar.LENGTH_LONG)
            snackbar.setAction("UNDO") {
                // undo is selected, restore the deleted item
                restoreItem(deletedItem, deletedIndex)
                recyclerView.smoothScrollToPosition(deletedIndex)

            }

            snackbar.addCallback(object :Snackbar.Callback(){
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    if(event!=DISMISS_EVENT_ACTION){
                        //確定沒有undo後，才從資料庫刪除
                        itemDAO.delete(deletedItem.id)
                        //取消已註冊提醒
                        if(deletedItem.alarmDatetime!=0L && deletedItem.alarmDatetime-23L*60L*60L*1000L>currenttime.timeInMillis) {
                            val cancellationIntent = Intent(recyclerView.context, CancelAlarmReceiver::class.java)
                            cancellationIntent.putExtra("item_id", deletedItem.id)
                            val broadcastCODE = deletedItem.id.toInt()
                            val cancellationPendingIntent = PendingIntent.getBroadcast(recyclerView.context, broadcastCODE, cancellationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                            val am = recyclerView.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                            am.set(AlarmManager.RTC_WAKEUP, currenttime.timeInMillis, cancellationPendingIntent)
                            Log.e("因項目${deletedItem.item_title}(id:${deletedItem.id})刪除而註銷提醒", "${currenttime.time}")
                        }
                    }
                }
            })
            snackbar.setActionTextColor(Color.YELLOW)
            snackbar.show()

        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        //recyclerView換位置
        Collections.swap(mydata,fromPosition,toPosition)
        notifyItemMoved(fromPosition,toPosition)
        //資料庫換位置

    }


    override fun onItemDissmiss(position: Int) {
        //資料庫在onswipe處理

        //recyclerView刪除
        mydata.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun restoreItem(item: Item, position: Int) {
        //recyclerView恢復
        mydata.add(position, item)
        // notify item added by position
        notifyItemInserted(position)
    }



    override fun getItemViewType(position: Int): Int {
        return layoutmanager.spanCount
        //1,2

    }
    override fun getItemCount(): Int {
        return mydata.size
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {

        holder.bindinfo(mydata[position])
        //position 從0開始編號
        holder.itemView.setOnClickListener{clickListener(holder.adapterPosition)}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        var view: View?=null
        when (viewType){
            1->  view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_list, parent, false)

            2->  view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_grid, parent, false)
        }

        return viewholder(view!!,viewType)
    }
    inner class viewholder(view: View, val viewType: Int): RecyclerView.ViewHolder(view){
        fun bindinfo(item:Item){
            when (viewType){
                1->{
                    itemView.textView_itemTitle_list.text=item.item_title
                    val daysbetween= timeToDays(item.eventDatetime)
                    itemView.textView_itemDaysBetween_list.text=daysbetween.toString()
                    //會有問題  要改掉
                    //itemView.imageView_itemIcon.setImageResource(item.item_icon)
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
                2->{
                    itemView.textView_itemTitle_grid.text=item.item_title
                    val daysbetween= timeToDays(item.eventDatetime)
                    itemView.textView_itemDaysBetween_grid.text=daysbetween.toString()
                    if(item.alarmDatetime!=0L && item.alarmDatetime-23L*60L*60L*1000L>currenttime.timeInMillis){
                        itemView.item_notif_icon.visibility=View.VISIBLE
                    }
                    else{itemView.item_notif_icon.visibility=View.INVISIBLE}
                }
            }
        }
    }
}