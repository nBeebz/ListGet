package com.example.nav.listget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by mao on 2014/11/17.
 */
public class AccessObject {
    private final DBHelper dbHelper;
    private SQLiteDatabase database;


    public AccessObject(Context context)
    {
        dbHelper = new DBHelper(context);
    }

    public void open()throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void saveId(String id){
        deleteId();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COL_ID, id);
        database.insert(DBHelper.TABLE_USER, null, cv);
    }

    public void deleteId(){
        database.delete(DBHelper.TABLE_USER,null,null);
    }

    public String getId(){
        String id = null;
        Cursor c = (Cursor) database.rawQuery("select * from " + DBHelper.TABLE_USER + " ;", null);
        if (c.moveToFirst())
            id = c.getString(c.getColumnIndex(DBHelper.COL_ID));
        c.close();
        return id;
    }


}