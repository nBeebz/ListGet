package com.example.nav.listget.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nav.listget.Interfaces.MongoInterface;
import com.example.nav.listget.Mongo;
import com.example.nav.listget.R;

import org.json.JSONArray;
import org.json.JSONException;

public class Login extends Activity implements MongoInterface {

//    private AccessObject2 datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        datasource = new AccessObject2();
        setContentView(R.layout.activity_login);
    }



    public void register( View v ){
        startActivity(new Intent(this, RegisterActivity.class) );

    }
    public void login( View v )
    {
        String email = ((EditText)findViewById(R.id.username)).getText().toString();
        Mongo.getMongo( this ).get("users", "_id", email);
    }

    public void processResult( String result )
    {
        JSONArray arr;
        String email;
        Intent myIntent;

        try
        {
            arr = new JSONArray(result);
            if( arr.length() > 0 )
            {
                myIntent = new Intent( this, ListActivity.class );
                email = arr.getJSONObject(0).getString( "_id" );
                myIntent.putExtra( "email", email );
                startActivity( myIntent );
            }
        }
        catch (JSONException e)
        {
            Toast.makeText(this, "Invalid email/password", Toast.LENGTH_LONG).show();
        }
    }

}
