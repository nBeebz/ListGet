package com.example.nav.listget.Activities;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nav.listget.Adapters.ItemAdapter;
import com.example.nav.listget.Interfaces.MongoInterface;
import com.example.nav.listget.Mongo;
import com.example.nav.listget.R;
import com.example.nav.listget.ShareDialog;
import com.example.nav.listget.parcelable.ItemObject;
import com.example.nav.listget.parcelable.ListObject;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class ItemActivity extends ListActivity implements MongoInterface{

    static ItemAdapter adapter = null;

    ListObject selectedList = null;
    SwipeRefreshLayout swipeContainer = null;

    List<ItemObject> items = null;
    EditText inputEditText= null;
    TextView filterText;
    String userid;
    MongoInterface m = null;

    public ItemActivity() {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {


            case R.id.from_contacts:
                Intent contact_select = new Intent(getBaseContext(), ContactShare.class);
                startActivity(contact_select);
                return true;

            case R.id.sharing_with:
                Intent userList = new Intent(getBaseContext(), PeopleListActivity.class);
                userList.putExtra("list",selectedList);
                userList.putExtra("id",userid);
                startActivity(userList);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Set EditTextField and Category textview field, set Selectedcategory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        m = this;
        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipe_container);

        //set edittext field
        inputEditText = (EditText) findViewById(R.id.addItem);
        inputEditText.setOnFocusChangeListener(new MyOnFocusChangeListener());
        inputEditText.setOnEditorActionListener(new MyOnEditorActionListener());
        filterText = (TextView) findViewById(R.id.textView2);

        if (savedInstanceState == null) {
            ListView ls = getListView();
            ls.setOnItemClickListener(new listViewListener());
        }

        Intent intent = getIntent();
        selectedList = ((ListObject) intent.getExtras().getParcelable("list"));
        userid = intent.getExtras().getString("userid");
        if(selectedList!=null){
            filterText.setText(selectedList.getName());
            adapter = new ItemAdapter(this, items);
            Mongo.getMongo(this).get(Mongo.COLL_ITEMS, Mongo.KEY_LISTID, selectedList.getId());
        }


        swipeContainer.setRefreshing( true );
        swipeContainer.setColorSchemeResources(R.color.bg_color, R.color.grey, R.color.txt_color, R.color.btn_blue);
        swipeContainer.setOnRefreshListener(new ItemRefreshListener(this, selectedList.getId(), swipeContainer));
        swipeContainer.setRefreshing( true );
    }

    private static class ItemRefreshListener implements SwipeRefreshLayout.OnRefreshListener
    {
        private MongoInterface activity;
        SwipeRefreshLayout layout;
        private String value;

        ItemRefreshListener( MongoInterface a, String id, SwipeRefreshLayout l )
        {
            activity = a;
            value = id;
            layout = l;
        }
        @Override
        public void onRefresh() {
            Mongo.getMongo( activity ).get(Mongo.COLL_ITEMS, Mongo.KEY_LISTID, value);
            layout.setRefreshing( true );
        }
    }

    public class listViewListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listView = (ListView) parent;
            ItemObject selectedItem = (ItemObject) listView.getItemAtPosition(position);
            Intent intent = new Intent(getBaseContext(), EditItemActivity.class);
            intent.putExtra("caller", getIntent().getComponent().getClassName());
            intent.putExtra("item", selectedItem);
            intent.putExtra("position", position);
            intent.putExtra("userid",userid);
            int requestCode = 1;
            startActivityForResult(intent, requestCode);

        }
    }

    /**
     * When you shift focus from edit text, close soft keyboard
     */
    public class MyOnFocusChangeListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus == false) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                inputEditText.setText("");
            }
        }
    }

    /**
     * when edit item activity is closed, this method will be called and change list
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            Bundle bundle = data.getExtras();
            int position = bundle.getInt("position");
            ItemObject item = bundle.getParcelable("item");

            switch (requestCode) {
                case 1:
                    if (resultCode == RESULT_OK) {
                        adapter.getItem(position).setName(item.getName());
                        adapter.getItem(position).setMemo(item.getMemo());
                        adapter.getItem(position).setCompleter(item.getCompleter());
                        setListAdapter(adapter);

                    } else if (resultCode == RESULT_CANCELED) {
                        //if(items!=null && items.size()>0) {
                            adapter.remove(adapter.getItem(position));
                            setListAdapter(adapter);
                        //}
                    }
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * When you type item names and tap checked, save the item.
     */
    public class MyOnEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            // TODO Auto-generated method stub
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                if (!(inputEditText.getText().toString().equals(""))) {

                    String id =""+ System.currentTimeMillis();
                    ItemObject inputItem = new ItemObject(id,selectedList.getId(),inputEditText.getText().toString() ,"","");
                    Mongo.getMongo(m).post(Mongo.COLL_ITEMS, inputItem.getJSON());
                    if(items.size() <1){
                        items.add(inputItem);
                        adapter = new ItemAdapter((Activity)m,items);
                    }else {
                        adapter.add(inputItem);
                    }

                    setListAdapter(adapter);
                }
                inputEditText.setText("");
                return true;
            }
            return false;
        }
    }



    /**
     * When there is no task, show the text
     */
    public void setEmptyText(CharSequence text) {
        TextView tv = (TextView) getListView().getEmptyView();
        tv.setText(text);
    }

    @Override
    public void onPause()
    {
        storeChecked();
        super.onPause();
    }


    public void storeChecked(){
        if(adapter != null){
            if(items.size()>0) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    ItemObject item = adapter.getItem(i);
                    if (item.getCompleter().equals(userid)) {
                        Log.d("user", "itemActivity storeChecked");
                        Mongo.getMongo(this).put(Mongo.COLL_ITEMS, Mongo.KEY_ID, item.getId(), Mongo.KEY_COMPLETED, userid);
                    } else if (item.getCompleter().equals("")) {
                        Log.d("user", "itemActivity storeChecked");
                        Mongo.getMongo(this).put(Mongo.COLL_ITEMS, Mongo.KEY_ID, item.getId(), Mongo.KEY_COMPLETED, item.getCompleter());
                    }

                }
            }
        }
    }

    /*
    @Override
    protected void onResume()
    {
        if(selectedList!=null){
            filterText.setText(selectedList.getName());
            adapter = new ItemAdapter(this, items);
            Mongo.getMongo(this).get(Mongo.COLL_ITEMS, Mongo.KEY_LISTID, selectedList.getId());

        }
        super.onResume();
    }

   */





    public void processResult( String result )
    {
        JSONArray arr;
        items = new ArrayList<ItemObject>();
        try {
            if(result != null) {
                arr = new JSONArray(result);
                if (arr.length() < 1) {
                    setEmptyText("No Items");
                } else {
//                   items.add( ItemObject.parseJSON( arr.getJSONObject(i) ));
                    items = ItemObject.getItems(arr);
                    adapter = new ItemAdapter(this, items);
                    setListAdapter(adapter);
                }
            }
        }
        catch (Exception e){ e.printStackTrace(); }
        finally {
            swipeContainer.setRefreshing(false);
        }
        //DO SOMETHING WITH THE ITEMS

    }

    public void shareList( View v )
    {
        ShareDialog dialog = new ShareDialog();
        dialog.setList( selectedList );
        dialog.show( getFragmentManager(), "");
    }

}