package com.example.nav.listget.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nav.listget.Interfaces.MongoInterface;
import com.example.nav.listget.Mongo;
import com.example.nav.listget.R;
import com.example.nav.listget.parcelable.User;

public class RegisterActivity extends Activity implements MongoInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void register( View v ){
        String password = ((EditText)findViewById(R.id.password)).getText().toString();
        String passwordC = ((EditText)findViewById(R.id.password_confirm)).getText().toString();
        String email = ((EditText)findViewById(R.id.username)).getText().toString();
        if(password.equals(passwordC)){
            User u = new User( email, password );
            Mongo.getMongo(this).post( Mongo.COLL_USERS, u.getJSON() );

        }else{
            Toast.makeText(getBaseContext(),"password and password confirm don't match.",Toast.LENGTH_LONG).show();
        }

    }
    public void clear( View v ){
        ((EditText)findViewById(R.id.password)).setText("");
        ((EditText)findViewById(R.id.password_confirm)).setText("");
        ((EditText)findViewById(R.id.username)).setText("");
    }

    public void processResult(String result)
    {
        startActivity(new Intent(this, Login.class));
    }
}
