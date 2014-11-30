package com.example.nav.listget.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nav.listget.AccessObject;
import com.example.nav.listget.Interfaces.MongoInterface;
import com.example.nav.listget.Mongo;
import com.example.nav.listget.R;

import org.json.JSONArray;
import org.json.JSONException;

public class Login extends Activity implements MongoInterface {

   private AccessObject datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        datasource = new AccessObject(this);
        datasource.open();
        String id = datasource.getId();
        datasource.close();
        if(id != null){
            Intent myIntent = new Intent( this, ListActivity.class );
            myIntent.putExtra( "email", id);
            myIntent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity( myIntent );
            finish();
        }
        setContentView(R.layout.activity_login);
    }

    public void register( View v ){
        startActivity(new Intent(this, RegisterActivity.class) );

    }
    public void login( View v )
    {
        String email = ((EditText)findViewById(R.id.username)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();

        if(email.equals("") || password.equals("")){
            Toast.makeText(getBaseContext(),"Please input both username and password",Toast.LENGTH_LONG).show();

        }else {
            Mongo.getMongo(this).get("users", "_id", email);

        }
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
                String password = ((EditText)findViewById(R.id.password)).getText().toString();
                if(arr.getJSONObject(0).getString(Mongo.KEY_PASSWORD).equals(password))
                {
                    email = arr.getJSONObject(0).getString( Mongo.KEY_ID );
                    myIntent.putExtra( "email", email );
                    datasource.open();
                    datasource.saveId(email);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(myIntent);
                    datasource.close();
                    finish();
                }else{
                    Toast.makeText(getBaseContext(),"email and password don't match",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getBaseContext(),"The email address is not registered",Toast.LENGTH_LONG).show();

            }
        }
        catch (JSONException e)
        {
            Toast.makeText(this, "Invalid email/password", Toast.LENGTH_LONG).show();
        }
    }

}
