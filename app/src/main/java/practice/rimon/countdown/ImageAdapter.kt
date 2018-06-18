package practice.rimon.countdown

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import kotlinx.android.synthetic.main.single_icon.view.*

class ImageAdapter(private val mContext: Context,
                   private val icons_array:ArrayList<Int>) : BaseAdapter() {

    override fun getCount(): Int = icons_array.size

    override fun getItem(position: Int): Any? = null

    override fun getItemId(position: Int): Long = 0L

    // create a new ImageView for each item referenced by the Adapter
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView: ImageView

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            val singleItem=LayoutInflater.from(mContext).inflate(R.layout.single_icon,null)
            imageView = singleItem.single_icon
        }
        else {
            imageView = convertView as ImageView
        }

        imageView.setImageResource(icons_array[position])
        return imageView
    }
}