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
public class ListObject implements Parcelable {
    private String id;
    private String name;
    private String owner;
    private ArrayList<String> contributors;

    public ListObject( String i, String n, String o, ArrayList<String> c )
    {
        id = i;
        name = n;
        owner = o;
        contributors = c;
    }

    public ListObject( String n, String o )
    {
        id = "";
        name = n;
        owner = o;
        contributors = new ArrayList<String>();
    }

    public ListObject( Parcel p )
    {
        id = p.readString();
        name = p.readString();
        owner = p.readString();
        contributors = p.createStringArrayList();
    }

    public static ArrayList<ListObject> getLists( String str )
    {
        JSONArray arr;
        ArrayList<ListObject> lists = null;
        try{
            arr = new JSONArray( str );
            lists = new ArrayList<ListObject>( arr.length() );
            for( int i=0; i<arr.length(); ++i )
            {
                lists.add( i, getList( arr.getJSONObject(i) ));
            }
        }
        catch ( JSONException e )
        {
            Log.d( "getLists", e.getLocalizedMessage() );
        }
        return lists;
    }
    public static ListObject getList( JSONObject obj )
    {
        String d = null;
        String o = null;
        String n = null;
        ArrayList<String> c = new ArrayList<String>();
        JSONArray arr = null;
        try{
            d = obj.getString("_id");
            n = obj.getString("name");
            o = obj.getString("owner");
            arr = obj.getJSONArray("contributors");
            for( int i=0; i<arr.length(); ++i )
            {
                c.add(arr.getString(i));
            }
        }
        catch (JSONException e)
        {
            Log.d("getList", e.getLocalizedMessage());
        }
        return new ListObject( d, n, o, c );
    }

    public void setName(String name){this.name = name;}
    public void setId(String id){this.id = id;}
    public String getId(){ return id; }
    public String getName(){ return name; }
    public String getOwner(){ return owner; }
    public ArrayList<String> getContributors(){ return contributors; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString( id );
        parcel.writeString( name );
        parcel.writeString( owner );
        parcel.writeStringList( contributors );
    }

    public static final Parcelable.Creator<ListObject> CREATOR = new Parcelable.Creator<ListObject>() {
        public ListObject createFromParcel(Parcel in) {
            return new ListObject(in);
        }

        public ListObject[] newArray(int size) {
            return new ListObject[size];
        }
    };

    public JSONObject getJSON()
    {
        JSONObject obj = null;
        try{
            obj = new JSONObject();
            obj.put(Mongo.KEY_ID, id );
            obj.put(Mongo.KEY_OWNER, owner );
            obj.put(Mongo.KEY_NAME, name  );
            obj.put(Mongo.KEY_CONTRIBUTORS, new JSONArray( contributors ) );
        }catch (Exception e){ Log.d("List Object", e.getLocalizedMessage()); }

        return obj;
    }

    public void addContributor( String email )
    {
        if( !contributors.contains( email ) )
            contributors.add( email );
    }
    public void removeContributor( String email )
    {
        contributors.remove( email );
    }

}
