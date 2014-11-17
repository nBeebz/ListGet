package com.example.nav.listget.parcelable;

import com.example.nav.listget.Mongo;

import org.json.JSONObject;

import java.io.Serializable;

/* Storing items for a list */
public class ListObject implements Serializable {
    private int categoryId;
    private String category;
    private int numTask = 0;


    public ListObject(int id, String l){
        categoryId = id;
        category = l;
    }

    public ListObject(int id, String l,  int n){
        categoryId = id;
        category = l;
        numTask = n;
    }

    public int getNumTask(){
        return numTask;
    }
    public void setNumTask(int n){
        numTask = n;
    }

    public String toString(){
        return  category;
    }

    public void setCategoryId(int id){
        categoryId = id;
    }

    public void setCategory(String t){
        category = t;
    }

    public int getCategoryId(){
        return categoryId;
    }

    public String getCategory(){
        return category;
    }

    public static ListObject parseJSON( JSONObject obj )
    {
        int id = 0;
        int items = 0;
        String name = null;

        try {
            id = obj.getInt(Mongo.KEY_ID);
            name = obj.getString( Mongo.KEY_NAME );

        }
        catch ( Exception e ){ e.printStackTrace(); }

        return new ListObject( id, name );
    }

}
