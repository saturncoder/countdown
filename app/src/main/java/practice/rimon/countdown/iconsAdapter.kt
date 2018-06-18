package practice.rimon.countdown

//沒用了
/*
class iconsAdapter(val icons: ArrayList<Int>, val clickListener: (Int) -> Unit)    : RecyclerView.Adapter<iconsAdapter.viewholder>() {

var mSelectedItem=-1
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): iconsAdapter.viewholder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.single_icon, parent, false)
        return viewholder(view)
    }

    override fun getItemCount(): Int {
        return icons.size
    }

    override fun onBindViewHolder(holder: iconsAdapter.viewholder?, position: Int) {
        holder!!.itemView.single_icon.setImageResource(icons[position])
        if(position==mSelectedItem){
            holder.itemView.single_icon.setBackgroundColor(Color.BLUE)
        }

        holder.itemView.single_icon.setOnClickListener {
            clickListener(holder.adapterPosition)

        }

    }
    inner class viewholder(view: View): RecyclerView.ViewHolder(view){
       init{
            val image = view.findViewById(R.id.text) as ImageButton

            val clickListener = View.OnClickListener {
                mSelectedItem = adapterPosition
                notifyDataSetChanged()
            }
            image.setOnClickListener(clickListener)

        }
    }
}
*/