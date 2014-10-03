package com.example.nav.listget;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ItemActivity extends ListActivity {

    static ItemAdapter adapter;
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
        getMenuInflater().inflate(R.menu.list, menu);
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


    /**
     * Set EditTextField and Category textview field, set Selectedcategory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item2);

        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
        resetList();
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
                objects.add(new ItemObject(c.getInt(c.getColumnIndex("itemId")), c.getString(c.getColumnIndex("item")), true, color.getInt(color.getColumnIndex("color")), c.getInt(c.getColumnIndex("checked"))));
            } else {
                objects.add(new ItemObject(c.getInt(c.getColumnIndex("itemId")), c.getString(c.getColumnIndex("item")), true, 0, c.getInt(c.getColumnIndex("checked"))));
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

    public void saveOrder() {
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        ListView listView = (ListView) getListView();
        for (int position = 0; position < listsize; position++) {
            ItemObject selectedItem = (ItemObject) listView.getItemAtPosition(position);
            ContentValues cv = new ContentValues();
            cv.put("importance", position);
            if (selectedItem.getChecked() == 1) {
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

}
