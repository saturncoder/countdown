package practice.rimon.countdown

import android.app.Activity
import android.content.res.ColorStateList
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import kotlinx.android.synthetic.main.activity_theme.*


class ThemeActivity : AppCompatActivity(), View.OnClickListener {

    var themeID=0

    override fun onCreate(savedInstanceState: Bundle?) {

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        themeID = prefs.getInt("theme", 0)
        Log.e("讀出主題號碼","$themeID")
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
        setContentView(R.layout.activity_theme)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setup()

    }
    private fun setup(){
        defaultColor.setOnClickListener(this)
        brownTheme.setOnClickListener(this)
        greenTheme.setOnClickListener(this)
        redTheme.setOnClickListener(this)
        yellowTheme.setOnClickListener(this)
        orgTheme.setOnClickListener(this)
        blueTheme.setOnClickListener(this)
        greyTheme.setOnClickListener(this)
        purpleTheme.setOnClickListener(this)
        pinkTheme.setOnClickListener(this)
        indigoTheme.setOnClickListener(this)
        blackTheme.setOnClickListener(this)
        silverTheme.setOnClickListener(this)
        skinTheme.setOnClickListener(this)
        checkDefaultTheme()
    }
    override fun onClick(view: View?) {
        //清除全部按鍵打勾鍵
        clearAllotherCheckedItem()
        if(view is ImageButton){
            view.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.check_circle_48dp))
        }
        when (view!!.id){
            R.id.defaultColor->{changeTheme(R.color.colorPrimary,R.color.colorPrimaryDark,R.color.colorAccent,0)}
            R.id.brownTheme->{changeTheme(R.color.brownPrimary,R.color.browmPrimaryDark,R.color.brownAccent,1)}
            R.id.greenTheme->{changeTheme(R.color.greenPrimary,R.color.greenPrimaryDark,R.color.greenAccent,2)}
            R.id.redTheme->{changeTheme(R.color.redPrimary,R.color.redPrimaryDark,R.color.redAccent,3)}
            R.id.yellowTheme->{changeTheme(R.color.yellowPrimary,R.color.yellowPrimaryDark,R.color.yellowAccent,4)}
            R.id.orgTheme->{changeTheme(R.color.orgPrimary,R.color.orgPrimaryDark,R.color.orgAccent,5)}
            R.id.blueTheme->{changeTheme(R.color.bluePrimary,R.color.bluePrimaryDark,R.color.blueAccent,6)}
            R.id.greyTheme->{changeTheme(R.color.greyPrimary,R.color.greyPrimaryDark,R.color.greyAccent,7)}
            R.id.purpleTheme->{changeTheme(R.color.purplePrimary,R.color.purplePrimaryDark,R.color.purpleAccent,8)}
            R.id.pinkTheme->{changeTheme(R.color.pinkPrimary,R.color.pinkPrimaryDark,R.color.pinkAccent,9)}
            R.id.indigoTheme->{changeTheme(R.color.indigoPrimary,R.color.indigoPrimaryDark,R.color.indigoAccent,10)}
            R.id.blackTheme->{changeTheme(R.color.blackPrimary,R.color.blackPrimaryDark,R.color.blackAccent,11)}
            R.id.silverTheme->{changeTheme(R.color.silverPrimary,R.color.silverPrimaryDark,R.color.silverAccent,12)}
            R.id.skinTheme->{changeTheme(R.color.skinPrimary,R.color.skinPrimaryDark,R.color.skinAccent,13)}

        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.theme_menu, menu)
        return super.onCreateOptionsMenu(menu)


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == R.id.theme_confirm){
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = prefs.edit()
            editor.putInt("theme",themeID)
            editor.apply()

            setResult(Activity.RESULT_OK, intent)
            finish()
            return true
        }
        if (item.itemId==android.R.id.home){

            onBackPressed()//回上一步
            return true//成功處理完選單(consumed)項目時會傳回 true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun changeTheme(primaryColor:Int,primaryDark:Int,accent:Int,theme:Int){
        supportActionBar!!.setBackgroundDrawable(ContextCompat.getDrawable(this,primaryColor))
        val window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.statusBarColor = ContextCompat.getColor(this,primaryDark )
        FAB_theme.backgroundTintList= ColorStateList.valueOf(ContextCompat.getColor(this,accent))
        themeID=theme
    }

    private fun clearAllotherCheckedItem(){
        val childcount=scroll_linearlayout.childCount
        for(i in 0 until childcount){
            val childview=scroll_linearlayout.getChildAt(i)
            if(childview is ImageButton){
                childview.setImageResource(android.R.color.transparent)
            }
        }
    }

    private fun checkDefaultTheme(){
        println(themeID)
        when(themeID){
            0->this.onClick(defaultColor)
            1->this.onClick(brownTheme)
            2->this.onClick(greenTheme)
            3->this.onClick(redTheme)
            4->{
                scrollview.post( { scrollview.scrollTo(yellowTheme.left,0) })
                this.onClick(yellowTheme)
                }
            5->{
                scrollview.post( { scrollview.scrollTo(orgTheme.left,0) })
                this.onClick(orgTheme)
            }
            6->{
                scrollview.post( { scrollview.scrollTo(blueTheme.left,0) })
                this.onClick(blueTheme)
            }

            7->{
                scrollview.post( { scrollview.scrollTo(greyTheme.left,0) })
                this.onClick(greyTheme)
            }
            8->{
                scrollview.post( { scrollview.scrollTo(purpleTheme.left,0) })
                this.onClick(purpleTheme)
            }
            9->{
                scrollview.post( { scrollview.scrollTo(pinkTheme.left,0) })
                this.onClick(pinkTheme)
            }
            10->{
                scrollview.post( { scrollview.scrollTo(indigoTheme.left,0) })
                this.onClick(indigoTheme)
            }
            11->{
                scrollview.post( { scrollview.scrollTo(blackTheme.left,0) })
                this.onClick(blackTheme)
            }
            12->{
                scrollview.post( { scrollview.scrollTo(silverTheme.left,0) })
                this.onClick(silverTheme)
            }
            13->{
                scrollview.post( { scrollview.scrollTo(skinTheme.left,0) })
                this.onClick(skinTheme)
            }
        }
    }
}
