package practice.rimon.countdown

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.util.*

// 資料功能類別
class ItemDAO(context: Context) {

    companion object {
        // 表格名稱
        val TABLE_NAME = "item"

        // 編號表格欄位名稱，固定不變
        val KEY_ID = "_id"

        // 其它表格欄位名稱
        val ICON_COLUMN = "icon"
        val TITLE_COLUMN = "title"
        val DAYSBETWEEN_COLUMN = "daysbetween"

        // 使用上面宣告的變數建立表格的SQL敘述
        val CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ICON_COLUMN + " INTEGER NOT NULL, " +
                TITLE_COLUMN + " TEXT NOT NULL, " +
                DAYSBETWEEN_COLUMN + " INTEGER NOT NULL)"
    }

    // 資料庫物件 writableDatabase
    private val db: SQLiteDatabase = MyDataBaseHelper.getDatabase(context)

    // 讀取所有記事資料
    val all: ArrayList<Item>
        get() {
            val result = ArrayList<Item>()
            val cursor = db.query(
                    TABLE_NAME, null, null, null, null, null, null, null)

            while (cursor.moveToNext()) {
                result.add(getRecord(cursor))
            }

            cursor.close()
            return result
        }

    // 取得資料數量
    val count: Int
        get() {
            var result = 0
            val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME", null)

            if (cursor.moveToNext()) {
                result = cursor.getInt(0)//獲取第一列的值
            }

            return result
        }

    // 關閉資料庫，一般的應用都不需要修改
    fun close() {
        db.close()
    }

    // 新增參數指定的物件
    fun insert(item: Item): Item {
        // 建立準備新增資料的ContentValues物件
        val cv = ContentValues()

        // 加入ContentValues物件包裝的新增資料
        itemToContentValues(item, cv)

        // 新增一筆資料並取得編號  (return _id)  從1開始
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        val id = db.insert(TABLE_NAME, null, cv)
        println(id)
        // 設定編號
        item.id = id
        // 回傳結果
        return item
    }

    // 修改參數指定的物件
    fun update(item: Item): Boolean {
        // 建立準備修改資料的ContentValues物件
        val cv = ContentValues()

        // 加入ContentValues物件包裝的修改資料
        itemToContentValues(item, cv)

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        val where = KEY_ID + "=" + item.id

        // 執行修改資料並回傳修改的資料數量是否成功(修改的行數>0)
        return db.update(TABLE_NAME, cv, where, null) >0
    }

    private fun itemToContentValues(item : Item, cv : ContentValues) {
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(ICON_COLUMN, item.id)
        cv.put(ICON_COLUMN, item.item_icon)
        cv.put(TITLE_COLUMN, item.item_title)
        cv.put(DAYSBETWEEN_COLUMN, item.daysbetween)
    }

    // 刪除指定參數編號的資料
    fun delete(id: Long): Boolean {
        // 設定條件為編號，格式為「欄位名稱=資料」
        val where = KEY_ID + "=" + id
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where, null) > 0
    }

    // 取得指定編號的資料物件
    operator fun get(id: Long): Item? {
        // 準備回傳結果用的物件
        var item: Item? = null
        // 使用編號為查詢條件
        val where = KEY_ID + "=" + id
        // 執行查詢
        val result = db.query(
                TABLE_NAME, null, where, null, null, null, null, null)

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            item = getRecord(result)
        }

        // 關閉Cursor物件
        result.close()
        // 回傳結果
        return item
    }

    // 把Cursor目前的資料包裝為物件
    fun getRecord(cursor: Cursor): Item {
        // 準備回傳結果用的物件
        val result = Item()

        result.id = cursor.getLong(0)
        result.item_icon = cursor.getInt(1)
        result.item_title = cursor.getString(2)
        result.daysbetween = cursor.getInt(3)

        // 回傳結果
        return result
    }

    // 建立範例資料
    fun createSampleData() {
        val item = Item(0, R.drawable.test, "考試", 32)
        val item2 = Item(0, R.drawable.test, "回家", 112)

        insert(item)
        insert(item2)
    }

}
