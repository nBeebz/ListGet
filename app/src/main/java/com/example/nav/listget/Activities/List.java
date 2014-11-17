package com.example.nav.listget.Activities;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.nav.listget.Adapters.ListAdapter2;
import com.example.nav.listget.DBHelper;
import com.example.nav.listget.DragSort.DragSortController;
import com.example.nav.listget.DragSort.DragSortListView;
import com.example.nav.listget.MyDialogFragment;
import com.example.nav.listget.R;
import com.example.nav.listget.parcelable.ListObject;

import java.util.ArrayList;

public class List extends ListActivity {

    //EditCategoryFragment frag;

    ListAdapter2 adapterCat = null;
    private DragSortController mController;

    LinearLayout btn;

    DragSortListView listCat = null;
    private int listsize = 0;

    Intent intent = null;
    Activity act = this;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //if(btn==null){
        btn = (LinearLayout) findViewById(R.id.LinearLayout);
        btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ListObject selectedCat = new ListObject(0, "Add List", 1);
                MyDialogFragment dialog = new MyDialogFragment();
                dialog.setCategory(selectedCat);
                dialog.setOnCloseListener(new onMyClickListener());
                dialog.show(act.getFragmentManager(), "");
            }
        });
        createList();
    }

    public class onMyClickListener implements MyDialogFragment.OnMyClickListener {
        @Override
        public void onClose() {
            setAdapterForCatList();
        }

        //closed dialog by tapping x
        public void onCancelClose() {
        }
    }


    private void setAdapterForCatList() {
        ArrayList<ListObject> objects = new ArrayList<ListObject>();
        listsize = 0;

        //open database
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        //カテゴリlistView
        Cursor c = (Cursor) db.rawQuery("select * from categories order by importance desc;", null);
        boolean isEof = c.moveToFirst();
        int number = getNumber("select count(itemId) from items;", db);
        //objects.add(new ListObject(-3, "AllItems",-1,number));
        while (isEof) {
            number = getNumber("select count(itemId) from items where categoryId =" + c.getInt(0) + ";", db);
            objects.add(new ListObject(c.getInt(0), c.getString(1), number));
            isEof = c.moveToNext();
            listsize++;
        }
        number = getNumber("select count(itemId) from items where not exists (select categoryId from categories where items.categoryId = categories.categoryId) ;", db);
        c.close();
        db.close();

        // set adapter
        listCat = (DragSortListView) getListView();
        adapterCat = new ListAdapter2(act, objects);
        listCat.setAdapter(adapterCat);
    }

    public int getNumber(String st, SQLiteDatabase db) {
        int number = 0;
        Cursor num = (Cursor) db.rawQuery(st, null);
        if (num.moveToFirst()) {
            number = num.getInt(0);
        }
        return number;
    }

    private void createList() {
        listCat = (DragSortListView) getListView();
        mController = buildController(listCat);
        listCat.setFloatViewManager(mController);
        listCat.setOnTouchListener(mController);
        listCat.setDragEnabled(true);
        // set adapter and list
        setAdapterForCatList();
        //listener for list
        listCat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get selected item
                ListView listView = (ListView) parent;
                ListObject selectedCat = (ListObject) listView.getItemAtPosition(position);
               // saveCatInDatabase(selectedCat);
                Intent itemActivity = new Intent(act, ItemActivity.class);
                itemActivity.putExtra("list", selectedCat);
                itemActivity.putExtra("listsize",selectedCat.getNumTask());

                startActivity(itemActivity);
            }
        });
        listCat.setDropListener(onDrop);
        listCat.setRemoveListener(onRemove);
    }

    
    /*drag & drop stuff*/
    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            ListObject item = adapterCat.getItem(from);
            adapterCat.remove(item);
            adapterCat.insert(item, to);
        }
    };

    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
        @Override
        public void remove(int which) {
            adapterCat.remove(adapterCat.getItem(which));
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

    @Override
    public void onPause() {
        super.onPause();
        //save task positions to the database
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        ListView listView = (ListView) getListView();
        int importance = listsize;
        for (int position = 0; position < listsize; position++) {
            ListObject selectedCat = (ListObject) listView.getItemAtPosition(position);
            ContentValues cv = new ContentValues();
            cv.put("importance", importance--);
            db.update("categories", cv, "categoryId = " + selectedCat.getCategoryId(), null);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        createList();
    }
}
