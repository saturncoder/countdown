package practice.rimon.countdown

import android.os.Bundle
import android.preference.PreferenceActivity

class PrefActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentManager.beginTransaction().replace(
                android.R.id.content, PrefFragment()).commit()
    }

    // Android 4.4、API level 19以後的版本須覆寫，檢查使用的Fragment是否有效
    override fun isValidFragment(fragmentName: String): Boolean {
        return PrefFragment::class.java.name == fragmentName
    }
}
