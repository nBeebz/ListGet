package com.example.nav.listget.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nav.listget.AccessObject;
import com.example.nav.listget.Activities.PeopleListActivity;
import com.example.nav.listget.R;
import com.example.nav.listget.parcelable.User;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {
    private LayoutInflater layoutInflater_;
    private Activity act = null;
    String id = null;


    public UserAdapter(Context context, List<User> objects) {
        super(context, R.layout.adapter_item, objects);
        act = (Activity)context;
        layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AccessObject datasource = new AccessObject(getContext());
        datasource.open();
        id = datasource.getId();
        datasource.close();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        User user = (User) getItem(position);
        if (row == null) {
            row = layoutInflater_.inflate(R.layout.adapter_item, parent, false);
            holder = new ViewHolder();
            holder.delete_button = (LinearLayout) row.findViewById(R.id.delete_button);
            holder.username = (TextView) row.findViewById(R.id.username);
            holder.icon_star.setOnClickListener(new onDeleteClicked(user));

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        holder.username.setText(user.getEmail());
        //holder.textPos.setText("" + position);

        return (row);
    }


    public class onDeleteClicked implements View.OnClickListener {

        User selectedUser;

        public onDeleteClicked(User selectedUser) {
            this.selectedUser = selectedUser;

        }

        @Override
        public void onClick(View view) {

            AlertDialog.Builder alert = new AlertDialog.Builder(act);

            alert.setTitle("Warning");
            alert.setMessage("Are you sure to delete this user from this list?");

            alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    ((PeopleListActivity)act).deleteUserFromAdapter(selectedUser);
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            alert.show();
        }
    }




    public static class ViewHolder {
        LinearLayout delete_button;
        ImageView icon_star;
        TextView username;
    }


}