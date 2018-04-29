package practice.rimon.countdown

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_item_grid.view.*
import kotlinx.android.synthetic.main.layout_item_list.view.*

/**
 * Created by rimon on 2018/4/26.
 */

class myAdapter(val mydata:ArrayList<Item>,val layoutmanager: GridLayoutManager): RecyclerView.Adapter<myAdapter.viewholder>() {

    override fun getItemViewType(position: Int): Int {
        return layoutmanager.spanCount
        //1,2

    }
    override fun getItemCount(): Int {
        return mydata.size
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {

        holder.bindinfo(mydata[position])

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
                    itemView.textView_itemDaysBetween_list.text=item.daysbetween.toString()
                    itemView.imageView_itemIcon.setImageResource(item.item_icon)
                }
                2->{
                    itemView.textView_itemTitle_grid.text=item.item_title
                    itemView.textView_itemDaysBetween_grid.text=item.daysbetween.toString()



                }
            }
        }
    }
}