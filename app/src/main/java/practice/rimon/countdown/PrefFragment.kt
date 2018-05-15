package practice.rimon.countdown


import android.os.Bundle
import android.preference.PreferenceFragment

class PrefFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference)
    }


}
