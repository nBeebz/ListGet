package com.example.nav.listget;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nav.listget.Interfaces.MongoInterface;
import com.example.nav.listget.parcelable.ListObject;

import org.json.JSONArray;
import org.json.JSONObject;


public class ShareDialog extends DialogFragment implements MongoInterface {

    ListObject list = null;
    Dialog d;


    public void setList( ListObject l )
    {
        list = l;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog( getActivity() );
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_share_dialog);
        shareButton(dialog);
        d = dialog;
        return dialog;
    }

    private void shareButton(final Dialog dialog){
        Button share = (Button) dialog.findViewById(R.id.share);
        final String email = ((TextView)dialog.findViewById(R.id.email)).getText().toString();
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get();
            }
        });
    }

    public void get()
    {
        String email = ((TextView)d.findViewById(R.id.email)).getText().toString();
        Mongo.getMongo(this).get( Mongo.COLL_USERS, Mongo.KEY_ID, email);
    }

    @Override
    public void processResult(String result) {
        try {
            JSONArray arr = new JSONArray( result );
            JSONObject obj = arr.getJSONObject(0);
            list.addContributor(obj.getString(Mongo.KEY_ID));
        }catch (Exception e){
            Toast.makeText( getActivity(), "An error has occurred", Toast.LENGTH_SHORT).show();
        }finally {
            dismiss();
            Mongo.getMongo((MongoInterface)getActivity()).post( Mongo.COLL_LISTS, list.getJSON() );
            Mongo.getMongo((MongoInterface)getActivity()).get( Mongo.COLL_ITEMS, Mongo.KEY_LISTID, list.getId() );
        }
    }

}
