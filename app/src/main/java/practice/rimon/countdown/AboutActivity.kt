package practice.rimon.countdown

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val themeID = prefs.getInt("theme", 0)
        when(themeID){
            0->setTheme(R.style.AppTheme)
            1->setTheme(R.style.brownTheme)
            2->setTheme(R.style.greenTheme)
            3->setTheme(R.style.redTheme)
            4->setTheme(R.style.yellowTheme)
            5->setTheme(R.style.orgTheme)
            6->setTheme(R.style.blueTheme)
            7->setTheme(R.style.greyTheme)
            8->setTheme(R.style.purpleTheme)
            9->setTheme(R.style.pinkTheme)
            10->setTheme(R.style.indigoTheme)
            11->setTheme(R.style.blackTheme)
            12->setTheme(R.style.silverTheme)
            13->setTheme(R.style.skinTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title=resources.getString(R.string.about)
        setup()
    }
    private fun setup(){
        rateButton.setOnClickListener {
            val intent= Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=practice.rimon.countdown")
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId==android.R.id.home){

            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
