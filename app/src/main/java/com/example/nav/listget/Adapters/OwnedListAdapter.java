package com.example.nav.listget.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nav.listget.MyDialogFragment;
import com.example.nav.listget.R;
import com.example.nav.listget.parcelable.ListObject;

import java.util.ArrayList;

/**
 * Created by Nav on 11/22/2014.
 */
public class OwnedListAdapter extends ArrayAdapter<ListObject> {

    private ArrayList<ListObject> lists;
    private Activity c = null;

    public OwnedListAdapter(Context context, int layoutResourceId, int textViewResourceId, ArrayList<ListObject> list)
    {
        super( context, layoutResourceId, textViewResourceId, list );
        c = (Activity)context;
        lists = list;
    }

    public View getView( int position, View convertView, ViewGroup parent )
    {
        View v = convertView;

        ListObject list = lists.get(position);


        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.owned_list_line, null);
            LinearLayout editButton = (LinearLayout)v.findViewById(R.id.edit_button);
            editButton.setOnClickListener(new onEditClicked(list));
        }


        if (list != null) {

            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.

            TextView tt = (TextView) v.findViewById(R.id.oListName);

            // check to see if each individual textview is null.
            // if not, assign some text!
            if (tt != null){
                tt.setText(list.getName());
            }

        }

        // the view must be returned to our activity
        return v;
    }

    public class onEditClicked implements View.OnClickListener {

        ListObject selectedList;

        public onEditClicked(ListObject selectedList) {
            this.selectedList = selectedList;

        }

        @Override
        public void onClick(View view) {
            MyDialogFragment dialog = new MyDialogFragment();
            dialog.setCategory(selectedList);
            dialog.setOnCloseListener(new onMyClickListener());
            dialog.show(c.getFragmentManager(), "");
        }
    }

    /**
     * listener for　fragmentDialog.
     * listens action from fragment dialog and does stuff here
     */
    public class onMyClickListener implements MyDialogFragment.OnMyClickListener {
        @Override
        public void onClose() {
            //setAdapterForCatList();
        }

        //closed dialog by tapping x
        public void onCancelClose() {
        }
    }
}



