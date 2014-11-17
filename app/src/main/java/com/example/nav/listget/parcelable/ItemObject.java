package com.example.nav.listget.parcelable;

import com.example.nav.listget.Mongo;

import org.json.JSONObject;

import java.io.Serializable;

/* Stores values for items */
public class ItemObject implements Serializable {
    private int itemId;
    private String item;
    private boolean inDatabase;
    private int complete = 0;

    public ItemObject(int id, String t, boolean d){
        itemId = id;
        item = t;
        inDatabase = d;

    }

    public ItemObject(int id, String t, boolean d, int checked){
        itemId = id;
        item = t;
        inDatabase = d;
        complete = checked;

    }
    public String toString(){
        return  item;
    }

    public void setItemId(int id){
        itemId = id;
    }

    public void setItem(String t){
        item = t;
    }

    public int getItemId(){
        return itemId;
    }

    public String getItem(){
        return item;
    }

    public void putInDatabase(){
        inDatabase = true;
    }

    public boolean ifInDatabase(){
        return inDatabase;
    }
    public int getChecked(){
        return complete;
    }
    public void setChecked(int d){
        complete = d;
    }

    public static ItemObject parseJSON( JSONObject obj )
    {
        int id = 0;
        String name = null;
        boolean db = true;

        try{
            id = obj.getInt( Mongo.KEY_ID );
            name = obj.getString( Mongo.KEY_NAME );
        }
        catch (Exception e ){ e.printStackTrace(); }

        return new ItemObject( id, name, db );
    }
}
