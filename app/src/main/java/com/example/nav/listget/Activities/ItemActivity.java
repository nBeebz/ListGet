package com.example.nav.listget.Activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.example.nav.listget.AccessObject;
import com.example.nav.listget.Adapters.ItemAdapter;
import com.example.nav.listget.Interfaces.MongoInterface;
import com.example.nav.listget.R;
import com.example.nav.listget.parcelable.ItemObject;
import com.example.nav.listget.parcelable.ListObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ItemActivity extends ListActivity implements MongoInterface {

    static ItemAdapter adapter;
   // private DragSortController mController;
    private AccessObject datasource;


    int listsize = 0;
    ListObject selectedCat = null;

    List<ItemObject> objects;
    EditText inputItem = null;
    TextView filterText;

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
                intent.putExtra("list", selectedCat);
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

        datasource = new AccessObject(this);
        datasource.open();

        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //set edittext field
        inputItem = (EditText) findViewById(R.id.addItem);
        inputItem.setOnFocusChangeListener(new MyOnFocusChangeListener());
        inputItem.setOnEditorActionListener(new MyOnEditorActionListener());

        filterText = (TextView) findViewById(R.id.textView2);

        Intent intent = getIntent();
        selectedCat = ((ListObject) intent.getExtras().getSerializable("list"));
        listsize = intent.getExtras().getInt("listsize");


        if (savedInstanceState == null) {
            resetList();
            ListView ls = getListView();
            /*DragSortListView mDslv = (DragSortListView) getListView();
            mController = buildController(mDslv);
            mDslv.setFloatViewManager(mController);
            mDslv.setOnTouchListener(mController);
            mDslv.setDragEnabled(true);
            mDslv.getCheckedItemPosition();
            mDslv.setDropListener(onDrop);
            mDslv.setRemoveListener(onRemove);*/
            ls.setOnItemClickListener(new listViewListener());
        } else {
            resetList();
        }
    }

    public class listViewListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listView = (ListView) parent;
            ItemObject selectedItem = (ItemObject) listView.getItemAtPosition(position);
            //listener.onMove(selectedTask, selectedCat);
            Intent intent = new Intent(getBaseContext(), EditItemActivity.class);
            intent.putExtra("item", selectedItem);
            intent.putExtra("list", selectedCat);
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
                inputItem.setText("");
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
                if (!(inputItem.getText().toString().equals(""))) {
                    datasource.insertAnItem(inputItem.getText().toString(),selectedCat.getCategoryId());
                    saveOrder();
                    resetList();
                }
                inputItem.setText("");
                return true;
            }
            return false;
        }
    }


    /**
     * return the category name with number of items
     *
     * @return
     */
    public String getFilterString() {
        String str = selectedCat.getCategory();
        return str + " (" + listsize + ") ";
    }

    /**
     * reset, recreate listView and filterText
     */
    private void resetList() {
        objects = new ArrayList<ItemObject>();
        if(selectedCat != null) {
            objects = datasource.getItems(selectedCat.getCategoryId());
            listsize = objects.size();
            String newName = datasource.getListNameById(selectedCat.getCategoryId());
            selectedCat.setCategory(newName);
        }
        adapter = new ItemAdapter(this, objects);
        setListAdapter(adapter);
        if (listsize<1)
            setEmptyText("NoItems");
        filterText.setText(getFilterString());
    }



    /**
     * When there is no task, show the text
     */
    public void setEmptyText(CharSequence text) {
        TextView tv = (TextView) getListView().getEmptyView();
        tv.setText(text);
    }


    public void saveOrder() {
        ListView listView = (ListView) getListView();
        datasource.saveOrderOfItemList(listView, listsize);
    }
    /**
     * save the order
     */
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
    }

    /*
    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            DragSortListView list = (DragSortListView) getListView();
            ItemObject item = adapter.getItem(from);
            adapter.remove(item);
            adapter.insert(item, to);
            list.moveCheckState(from, to);
        }
    };

    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
        @Override
        public void remove(int which) {
            DragSortListView list = (DragSortListView) getListView();
            ItemObject item = adapter.getItem(which);
            adapter.remove(item);
            list.removeCheckState(which);
        }
    };

    public DragSortController getController() {
        return mController;
    }

    private DragSortController buildController(DragSortListView dslv) {
        DragSortController controller = new DragSortController(dslv);
        controller.setDragHandleId(R.id.drag_handle);
        //controller.setClickRemoveId(R.id.click_remove);
        controller.setBackgroundColor(Color.GRAY);
        controller.setRemoveEnabled(false);
        controller.setSortEnabled(true);
        controller.setDragInitMode(DragSortController.ON_DOWN);
        controller.setRemoveMode(DragSortController.CLICK_REMOVE);
        controller.setDragInitMode(DragSortController.ON_LONG_PRESS);
        return controller;
    }
	/*drag & drop stuff done*/

    public void processResult( String result )
    {
        JSONArray arr;
        JSONObject obj;
        ArrayList<ItemObject> items = new ArrayList<ItemObject>();

        try {
            arr = new JSONArray(result);
            for( int i=0; i<arr.length(); ++i )
            {
                items.add( ItemObject.parseJSON( arr.getJSONObject(i) ));
            }
        }
        catch (Exception e){ e.printStackTrace();
        }

        //DO SOMETHING WITH THE ITEMS

    }
}