package practice.rimon.countdown

/**
 * Created by rimon on 2018/4/26.
 */
class Item: java.io.Serializable  {

    var id:Long=0
    var item_icon: Int=0
    var item_title:String="default"
    var daysbetween:Int=333

    constructor() {
        item_icon = R.drawable.test
        item_title ="ddefault"
        daysbetween=222
    }

    constructor(id: Long, item_icon:Int, item_title:String, daysbetween:Int) {
        this.id = id
        this.item_icon=item_icon
        this.item_title=item_title
        this.daysbetween = daysbetween

    }
}