package com.example.nav.listget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;

import com.example.nav.listget.parcelable.ItemObject;
import com.example.nav.listget.parcelable.ListObject;

import java.util.ArrayList;

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

    /**
     * save all the lists in list activity
     *
     * @param listsize number of lists
     * @param listView all the lists in list view
     */

    public void saveLists(int listsize, ListView listView) {
        int importance = listsize;
        for (int position = 0; position < listsize; position++) {
            ListObject selectedCat = (ListObject) listView.getItemAtPosition(position);
            ContentValues cv = new ContentValues();
            cv.put("importance", importance--);
            database.update("categories", cv, "categoryId = " + selectedCat.getCategoryId(), null);
        }
    }

    /**
     * get all the lists in database with number of items in a list
     * @return all listObjects
     */
    public ArrayList<ListObject> getLists(){
        ArrayList<ListObject> objects = new ArrayList<ListObject>();
        Cursor c = (Cursor) database.rawQuery("select * from categories order by importance desc;", null);
        boolean isEof = c.moveToFirst();
        while (isEof) {
            int number = getNumber(c.getInt(c.getColumnIndex("categoryId")));
            objects.add(new ListObject(c.getInt(0), c.getString(1), number));
            isEof = c.moveToNext();
        }
        c.close();
        return objects;
    }

    /**
     * getting number of items in a list
     * @param id id of the list
     * @return number of items in a list
     */
    public int getNumber(int id) {
        int number = 0;

        Cursor num = (Cursor) database.rawQuery("select count(itemId) from items where categoryId ="+id+";", null);
        if (num.moveToFirst()) {
            number = num.getInt(0);
        }
        num.close();
        return number;
    }

    public void insertAnItem(String itemName, int listId){
        ContentValues cv = new ContentValues();
        cv.put("Item", itemName);
        cv.put("categoryId", listId);
        database.insert("Items", null, cv);
    }

    /**
     * save the order(importance) of items and if the item is checked or not
     * @param listView all the lists in item activity
     * @param listsize size of the list
     */
    public void saveOrderOfItemList(ListView listView, int listsize){
        for (int position = 0; position < listsize; position++) {
            ItemObject selectedItem = (ItemObject) listView.getItemAtPosition(position);
            ContentValues cv = new ContentValues();
            cv.put("importance", position);
             cv.put("checked", selectedItem.getChecked());
            database.update("Items", cv, "ItemId = " + selectedItem.getItemId(), null);
        }

    }

    /**
     * get the name of the list by list id
     * @param id
     * @return name of the list
     */
    public String getListNameById(int id){
        Cursor c = (Cursor) database.rawQuery("select * from categories where categoryId == " + id + ";", null);
        c.moveToFirst();
        String catName =  c.getString(c.getColumnIndex("category"));
        c.close();
        return catName;
    }

    /**
     * get items in database by list id
     * 1 get checked items order by importance
     * 2 get checked items with importance -1
     * @param listId id of the list
     * @return ItemObject
     */
    public ArrayList<ItemObject> getItems(int listId){
        ArrayList<ItemObject> objects = new ArrayList<ItemObject>();

        Cursor c = (Cursor) database.rawQuery("select * from items where categoryId == "+listId+";", null);
        boolean dataInside = c.moveToFirst();
        //if there are items inside
        if (dataInside) {
            /*
            c = (Cursor) database.rawQuery("select * from items where importance >-1 and checked == 0 order by importance asc;", null);
            addToItemObjectArrayList(c, objects);

            c = (Cursor) database.rawQuery("select * from items where importance < 0 and checked == 0 order by importance asc;", null);
            addToItemObjectArrayList(c, objects);

            c = (Cursor) database.rawQuery("select * from items where importance >-1 and checked != 0 order by importance asc;", null);
            addToItemObjectArrayList(c, objects);

            c = (Cursor) database.rawQuery("select * from items where importance < 0 and checked != 0 order by importance asc;", null);
            addToItemObjectArrayList(c, objects);
            */
            c = (Cursor) database.rawQuery("select * from items where importance >-1 and categoryId == "+listId+" order by importance asc;", null);
            addToItemObjectArrayList(c, objects);

            c = (Cursor) database.rawQuery("select * from items where importance < 0 AND categoryId == "+listId+";", null);
            addToItemObjectArrayList(c, objects);

        }
        c.close();
        return objects;
    }

    /**
     * update the name of the list
     * @param listName the new name of the list
     * @param listId the id of the list
     */
    public void updateListName(String listName, int listId){
        ContentValues cv = new ContentValues();
        cv.put("category", listName );
        database.update("categories", cv, "categoryId = "+listId, null);
        database.close();

    }

    /**
     * delete list by id
     * @param listId id of the list
     */
    public void deleteListById(int listId){
        database.delete("categories", "categoryId = " + listId, null);
    }


    /**
     * put all the item objects in cursor in itemObject array
     * @param c cursor to the itemObjects
     * @param objects itemObject arrayList
     */
    private void addToItemObjectArrayList(Cursor c, ArrayList<ItemObject> objects) {
        Boolean isEof = c.moveToFirst();
        while (isEof) {
            objects.add(new ItemObject(c.getInt(c.getColumnIndex("itemId")), c.getString(c.getColumnIndex("item")), c.getInt(c.getColumnIndex("checked"))));
            isEof = c.moveToNext();
        }
    }

    /**
     * get item by id from database
     * @param itemId id of the item
     * @return itemObject
     */
    public ItemObject getItemById(int itemId){
        ItemObject item = null;
        Cursor c = (Cursor)database.rawQuery("select * from items where itemId="+itemId+";", null);
        if(c.moveToFirst()){
            String itemName = c.getString(c.getColumnIndex("item"));
            String itemMemo = c.getString(c.getColumnIndex("memo"));
            item = new ItemObject(itemId, itemName,itemMemo);
        }
        return item;
    }

    /**
     * update item name and memo in database by id
     * @param item item object to get item name, memo and id
     */
    public void updateItem(ItemObject item){
        ContentValues cv = new ContentValues();
        cv.put("item", item.getItem() );
        cv.put("memo", item.getMemo() );
        database.update("items", cv, "itemId = "+item.getItemId(), null);

    }

    /**
     * delete item by item id
     * @param id id of the item
     */
    public void deleteItemById(int id){
        database.delete("items", "itemId = "+id, null);
    }

}
