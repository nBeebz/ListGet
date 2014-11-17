package com.example.nav.listget;

/**
 * Created by mao on 2014/09/29.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "items.db", null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create item table
        db.execSQL("create table items(" +
                "itemId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "item text not null, " +
                "categoryId integer default 0,"+
                "memo text," +
                "checked integer default 0," +     // 0 = not checked, 1 = checked
                "importance integer default -1" +  // used to change order
                ");");

        //test
        db.execSQL("insert into items(item, categoryId) values ('item1', 1);");
        db.execSQL("insert into items(item, categoryId)values ('item1', 2);");

        //create category table
        db.execSQL("create table categories(" +
                "categoryId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "category text not null, " +
                // "color integer default 0," +
                "importance integer default -1" +// used to change order
                ");");

        //test
        db.execSQL("insert into categories(category) values ('job');");
        db.execSQL("insert into categories(category)values ('private');");
        db.execSQL("insert into categories(category)values ('shopping');");


        db.execSQL("create table SelectedCategories(" +
                "categoryId INTEGER," +
                "category text not null,"+
                "filter integer default 0," +
                "number integer default 0" +
                ");");

        // filter 0 = allItems, 1=unCheckedItems, 2=checkedItems
        db.execSQL("insert into SelectedCategories(categoryId, category) values (1, 'job');");

    }
}
