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
        return arrayListOf("未分類","生活","工作","考試","紀念日")
    }
    else {
        val type = object : TypeToken<ArrayList<String>>() {}.type

        val arraylist: ArrayList<String> = gson.fromJson(json, type)
        return arraylist
    }
}