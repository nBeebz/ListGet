package com.example.nav.listget;

/**
 * Created by mao on 2014/09/29.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {


    public static final String TABLE_USER= "users";
    public static final String COL_ID= "_id";

    public static final String CREATE_TABLE =
            "create table "+TABLE_USER+"(" +
            COL_ID+" text PRIMARY KEY " +
            ");";

    public DBHelper(Context context) {
        super(context, "items.db", null, 1);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("table","created");

        db.execSQL( CREATE_TABLE );

    }
}
