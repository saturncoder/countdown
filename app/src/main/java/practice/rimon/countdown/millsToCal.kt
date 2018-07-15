package practice.rimon.countdown

import java.util.*

fun millsToCal(timeinmills:Long):Calendar
{
    val read= Calendar.getInstance()
    read.timeInMillis=timeinmills
    return read
}