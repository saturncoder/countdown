package practice.rimon.countdown
//自訂dialogfragment 沒用了
/*
class MyDialogFragment: DialogFragment() {
    /*
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.icons_layout, container)
        val mRecyclerView = view.findViewById(R.id.icons_recyclerView) as RecyclerView
        mRecyclerView.layoutManager = GridLayoutManager(activity,3)
        val icons= arrayListOf(
                R.drawable.app_icon,
                R.drawable.test,
                R.drawable.grid_bg,
                R.drawable.test,
                R.drawable.ic_tag_grey_24dp,
                R.drawable.app_icon,
                R.drawable.test,
                R.drawable.grid_bg,
                R.drawable.test,
                R.drawable.ic_tag_grey_24dp,
                R.drawable.grid_bg,
                R.drawable.test,
                R.drawable.ic_tag_grey_24dp,
                R.drawable.app_icon,
                R.drawable.test,
                R.drawable.grid_bg
        )
        mRecyclerView.adapter = iconsAdapter(icons,{ position:Int->
            Log.e("選擇icon", "Clicked on item $position")

        })
        return view
    }*/

    interface mDialogListener {
        fun onDialogPositiveClick(dialog:DialogFragment,iconID:Int)
        fun onDialogNegativeClick(dialog:DialogFragment)
    }
var mListener:mDialogListener? =null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try
        {
            // Instantiate the NoticeDialogListener so we can send events to the host
           mListener = activity as mDialogListener
        }
        catch (e:ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((activity.toString() + " must implement NoticeDialogListener"))
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("test")
        var itemClickedID=-1 //預設 沒選
        val view = LayoutInflater.from(context).inflate(R.layout.icons_layout,null)
        val mRecyclerView = view.findViewById(R.id.icons_recyclerView) as RecyclerView
        mRecyclerView.layoutManager = GridLayoutManager(activity,3)
        val icons= arrayListOf(
                R.drawable.app_icon,
                R.drawable.test,
                R.drawable.grid_bg,
                R.drawable.test,
                R.drawable.ic_tag_grey_24dp,
                R.drawable.app_icon,
                R.drawable.test
                /*R.drawable.grid_bg,
                R.drawable.test,
                R.drawable.ic_tag_grey_24dp,
                R.drawable.grid_bg,
                R.drawable.test,
                R.drawable.ic_tag_grey_24dp,
                R.drawable.app_icon,
                R.drawable.test,
                R.drawable.grid_bg*/
        )
        mRecyclerView.adapter = iconsAdapter(icons,{ position:Int->
            Log.e("選擇icon", "Clicked on item $position")
            itemClickedID=icons[position]

        })
        builder.setView(view)
        builder.setPositiveButton("ok") { dialog, id ->
            // Send the positive button event back to the host activity
            mListener!!.onDialogPositiveClick(this@MyDialogFragment,itemClickedID)
        }
         builder.setNegativeButton("no") { dialog, id ->
                    // Send the negative button event back to the host activity
                    mListener!!.onDialogNegativeClick(this@MyDialogFragment)
                }

        return builder.create()
    }

}*/
