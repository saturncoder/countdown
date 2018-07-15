package practice.rimon.countdown

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun getArrayList(key: String,context:Context): ArrayList<String> {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val gson = Gson()
    val json = prefs.getString(key, "")
    if (json.isEmpty()){
        return arrayListOf(context.getString(R.string.unsorted),context.getString(R.string.life),
                            context.getString(R.string.work),context.getString(R.string.exam),
                             context.getString(R.string.anniversary))
    }
    else {
        val type = object : TypeToken<ArrayList<String>>() {}.type

        val arraylist: ArrayList<String> = gson.fromJson(json, type)
        return arraylist
    }
}