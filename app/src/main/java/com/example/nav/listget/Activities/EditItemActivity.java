package com.example.nav.listget.Activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.nav.listget.DBHelper;
import com.example.nav.listget.R;
import com.example.nav.listget.parcelable.ItemObject;
import com.example.nav.listget.parcelable.ListObject;

public class EditItemActivity extends Activity {


    EditText itemName;
    EditText textMemo;
    int selectedItem;
    int selectedCat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        MyOnFocusChangeListener focusChangeListener = new MyOnFocusChangeListener();
        //itemName
        itemName = (EditText)findViewById(R.id.item_value);
        itemName.setOnFocusChangeListener(focusChangeListener);
        //memo
        textMemo = (EditText)findViewById(R.id.memo);
        textMemo.setOnFocusChangeListener(focusChangeListener);
        setButtons();
        Intent intent = getIntent();
        selectedItem = ((ItemObject) intent.getExtras().getSerializable("item")).getItemId();
        selectedCat = ((ListObject) intent.getExtras().getSerializable("list")).getCategoryId();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_list, menu);
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
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        setStoredData();
    }

    /**
     * close soft keyboad
     */
    public class MyOnFocusChangeListener implements View.OnFocusChangeListener{
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus == false) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
    /**
     *	set listener to buttons
     */
    public void setButtons(){
        ClickListener listener = new ClickListener();
        Button btn3 = (Button)findViewById(R.id.save);
        btn3.setOnClickListener(listener);
        Button btn4 = (Button)findViewById(R.id.delete);
        btn4.setOnClickListener(listener);
    }


    /**
     * put data to edit text
     */
    private void setStoredData(){
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = (Cursor)db.rawQuery("select * from items where itemId="+selectedItem+";", null);
        if(c.moveToFirst()){
            itemName.setText(c.getString(c.getColumnIndex("item")));
            textMemo.setText(c.getString(c.getColumnIndex("memo")));
        }
        db.close();
    }



    /**
     * listener class
     */

    class ClickListener implements OnClickListener {
        public void onClick(View v){
            //open database
            DBHelper helper = new DBHelper(getBaseContext());
            SQLiteDatabase db = helper.getReadableDatabase();
            ContentValues cv = getDataFromFields();
            switch(v.getId())
            {
                case R.id.save:
                    save(db, cv);
                    break;

                case R.id.delete:
                    db.delete("items", "itemId = "+selectedItem, null);
                    break;
                default:
                    break;
            }
            db.close();
            finish();
        }
    }

    /**
     * put everything to store in cv
     * @return　cv things in imput field
     */
    public ContentValues getDataFromFields(){
        ContentValues cv = new ContentValues();
        String inputTask = (String)itemName.getText().toString();
        String inputMemo = textMemo.getText().toString();

        if(!(inputTask.equals(""))){
            cv.put("item", inputTask );
            cv.put("memo", inputMemo );
        }
        return cv;
    }

    /**
     * save data in cv to database
     * @param db database
     * @param cv data
     */
    private void save(SQLiteDatabase db,ContentValues cv){
        db.update("items", cv, "itemId = "+selectedItem, null);
    }
}
