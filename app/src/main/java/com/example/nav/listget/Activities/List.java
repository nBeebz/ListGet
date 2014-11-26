package com.example.nav.listget.Activities;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.nav.listget.AccessObject;
import com.example.nav.listget.Adapters.ListAdapter2;
import com.example.nav.listget.MyDialogFragment;
import com.example.nav.listget.R;
import com.example.nav.listget.parcelable.ListObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class List extends ListActivity {

    //EditCategoryFragment frag;

    ListAdapter2 adapterCat = null;
    //private DragSortController mController;
    private AccessObject datasource;


    LinearLayout btn;

    ListView listCat = null;
    private int listsize = 0;

    Intent intent = null;
    Activity act = this;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        datasource = new AccessObject(this);
        datasource.open();

        //if(btn==null){
        btn = (LinearLayout) findViewById(R.id.LinearLayout);
        btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
//                ListObject selectedCat = new ListObject(0, "Add List", 1);
                MyDialogFragment dialog = new MyDialogFragment();
//                dialog.setCategory(selectedCat);
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
        ArrayList<ListObject> objects = datasource.getLists();
        listsize = objects.size();
        // set adapter
        listCat =  getListView();
        adapterCat = new ListAdapter2(act, objects);
        listCat.setAdapter(adapterCat);
    }


    private void createList() {
        listCat = (ListView) getListView();
        //mController = buildController(listCat);
        //listCat.setFloatViewManager(mController);
        //listCat.setOnTouchListener(mController);
        //listCat.setDragEnabled(true);
        // set adapter and list
        setAdapterForCatList();
        //listener for list

        //listCat.setDropListener(onDrop);
        //listCat.setRemoveListener(onRemove);
        listCat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get selected item
                ListView listView = (ListView) parent;
                ListObject selectedCat = (ListObject) listView.getItemAtPosition(position);
                // saveCatInDatabase(selectedCat);
                Intent itemActivity = new Intent(act, ItemActivity.class);
                itemActivity.putExtra("list", selectedCat);
//                itemActivity.putExtra("listsize",selectedCat.getNumTask());

                startActivity(itemActivity);
            }
        });
    }

    
    /*drag & drop stuff
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
        //save task positions to the database
        ListView listView = (ListView) getListView();
        int importance = listsize;
        //datasource.saveLists(importance, listView);
        datasource.close();
        super.onPause();

    }
    @Override
    protected void onResume()
    {
        datasource.open();
        createList();
        super.onResume();
    }


    public void processResult( String result )
    {
        JSONArray arr;
        JSONObject obj;
        ArrayList<ListObject> lists = new ArrayList<ListObject>();

        try {
            arr = new JSONArray(result);
            for( int i=0; i<arr.length(); ++i )
            {
//                lists.add( ListObject.parseJSON( arr.getJSONObject(i) ));
            }
        }
        catch (Exception e){ e.printStackTrace(); }

        //DO SOMETHING WITH THE LISTS

    }
}
