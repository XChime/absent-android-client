package ml.dvnlabs.absenku.util.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import java.sql.SQLException

class UsersHelper(context : Context) : SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION){
    companion object{
        private const val DATABASE_VERSION: Int = 1
        private const val DATABASE_NAME : String = "AbsenKu"
        const val TABLE_NAME = "users"
        const val COLUMN_NIK = "NIK"
        const val COLUMN_DEVICE = "DeviceID"
        const val COLUMN_TOKEN = "Token"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsersTable= "CREATE TABLE ${TABLE_NAME}(${COLUMN_NIK} TEXT PRIMARY KEY,${COLUMN_DEVICE} TEXT,${COLUMN_TOKEN} TEXT)"
        db!!.execSQL(createUsersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun createUsers(nik: String,deviceID: String,token: String){
        val db : SQLiteDatabase = this.writableDatabase
        try {
            val values = ContentValues()
            values.put(COLUMN_NIK,nik)
            values.put(COLUMN_DEVICE,deviceID)
            values.put(COLUMN_TOKEN,token)
            db.insert(TABLE_NAME,null,values)
            db.close()

        }catch (e : SQLiteException){
            e.printStackTrace()
        }
    }

    fun isUsersAvail():Boolean{
        val db = this.readableDatabase
        try {
            val countQuery = "SELECT * FROM $TABLE_NAME"
            val cursor = db.rawQuery(countQuery,null)
            val count = cursor.count
            cursor.close()
            if(count != 0){
                return true
            }
            db.close()
            return false
        }catch (e : SQLiteException){
            e.printStackTrace()
        }
        return false
    }

    fun readUser():HashMap<String,String>?{
        val values = HashMap<String,String>()
        val db = this.readableDatabase
        try {
            val getUser = "SELECT * FROM $TABLE_NAME LIMIT 0,1"
            val cursor = db.rawQuery(getUser,null)
            if (cursor.moveToFirst()){
                val nik = cursor.getString(cursor.getColumnIndex(COLUMN_NIK))
                val device = cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE))
                val token = cursor.getString(cursor.getColumnIndex(COLUMN_TOKEN))
                values["nik"] = nik
                values["device"] = device
                values["token"] = token
                cursor.close()
            }
            db.close()
            return values
        }catch (e : SQLException){
            e.printStackTrace()
        }
        return null
    }

}

data class Users(var nik : String,var deviceID : String,var token : String)