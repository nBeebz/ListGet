package com.example.nav.listget.Activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nav.listget.Interfaces.MongoInterface;
import com.example.nav.listget.Mongo;
import com.example.nav.listget.R;
import com.example.nav.listget.parcelable.ListObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContactShare extends Activity implements MongoInterface
{
    private static final int CONTACT_PICKER_RESULT = 1001;
    private static EditText toEmail = null;
    ListObject list = null;
    LinearLayout second = null;
    TextView shared = null;
    ArrayList<String> added = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        list = getIntent().getExtras().getParcelable("list");
        setContentView(R.layout.activity_contact_share);
        shared = (TextView)findViewById(R.id.sharedText);
        shared.setVisibility(View.GONE);
        second = (LinearLayout)findViewById(R.id.second);;
        second.setVisibility(View.GONE);
        toEmail = (EditText) findViewById(R.id.invite_email);
    }

    public void doLaunchContactPicker(View view)
    {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case CONTACT_PICKER_RESULT:
                    Cursor cursor = null;
                    String email = "";
                    try
                    {
                        Uri result = data.getData();
                        Log.v("DEBUG_TAG", "Got a contact result: "
                                + result.toString());

                        // get the contact id from the Uri
                        String id = result.getLastPathSegment();

                        // query for everything email
                        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?", new String[] { id },
                                null);

                        int emailIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);

                        // let's just get the first email
                        if (cursor.moveToFirst())
                        {
                            email = cursor.getString(emailIdx);
                            Log.v("DEBUG_TAG", "Got email: " + email);
                        } else
                        {
                            Log.w("DEBUG_TAG", "No results");
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e("DEBUG_TAG", "Failed to get email data", e);
                    }
                    finally
                    {
                        if (cursor != null)
                        {
                            cursor.close();
                        }
                        EditText emailEntry = (EditText) findViewById(R.id.invite_email);
                        emailEntry.setText(email);
                        if (email.length() == 0)
                        {
                            Toast.makeText(this, "No email found for contact.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
            }
        }
        else
        {
            Log.w("DEBUG_TAG", "Warning: activity result not ok");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_contact_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
/*        int id = item.getItemId();

        switch(item.getItemId())
        {
            case R.id.menu_clear:

                toEmail.setText("");
                break;

            case R.id.menu_send:

                String to = toEmail.getText().toString();

                Intent email = new Intent(Intent.ACTION_SEND);

                email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});

                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose"));

                break;
        }*/
        return true;
    }


    /*public void shareButton(View view){
        Button share = (Button) findViewById(R.id.share);
        final String email = toEmail.getText().toString();
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get();
            }
        });
    }*/

    public void shareButton(View view)
    {
        final String email = toEmail.getText().toString();
        if(!email.equals(""))
             Mongo.getMongo(this).get( Mongo.COLL_USERS, Mongo.KEY_ID, email);
    }
    public void sendEmail(View v){

        String to = toEmail.getText().toString();

        Intent email = new Intent(Intent.ACTION_SEND);

        email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});

        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, "Choose"));
        toFinish();

    }

    @Override
    public void processResult(String result) {
        try {
            JSONArray arr = new JSONArray( result );
            JSONObject obj = arr.getJSONObject(0);
            String id = obj.getString(Mongo.KEY_ID);
            if(list.addContributor(id)){
                shared.setText("");
                TextView shared = (TextView)findViewById(R.id.sharedText);
                shared.setText("Shared!!");
                shared.setVisibility(View.VISIBLE);
                added.add(id);
                Mongo.getMongo(this).post( Mongo.COLL_LISTS, list.getJSON() );
            }else{
                shared.setText("");
                shared.setText("This user is already added.");
                shared.setVisibility(View.VISIBLE);
                toEmail.setText("");
            }
            //Mongo.getMongo(this).get( Mongo.COLL_ITEMS, Mongo.KEY_LISTID, list.getId() );
        }catch (Exception e){
            //Toast.makeText( this, "Couldn't find the user", Toast.LENGTH_SHORT).show();
            shared.setVisibility(View.GONE);
            Button share = (Button)findViewById(R.id.share);
            share.setVisibility(View.INVISIBLE);
            second.setVisibility(View.VISIBLE);
        }/*finally {
            Mongo.getMongo(this).post( Mongo.COLL_LISTS, list.getJSON() );
            Mongo.getMongo(this).get( Mongo.COLL_ITEMS, Mongo.KEY_LISTID, list.getId() );

        }*/
    }

    private void toFinish(){
        Intent data = new Intent();
        if(added.size()>0){
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("addedList", added);
            data.putExtras(bundle);
            setResult(RESULT_OK, data);

        }else{
            setResult(RESULT_CANCELED, data);

        }
        finish();
    }

    @Override
    public void onBackPressed() {
        toFinish();
    }
}