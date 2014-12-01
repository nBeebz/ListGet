package com.example.nav.listget.Activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
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
    ArrayList<String> deleted = new ArrayList<String>();


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
        if(selectedList.getOwner().equals(userid)){
            tx.setText("You are the owner");
        }else {
            tx.setText(selectedList.getOwner());
        }
        Button bt = (Button)findViewById(R.id.leave);
        if(selectedList.getOwner().equals(userid)){
            bt.setVisibility(View.GONE);
        }

    }

    public void onLeaveClick(View v){
        selectedList.removeContributor( userid );
        Mongo.getMongo( this ).post(Mongo.COLL_LISTS, selectedList.getJSON());
        Intent intent = new Intent(this, com.example.nav.listget.Activities.ListActivity.class);
        intent.putExtra("email",userid);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


    public void deleteUserFromAdapter(User user, int position){
        selectedList.removeContributor( user.getEmail() );
        Mongo.getMongo( this ).post(Mongo.COLL_LISTS, selectedList.getJSON());
        if(userAdapter!=null) {
            userAdapter.remove(userAdapter.getItem(position));
            setListAdapter(userAdapter);
            deleted.add(user.getEmail());
        }

    }

    public void setContributersToUsers(ArrayList<String> contributers){
        for(int i = 0; i<contributers.size();i++) {
            if(!contributers.get(i).equals(userid))
                users.add(new User(contributers.get(i),""));
        }
    }


    @Override
    public void processResult(String result) {


    }

    private void toFinish(){
        Intent data = new Intent();
        if(deleted.size()>0){
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("deletedList", deleted);
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
