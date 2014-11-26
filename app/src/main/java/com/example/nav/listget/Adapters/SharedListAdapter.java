package com.example.nav.listget.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.nav.listget.R;
import com.example.nav.listget.parcelable.ListObject;

import java.util.ArrayList;

/**
 * Created by Nav on 11/22/2014.
 */
public class SharedListAdapter extends ArrayAdapter<ListObject> {

    private ArrayList<ListObject> lists;

    public SharedListAdapter(Context context, int layoutResourceId, int textViewResourceId, ArrayList<ListObject> list)
    {
        super( context, layoutResourceId, textViewResourceId, list );
        lists = list;
    }

    public View getView( int position, View convertView, ViewGroup parent )
    {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.shared_list_line, null);
        }

        ListObject l = lists.get(position);

        if (l != null) {

            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.

            TextView tt = (TextView) v.findViewById(R.id.sListName);

            // check to see if each individual textview is null.
            // if not, assign some text!
            if (tt != null){
                tt.setText(l.getName());
            }

        }

        // the view must be returned to our activity
        return v;
    }
}
