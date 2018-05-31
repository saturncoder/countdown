package practice.rimon.countdown

import org.joda.time.Days
import org.joda.time.LocalDate
import java.util.*

fun timeToDays(timeinmillis:Long):Int{
    //讀出紀錄的事件日期
    val cal=Calendar.getInstance()
    cal.timeInMillis=timeinmillis
    val year=cal.get(Calendar.YEAR)
    val month=cal.get(Calendar.MONTH)
    val day=cal.get(Calendar.DAY_OF_MONTH)

    //Log.e("紀錄的時間","$year 年, ${month+1} 月, $day 日")
    val eventDate= LocalDate(year,month+1,day)
    //用joda time算差幾天
    //val today= LocalDate.now()//UTC 會錯
    val now=Calendar.getInstance()
    val y=now.get(Calendar.YEAR)
    val m=now.get(Calendar.MONTH)
    val d=now.get(Calendar.DAY_OF_MONTH)
    val today=LocalDate(y,m+1,d)
    //Log.e("今天","$y 年, ${m+1} 月, $d 日")
    val days= Days.daysBetween(today,eventDate) //start,end   days是object 裡面的getdays才是天數
    val daysbetween=days.days
    return daysbetween
}