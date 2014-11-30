package com.example.nav.listget.Activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.nav.listget.Adapters.UserAdapter;
import com.example.nav.listget.Interfaces.MongoInterface;
import com.example.nav.listget.Mongo;
import com.example.nav.listget.R;
import com.example.nav.listget.parcelable.ListObject;
import com.example.nav.listget.parcelable.User;

import java.util.ArrayList;

public class PeopleListActivity extends ListActivity implements MongoInterface {

    MongoInterface m = null;
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
        m = this;
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (savedInstanceState == null) {
            ListView ls = getListView();
        }

        Intent intent = getIntent();
        selectedList = ((ListObject) intent.getExtras().getParcelable("list"));
        userid = intent.getExtras().getString("userid");
        if(userid.equals(selectedList.getOwner())){
            setContributersToUsers(selectedList.getContributors());
            userAdapter = new UserAdapter(this,users);
            setListAdapter(userAdapter);
        }else{
            adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,selectedList.getContributors());
            setListAdapter(adapter);

        }
    }

    public void deleteUserFromAdapter(User user){
        Mongo.getMongo(this).delete(Mongo.COLL_USERS, user.getEmail());
        if(userAdapter!=null) {
            userAdapter.remove(user);
            setListAdapter(adapter);
        }

    }

    public void setContributersToUsers(ArrayList<String> contributers){
        for(int i = 0; i<contributers.size();i++) {
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
