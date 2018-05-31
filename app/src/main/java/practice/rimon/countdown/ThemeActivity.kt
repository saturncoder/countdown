package practice.rimon.countdown

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

class ThemeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setup()
    }
    private fun setup(){

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.theme_menu, menu)
        return super.onCreateOptionsMenu(menu)


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == R.id.theme_confirm){
            finish()
            return true
        }
        if (item.itemId==android.R.id.home){

            onBackPressed()//回上一步
            return true//成功處理完選單(consumed)項目時會傳回 true
        }

        return super.onOptionsItemSelected(item)
    }
}
