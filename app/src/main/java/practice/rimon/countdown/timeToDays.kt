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
    val date=cal.get(Calendar.DAY_OF_MONTH)
    val eventDate= LocalDate(year,month+1,date)
    //用joda time算差幾天
    val today= LocalDate.now()
    val days= Days.daysBetween(today,eventDate) //start,end   days是object 裡面的getdays才是天數
    val daysbetween=days.days
    return daysbetween
}