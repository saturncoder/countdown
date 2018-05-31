package practice.rimon.countdown

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.Gson

//儲存arraylist到sharedPreference
fun saveArrayList(list: ArrayList<String>, key: String,context:Context) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = prefs.edit()
    val gson = Gson()
    val json = gson.toJson(list)
    editor.putString(key, json)
    editor.apply()     // This line is IMPORTANT !!!
}