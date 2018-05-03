package practice.rimon.countdown

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_DRAG
import android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE
import kotlinx.android.synthetic.main.layout_item_list.view.*

class ItemTouchHelperCallback(val touchAdapter:ItemTouchHelperAdapter): ItemTouchHelper.Callback() {
    //設置允許手勢行為
    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN        //允許上下拖動
        val swipeFlags = ItemTouchHelper.START    //允許右到左滑動 or ItemTouchHelper.END
        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
    }
    //拖曳行為 換位置
    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        if (viewHolder != null && target != null) {
            touchAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }
        return false //預設不給拖曳
    }
    //滑動行為
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
        if (viewHolder!=null) {
            println(viewHolder.adapterPosition)
            touchAdapter.onSwiped(viewHolder, direction, viewHolder.adapterPosition)


        }
    }
    //項目被選中且在移動時
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder != null&& actionState==ACTION_STATE_SWIPE) {
            val foregroundView = (viewHolder as myAdapter.viewholder).itemView.viewForeground
            ItemTouchHelper.Callback.getDefaultUIUtil().onSelected(foregroundView)
        }
        else if(viewHolder != null&& actionState== ACTION_STATE_DRAG){
            viewHolder.itemView.alpha = 0.7f
        }

        super.onSelectedChanged(viewHolder, actionState)
    }
    //行為發生時如何視圖做動畫
    override fun onChildDraw(c: Canvas?, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

        if(actionState==ACTION_STATE_SWIPE) {
           val foregroundView = (viewHolder as myAdapter.viewholder).itemView.viewForeground
           ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                   actionState, isCurrentlyActive)

       }
        else {

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }
    //行為發生時如何視圖做動畫 (舊版本)

    override fun onChildDrawOver(c: Canvas?, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if(actionState==ACTION_STATE_SWIPE) {
            val foregroundView = (viewHolder as myAdapter.viewholder).itemView.viewForeground
            ItemTouchHelper.Callback.getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive)

        }
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    //清空viewholder有改變的動畫(onchilddraw中設定的)
    override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) {

        val foregroundView = (viewHolder as myAdapter.viewholder).itemView.viewForeground
        ItemTouchHelper.Callback.getDefaultUIUtil().clearView(foregroundView)
        //移動結束後透明度改回來
        viewHolder.itemView.alpha = 1f
    }

}