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
        cv.put(SQLHelper.COL_ID, id);
        database.insert(SQLHelper.TABLE_USER, null, cv);
    }

    public void deleteId(){
        database.delete(SQLHelper.TABLE_USER,null,null);
    }

    public String getId(){
        Cursor c = (Cursor) database.rawQuery("select * from "+SQLHelper.TABLE_USER+" ;", null);
        String id = null;
        if(c.moveToFirst())
             id =  c.getString(c.getColumnIndex(SQLHelper.COL_ID));
        c.close();
        return id;
    }


}