package practice.rimon.countdown

/**
 * Created by rimon on 2018/4/26.
 */
class Item: java.io.Serializable  {

    var id:Long=0  //資料庫的唯一編號 unique
    var item_icon: Int=0
    var item_title:String="default"
    var eventDatetime:Long=0
    var alarmDatetime:Long=0

    constructor() {
        item_icon = R.drawable.test
        item_title ="ddefault"
        eventDatetime=0L
        alarmDatetime=0L
    }

    constructor(id: Long, item_icon:Int, item_title:String, eventDatetime:Long,alarmDatetime:Long) {
        this.id = id
        this.item_icon=item_icon
        this.item_title=item_title
        this.eventDatetime = eventDatetime
        this.alarmDatetime=alarmDatetime

    }
}