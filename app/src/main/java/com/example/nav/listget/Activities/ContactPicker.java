package com.example.nav.listget.Activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nav.listget.R;

/**
 * Created by andres on 2014-11-17.
 */
public class ContactPicker extends Activity {

    private static final int CONTACT_PICKER_RESULT = 1001;

    public void doLaunchContactPicker(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    Cursor cursor = null;
                    String email = "";
                    try {
                        Uri result = data.getData();
                        Log.v("DEBUG_TAG", "Got a contact result: "
                                + result.toString());

                        // get the contact id from the Uri
                        String id = result.getLastPathSegment();

                        // query for everything email
                        cursor = getContentResolver().query(Email.CONTENT_URI,
                                null, Email.CONTACT_ID + "=?", new String[] { id },
                                null);

                        int emailIdx = cursor.getColumnIndex(Email.DATA);

                        // let's just get the first email
                        if (cursor.moveToFirst()) {
                            email = cursor.getString(emailIdx);
                            Log.v("DEBUG_TAG", "Got email: " + email);
                        } else {
                            Log.w("DEBUG_TAG", "No results");
                        }
                    } catch (Exception e) {
                        Log.e("DEBUG_TAG", "Failed to get email data", e);
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                        EditText emailEntry = (EditText) findViewById(R.id.invite_email);
                        emailEntry.setText(email);
                        if (email.length() == 0) {
                            Toast.makeText(this, "No email found for contact.",
                                    Toast.LENGTH_LONG).show();
                        }

                    }

                    break;
            }

        } else {
            Log.w("DEBUG_TAG", "Warning: activity result not ok");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_pickers);  }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_pickers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}



