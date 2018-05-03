package practice.rimon.countdown

import android.support.v7.widget.RecyclerView

interface ItemTouchHelperAdapter {
    //數據交換位置
    fun onItemMove(fromPosition: Int, toPosition: Int)

    //數據刪除
    fun onItemDissmiss(position: Int)
    //數據恢復
    fun restoreItem(item: Item, position: Int)

    //左滑行為
    fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int)
}