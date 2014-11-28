package com.example.nav.listget.Activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.example.nav.listget.Adapters.ItemAdapter;
import com.example.nav.listget.Interfaces.MongoInterface;
import com.example.nav.listget.Mongo;
import com.example.nav.listget.R;
import com.example.nav.listget.parcelable.ItemObject;
import com.example.nav.listget.parcelable.ListObject;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;



public class ItemActivity extends ListActivity implements MongoInterface {

    static ItemAdapter adapter;
   // private DragSortController mController;
   // private AccessObject datasource;


    ListObject selectedList = null;

    List<ItemObject> items;
    EditText inputEditText= null;
    TextView filterText;
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
            case R.id.share:
                Toast.makeText(getBaseContext(), "Share", Toast.LENGTH_LONG).show();
                return true;

            case R.id.from_contacts:
                Toast.makeText(getBaseContext(), "From Contacts", Toast.LENGTH_LONG).show();
                return true;

            case R.id.action_settings:
                Intent intent = new Intent(getBaseContext(), EditListActivity.class);
                intent.putExtra("list", selectedList);
                startActivity(intent);

                return true;

            case R.id.item2:
                Toast.makeText(getBaseContext(), "item2", Toast.LENGTH_LONG).show();
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
        if(selectedList!=null){
            filterText.setText(selectedList.getName());
            adapter = new ItemAdapter(this, items);
            Mongo.getMongo(this).get(Mongo.COLL_ITEMS, Mongo.KEY_LISTID, selectedList.getId());
        }
    }

    public class listViewListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listView = (ListView) parent;
            ItemObject selectedItem = (ItemObject) listView.getItemAtPosition(position);
            Intent intent = new Intent(getBaseContext(), EditItemActivity.class);
            intent.putExtra("item", selectedItem);
            intent.putExtra("list", selectedList);
            startActivity(intent);
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
                    adapter.add(inputItem);
                    setListAdapter(adapter);
//                    datasource.insertAnItem(inputEditText.getText().toString(),selectedCat.getCategoryId());
                    //saveOrder();
                    //resetList();
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


    /*
    public void saveOrder() {
        ListView listView = (ListView) getListView();
        //datasource.saveOrderOfItemList(listView, listsize);
    }
    /**
     * save the order
     */
    /*
    @Override
    public void onPause() {
        saveOrder();
        datasource.close();
        super.onPause();
    }
    @Override
    protected void onResume()
    {
        datasource.open();
        resetList();
        super.onResume();
    }*/


    public void processResult( String result )
    {
        JSONArray arr;
        items = new ArrayList<ItemObject>();
        try {
            arr = new JSONArray(result);
            Log.d("result",result);
            if(arr.length()<1){
                setEmptyText("No Items");
            }else {
//                   items.add( ItemObject.parseJSON( arr.getJSONObject(i) ));
                items = ItemObject.getItems(arr );
                adapter = new ItemAdapter(this,items);
                setListAdapter(adapter);
            }
        }
        catch (Exception e){ e.printStackTrace();
        }

        //DO SOMETHING WITH THE ITEMS

    }
}