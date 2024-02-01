package com.example.kotlingallerypro.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.kotlingallerypro.modelclass.FavModel



@RequiresApi(Build.VERSION_CODES.P)
class DbHelper(context: Context?, factory: SQLiteDatabase.CursorFactory? ) : SQLiteOpenHelper(context, "FAVOURITE.DB", factory, 1) {
    private val DB_NAME = "FAVOURITE.DB"
    private val DB_VERSION = 1
    private val DB_TABLE = "FavouriteImage"
    private val COL_ID = "Id"
    private val COL_FAV_STATUS = "FavStatus"

    override fun onCreate(db: SQLiteDatabase?) {
        if (db != null) {
            val CREATE_DB = "CREATE TABLE $DB_TABLE ($COL_ID TEXT PRIMARY KEY, $COL_FAV_STATUS TEXT)"
            db.execSQL(CREATE_DB)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (db != null) {
            db.execSQL("DROP TABLE IF EXISTS " + DB_NAME)
        }
        onCreate(db)
    }

    /*-----------------------------------------------------------*/
    fun setFav(imgId: String?) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_ID, imgId)
        db.insert(DB_TABLE, null, cv)
    }
    fun getStatuss(imgId: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("select * from $DB_TABLE where $COL_ID=?", arrayOf(imgId), null)
        return if (cursor != null) {
            if (cursor.moveToFirst()) {
                true
            } else {
                false
            }
        } else {
            false
        }
    }
    fun removeFav(path: String): Boolean {
        val db = this.writableDatabase
        return db.delete(DB_TABLE, "$COL_ID=?", arrayOf(path)) > 0
    }
    fun getFavList(): java.util.ArrayList<String?>? {
        val db = this.readableDatabase
        var favModelList: java.util.ArrayList<String?>? = java.util.ArrayList()
        val cursor = db.rawQuery("select * from  $DB_TABLE", null)
        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst()
            do {
                favModelList!!.add(cursor.getString(0))
            } while (cursor.moveToNext())
        } else {
            favModelList = null
        }
        return favModelList
    }
//    fun getFavList(): ArrayList<String?>? {
//        val db = this.readableDatabase
//        var favModelList: ArrayList<String?>? = ArrayList()
//        val cursor = db.rawQuery("select * from $DB_TABLE", null)
//        if (cursor != null && cursor.moveToFirst()) {
//            cursor.moveToFirst()
//            do {
//                favModelList!!.add(cursor.getString(0))
//            } while (cursor.moveToNext())
//        } else {
//            favModelList = null
//        }
//        return favModelList
//    }

    /* public void getStatus(String imageId, String url, String thumb) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_FAV_STATUS, "0");

        long isInserted = db.insert(DB_TABLE, null, cv);

        if (isInserted != -1){
            Toast.makeText(context, "Inserted", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Not Inserted", Toast.LENGTH_SHORT).show();
        }
    }*/
    fun getFavourite(favStatus: String): List<FavModel?>? {
        val db = this.readableDatabase
        var favModelList: MutableList<FavModel?>? = java.util.ArrayList<FavModel?>()
        val cursor =
            db.rawQuery("select * from $DB_TABLE where $COL_FAV_STATUS=$favStatus", null, null)
        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst()
            do {
                val favModel = FavModel()
                favModel.id
                favModel.favStatus
                favModelList!!.add(favModel)
            } while (cursor.moveToNext())
        } else {
            favModelList = null
        }
        return favModelList
    }

}
