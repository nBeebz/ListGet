package com.example.nav.listget.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.nav.listget.Interfaces.MongoInterface;
import com.example.nav.listget.R;

public class Login extends Activity implements MongoInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }



    public void register( View v ){}
    public void login( View v ){ startActivity( new Intent( this, List.class ) );}

    public void processResult( String result ){

    }
}
