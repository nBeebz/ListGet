package com.example.nav.listget.Adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nav.listget.Activities.ListActivity.OwnedListsFragment;
import com.example.nav.listget.Activities.ListActivity;

import com.example.nav.listget.MyDialogFragment;
import com.example.nav.listget.R;
import com.example.nav.listget.parcelable.ListObject;

import java.util.ArrayList;

/**
 * Created by Nav on 11/22/2014.
 */
public class OwnedListAdapter extends ArrayAdapter<ListObject> {

    private ArrayList<ListObject> lists;
    private Activity act = null;
    private OwnedListsFragment frag = null;

    public OwnedListAdapter(Context context, int layoutResourceId, int textViewResourceId, ArrayList<ListObject> list)
    {
        super( context, layoutResourceId, textViewResourceId, list );
        act = (Activity)context;
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
            ImageView icon_share = (ImageView)v.findViewById(R.id.icon_shared);


            // check to see if each individual textview is null.
            // if not, assign some text!
            if (tt != null){
                tt.setText(list.getName());
                if(list.getContributors().size()<1) {
                    icon_share.setVisibility(View.GONE);
                }
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
            dialog.show(act.getFragmentManager(), "");
        }
    }

    /**
     * listener for　fragmentDialog.
     * listens action from fragment dialog and does stuff here
     */
    public class onMyClickListener implements MyDialogFragment.OnMyClickListener {
        @Override
        public void onDelete(ListObject list) {
            Fragment frag = ((ListActivity)act).findFragmentByPosition(0);
            if(frag instanceof OwnedListsFragment){
                ((OwnedListsFragment) frag).deleteListFromAdapter(list);
            }else{
                Log.d("OwnedListAdapter","couldn't get owned list fragment");
            }

        }


        public void onSave(ListObject list) {
        }
    }
}



