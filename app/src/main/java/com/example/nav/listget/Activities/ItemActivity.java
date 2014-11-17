package com.example.nav.listget.Activities;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nav.listget.Adapters.ItemAdapter;

import com.example.nav.listget.DBHelper;
import com.example.nav.listget.DragSort.DragSortController;
import com.example.nav.listget.DragSort.DragSortListView;
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
    private DragSortController mController;
    Bundle bundle = new Bundle();

    Activity act = this;

    int listsize = 0;
    ListObject selectedCat = null;

    // filter 0 = allItems, 1=unCheckedItems, 2=checkedItems
    int filter = -3;

    int number; //number of items
    List<ItemObject> objects;
    EditText inputItem = null;
    TextView filterText;

    public ItemActivity() {
    }

    private OnMoveEditListener listener = null;

    public interface OnMoveEditListener {
        public void onMove(ItemObject selectedItem, ListObject selectedCat);
    }

    public void setOnMoveListener(OnMoveEditListener listener) {
        this.listener = listener;
    }

    private String[] filterTitles = {
            "All",
            "unChecked",
            "Checked"
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.share:
                return true;

            case R.id.from_contacts:
                Intent contact_select = new Intent(getBaseContext(), ContactPicker.class);
                contact_select.putExtra("item", selectedCat);
                startActivity(contact_select);
                return true;

            case R.id.edit:
                Intent edit_list = new Intent(getBaseContext(), EditListActivity.class);
                edit_list.putExtra("list", selectedCat);
                startActivity(edit_list);

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

        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ImageView setting = (ImageView)findViewById(R.id.icon_set);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), EditListActivity.class);
                intent.putExtra("list", selectedCat);
                startActivity(intent);
            }
        });

        //set edittext field
        inputItem = (EditText) findViewById(R.id.addItem);
        inputItem.setOnFocusChangeListener(new MyOnFocusChangeListener());
        inputItem.setOnEditorActionListener(new MyOnEditorActionListener());

        filterText = (TextView) findViewById(R.id.textView2);

        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = (Cursor) db.rawQuery("select * from SelectedCategories;", null);
        if (c.moveToFirst()) {
            selectedCat = new ListObject(c.getInt(0), c.getString(1));
            filter = c.getInt(2);
            number = c.getInt(3);
        } else {
            selectedCat = new ListObject(-2, filterTitles[2]);
            filter = -2;
        }
        c.close();
        db.close();

        if (savedInstanceState == null) {
            resetList();
            DragSortListView mDslv = (DragSortListView) getListView();
            mController = buildController(mDslv);
            mDslv.setFloatViewManager(mController);
            mDslv.setOnTouchListener(mController);
            mDslv.setDragEnabled(true);
            mDslv.setOnItemClickListener(new listViewListener());
            mDslv.getCheckedItemPosition();
            mDslv.setDropListener(onDrop);
            mDslv.setRemoveListener(onRemove);
        } else {
            resetList();
        }
    }

    public class listViewListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DragSortListView listView = (DragSortListView) parent;
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
                    DBHelper helper = new DBHelper(act);
                    SQLiteDatabase db = helper.getReadableDatabase();

                    updateItem(db);
                    updateNumItem(db);

                    saveOrder();
                    resetList();

                    db.close();
                }
                inputItem.setText("");
                return true;
            }
            return false;
        }

        private void updateItem(SQLiteDatabase db) {
            ContentValues cv = new ContentValues();
            cv.put("Item", inputItem.getText().toString());
            cv.put("categoryId", selectedCat.getCategoryId());
            if (filter == 2)
                cv.put("checked", 1);
            db.insert("Items", null, cv);

        }

        private void updateNumItem(SQLiteDatabase db) {
            ContentValues cv = new ContentValues();
            number++;
            cv.put("number", number);
            db.update("SelectedCategories", cv, "categoryId = " + selectedCat.getCategoryId(), null);

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
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        objects = new ArrayList<ItemObject>();
        listsize = 0;
        boolean dataInside = false;

        //カテゴリに合わせたquery用のstringを取得
        if (filter == 0) {//show unchecked items when user choose all task
            String sql = getQueryString(1);
            boolean notChecked = addToObjectsWithString(sql, db);
            sql = getQueryString(2);
            boolean checked = addToObjectsWithString(sql, db);
            if (notChecked || checked)
                dataInside = true;
        } else {
            String sql = getQueryString(filter);
            dataInside = addToObjectsWithString(sql, db);
        }
        db.close();
        adapter = new ItemAdapter(this, objects);
        setListAdapter(adapter);
        if (!dataInside)
            setEmptyText("NoItems");
        filterText.setText(getFilterString());
    }


    private boolean addToObjectsWithString(String sql, SQLiteDatabase db) {
        //category
        Cursor c = (Cursor) db.rawQuery("select * from items where importance >-100 " + sql + ";", null);
        boolean dataInside = c.moveToFirst();
        //if there are items inside
        if (dataInside) {

            // get items with importance
            c = (Cursor) db.rawQuery("select * from items where importance >-1 " + sql + " order by importance asc;", null);
            addToObjects(c, db);

            //get items without importance
            c = (Cursor) db.rawQuery("select * from items where importance <0 " + sql + ";", null);
            addToObjects(c, db);
        }
        c.close();
        return dataInside;
    }


    /**
     * Return cuery string depends on category and filter
     *
     * @param filter1
     * @return sql for query
     */
    public String getQueryString(int filter1) {
        String sql = "";
        if (selectedCat != null) {
            if (selectedCat.getCategoryId() > -1) {
                if (selectedCat.getCategory().equals("undefined")) {
                    sql = " and not exists (select categoryId from categories where items.categoryId = categories.categoryId) ";
                } else {
                    sql = " and categoryId == " + selectedCat.getCategoryId() + " ";
                }
            }
            // all task = 0, unChecked=1, checked=2
            if (filter1 == 0) {
            } else if (filter1 == 1) {
                sql += " and checked != 1 ";
            } else if (filter1 == 2) {
                sql += " and checked == 1 ";
            }
        }
        return sql;
    }

    /**
     * When there is no task, show the text
     */
    public void setEmptyText(CharSequence text) {
        TextView tv = (TextView) getListView().getEmptyView();
        tv.setText(text);
    }

    /**
     * put item object to item object list
     *
     * @param c  cursor which is already separated by category
     * @param db
     */
    private void addToObjects(Cursor c, SQLiteDatabase db) {
        Boolean isEof = c.moveToFirst();
        Cursor color;
        while (isEof) {
            color = (Cursor) db.rawQuery("select * from categories where categoryId=" + c.getInt(2) + ";", null);
            if (color.moveToFirst()) {
                objects.add(new ItemObject(c.getInt(c.getColumnIndex("itemId")), c.getString(c.getColumnIndex("item")), true, c.getInt(c.getColumnIndex("checked"))));
            } else {
                objects.add(new ItemObject(c.getInt(c.getColumnIndex("itemId")), c.getString(c.getColumnIndex("item")), true, c.getInt(c.getColumnIndex("checked"))));
            }
            listsize++;
            isEof = c.moveToNext();
        }
    }


    /**
     * save the order
     */
    @Override
    public void onPause() {
        super.onPause();
        //save item positions to the database
        saveOrder();
    }

    @Override
    public void onStart(){
        super.onStart();
        resetList();
    }

    public void saveOrder() {
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        ListView listView = (ListView) getListView();
        for (int position = 0; position < listsize; position++) {
            ItemObject selectedItem = (ItemObject) listView.getItemAtPosition(position);
            ContentValues cv = new ContentValues();
            cv.put("importance", position);
            if (selectedItem.getChecked() != 0) {
                cv.put("checked", 1);
            } else {
                cv.put("checked", 0);
            }
            db.update("Items", cv, "ItemId = " + selectedItem.getItemId(), null);
        }
        ContentValues cv = new ContentValues();
        cv.put("filter", filter);
        db.update("SelectedCategories", cv, "categoryId = " + selectedCat.getCategoryId(), null);
    }

    /*drag & drop stuff*/
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
        catch (Exception e){ e.printStackTrace(); }

        //DO SOMETHING WITH THE ITEMS

    }

}
