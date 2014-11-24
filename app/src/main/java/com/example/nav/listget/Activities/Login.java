package com.example.nav.listget.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.nav.listget.AccessObject2;
import com.example.nav.listget.R;

import org.json.JSONException;

public class Login extends Activity {

    private AccessObject2 datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        datasource = new AccessObject2();
        setContentView(R.layout.activity_login);
    }



    public void register( View v ){
        startActivity(new Intent(this, RegisterActivity.class) );

    }
    public void login( View v ) throws JSONException {
        // if the password matchs
        String username = ((EditText)findViewById(R.id.username)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();


        int userId = datasource.login(username, password);
        Intent intent = new Intent(this, List.class);
        intent.putExtra("userId", userId);
        startActivity(intent );
    }

}
