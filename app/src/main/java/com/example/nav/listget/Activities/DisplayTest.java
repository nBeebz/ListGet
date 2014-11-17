package com.example.nav.listget.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.nav.listget.Interfaces.MongoInterface;
import com.example.nav.listget.Mongo;
import com.example.nav.listget.R;

import org.json.JSONArray;

public class DisplayTest extends Activity implements MongoInterface {

    private Mongo m;
    private JSONArray arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_test);
<<<<<<< HEAD
        m = new Mongo( this );
        //m.get( "users", 1 );
=======
>>>>>>> 13d277e5653108b5983d1dd2ba0fada164a21c07
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_test, menu);
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


    public void getMongo( View v )
    {
        String text = "";
        int num = Integer.parseInt(((EditText) findViewById(R.id.num)).getText().toString());
        try {
            text = arr.get(--num).toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        ((TextView)findViewById(R.id.text)).setText( text );
    }


<<<<<<< HEAD
    public void processResult( String result )
    {
        try
        {
            arr = new JSONArray(result);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
=======
    public void processResult( String result ){}
>>>>>>> 13d277e5653108b5983d1dd2ba0fada164a21c07
}
