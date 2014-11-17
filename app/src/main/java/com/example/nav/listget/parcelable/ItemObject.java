package com.example.nav.listget.parcelable;

import com.example.nav.listget.Mongo;

import org.json.JSONObject;

import java.io.Serializable;

/* Stores values for items */
public class ItemObject implements Serializable {
    private int itemId;
    private String item;
    private int complete = 0;
    private String memo;

    public ItemObject(int id, String t){
        itemId = id;
        item = t;

    }

    public ItemObject(int id, String t, String m){
        itemId = id;
        item = t;
        memo = m;

    }

    public ItemObject(int id, String t, int checked){
        itemId = id;
        item = t;
        complete = checked;

    }

    public String getMemo(){
        return memo;
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

        try{
            id = obj.getInt( Mongo.KEY_ID );
            name = obj.getString( Mongo.KEY_NAME );
        }
        catch (Exception e ){ e.printStackTrace(); }

        return new ItemObject( id, name );
    }
}
