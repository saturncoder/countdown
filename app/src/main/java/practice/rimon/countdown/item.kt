package practice.rimon.countdown

/**
 * Created by rimon on 2018/4/26.
 */
class Item: java.io.Serializable  {

    var id:Long=0  //資料庫的唯一編號 unique
    var item_icon: ByteArray
    var item_title:String="default"
    var eventDatetime:Long=0
    var alarmDatetime:Long=0
    var alarmAt:Long=0
    var alarmInterval:Long=0
    var category:Int=0
    var memo:String=""

    constructor() {
        item_icon = byteArrayOf()
        item_title ="default"
        eventDatetime=0L
        alarmDatetime=0L
        alarmAt=0L
        alarmInterval=0L
        category=0
        memo=""
    }

    constructor(id: Long, item_icon:ByteArray, item_title:String, eventDatetime:Long
                ,alarmDatetime:Long, alarmAt:Long, alarmInterval:Long
                ,category:Int,memo:String) {
        this.id = id
        this.item_icon=item_icon
        this.item_title=item_title
        this.eventDatetime = eventDatetime
        this.alarmDatetime=alarmDatetime
        this.alarmAt=alarmAt
        this.alarmInterval=alarmInterval
        this.category=category
        this.memo=memo
    }
}