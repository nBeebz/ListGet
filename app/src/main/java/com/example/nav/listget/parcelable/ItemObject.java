package com.example.nav.listget.parcelable;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.nav.listget.Mongo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nav on 11/22/2014.
 */
public class ItemObject implements Parcelable{
    private String id;
    private String listID;
    private String name;
    private String memo;
    private String completed;

    public ItemObject( String i,String listI, String n, String m, String c )
    {
        id = i;
        listID = listI;
        name = n;
        memo = m;
        if(c ==null)
            completed = "";
        else
            completed = c;
    }
    public ItemObject( String i,String n, String m, String c )
    {
        id = i;
        listID = "";
        name = n;
        memo = m;
        if(c == null )
            completed = "";
        else
            completed = c;
    }

    public ItemObject( Parcel p )
    {
        id = p.readString();
        name = p.readString();
        memo = p.readString();
        completed = p.readString();
    }

    public static ArrayList<ItemObject> getItems( JSONArray arr )
    {
        ArrayList<ItemObject> list = new ArrayList<ItemObject>( arr.length() );

        try {
            for (int i = 0; i < arr.length(); ++i) {
                list.add( i, getItem( arr.getJSONObject(i) ) );
            }
        }
        catch (JSONException e)
        {
            Log.d("getItems", e.getLocalizedMessage());
        }
        return list;
    }

    public static ItemObject getItem( JSONObject obj )
    {
        String i = null;
        String n = null;
        String m = "";
        String c = "";

        try{
            //i = obj.getJSONObject( "_id" ).getString( "$oid" );
            i = obj.getString( "_id" );
            n = obj.getString( "name" );
            if( obj.has("memo") ) m = obj.getString( "memo" );
            if( obj.has("completed") ) c = obj.getString( "completed" );
        }
        catch (JSONException e)
        {
            Log.d( "getItem", e.getLocalizedMessage() );
        }
        return new ItemObject( i, n, m, c );
    }

    public String getName(){ return name; }
    public String getMemo(){ return memo; }
    public String getCompleter(){ return completed; }
    public String getId(){ return id; }
    public void setName(String n ){name = n;}
    public void setMemo(String n){memo =n;}
    public void setCompleter(String comp){completed = comp;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString( id );
        parcel.writeString( name );
        parcel.writeString( memo );
        parcel.writeString( completed );
    }

    public static final Parcelable.Creator<ItemObject> CREATOR = new Parcelable.Creator<ItemObject>() {
        public ItemObject createFromParcel(Parcel in) {
            return new ItemObject(in);
        }

        public ItemObject[] newArray(int size) {
            return new ItemObject[size];
        }
    };

    public JSONObject getJSON()
    {
        JSONObject obj = null;
        try{
            obj = new JSONObject();
            obj.put(Mongo.KEY_ID, id );
            obj.put(Mongo.KEY_LISTID, listID);

            obj.put(Mongo.KEY_NAME, name  );
            obj.put(Mongo.KEY_MEMO, memo);
            obj.put(Mongo.KEY_COMPLETED,completed);
        }catch (Exception e){ Log.d("List Object", e.getLocalizedMessage()); }

        return obj;
    }
}
