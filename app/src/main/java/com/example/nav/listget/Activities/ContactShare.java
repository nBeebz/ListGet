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
import android.widget.EditText;
import android.widget.Toast;

import com.example.nav.listget.Interfaces.MongoInterface;
import com.example.nav.listget.R;

public class ContactShare extends Activity implements MongoInterface
{
    private static final int CONTACT_PICKER_RESULT = 1001;
    private static EditText toEmail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_share);

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
        getMenuInflater().inflate(R.menu.menu_contact_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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
        }
        return true;
    }

    @Override
    public void processResult(String result) {

    }
}