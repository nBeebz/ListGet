package com.example.nav.listget.Activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nav.listget.Adapters.UserAdapter;
import com.example.nav.listget.Interfaces.MongoInterface;
import com.example.nav.listget.Mongo;
import com.example.nav.listget.R;
import com.example.nav.listget.parcelable.ListObject;
import com.example.nav.listget.parcelable.User;

import java.util.ArrayList;

public class PeopleListActivity extends ListActivity implements MongoInterface {

    ListObject selectedList = null;
    String userid = null;
    ArrayList<User> users = new ArrayList<User>();
    ArrayList<String> usersWithString = new ArrayList<String>();

    UserAdapter userAdapter = null;
    ArrayAdapter<String> adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_list);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (savedInstanceState == null) {
            ListView ls = getListView();
        }
        Intent intent = getIntent();
        selectedList = intent.getParcelableExtra("list");
        userid = intent.getStringExtra("userid");
        if(userid.equals(selectedList.getOwner())){
            setContributersToUsers(selectedList.getContributors());
            userAdapter = new UserAdapter(this,users);
            setListAdapter( userAdapter );
        }else{
            ArrayList<String> contributers = selectedList.getContributors();
            for(int i = 0; i<contributers.size();i++) {
                if(contributers.get(i).equals(userid))
                   contributers.remove(i);
            }
            setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,contributers));
        }

        TextView tx = (TextView)findViewById(R.id.ownerName);
        tx.setText(selectedList.getOwner());
        Button bt = (Button)findViewById(R.id.leave);
        if(selectedList.getOwner().equals(userid)){
            bt.setVisibility(View.GONE);
        }

    }

    public void onLeaveClick(View v){
        selectedList.removeContributor( userid );
        Mongo.getMongo( this ).post(Mongo.COLL_LISTS, selectedList.getJSON());
        Intent intent = new Intent(this, ListActivity.class);

    }


    public void deleteUserFromAdapter(User user){
        selectedList.removeContributor( user.getEmail() );
        Mongo.getMongo( this ).post(Mongo.COLL_LISTS, selectedList.getJSON());
        if(userAdapter!=null) {
            userAdapter.remove(user);
            userAdapter.notifyDataSetChanged();
        }

    }

    public void setContributersToUsers(ArrayList<String> contributers){
        for(int i = 0; i<contributers.size();i++) {
            if(!contributers.get(i).equals(userid))
                users.add(new User(contributers.get(i),""));
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.people_list, menu);
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


    @Override
    public void processResult(String result) {

        /*
        JSONArray arr;
        users = new ArrayList<User>();
        try {
            if(result != null) {
                arr = new JSONArray(result);
                if (arr.length() >= 1) {
                    users = User.getUsers(arr);
                    adapter = new UserAdapter(this, users);
                    setListAdapter(adapter);
                }
            }
        }
        catch (Exception e){ e.printStackTrace();
        }*/

        //DO SOMETHING WITH THE ITEMS


    }
}
