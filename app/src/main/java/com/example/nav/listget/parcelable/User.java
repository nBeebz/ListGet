package com.example.nav.listget.parcelable;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.nav.listget.Mongo;

import org.json.JSONObject;

/**
 * Created by Nav on 11/25/2014.
 */
public class User implements Parcelable {
    private String email;
    private String pass;

    public User( String e, String p )
    {
        email = e;
        pass = p;
    }

    public User( Parcel p )
    {
        email = p.readString();
        pass = p.readString();
    }

    public String getEmail(){ return email; }
    public String getPass(){ return pass; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString( email );
        parcel.writeString( pass );

    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public JSONObject getJSON()
    {
        JSONObject obj = new JSONObject();
        try {
            obj.put(Mongo.KEY_ID, email);
            obj.put(Mongo.KEY_PASS, pass);
        }catch (Exception e) {
            Log.d("User", e.getLocalizedMessage());
        }
        return obj;
    }
}
