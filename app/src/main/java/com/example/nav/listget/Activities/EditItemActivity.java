package com.example.nav.listget.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nav.listget.AccessObject;
import com.example.nav.listget.R;
import com.example.nav.listget.parcelable.ItemObject;
import com.example.nav.listget.parcelable.ListObject;

public class EditItemActivity extends Activity {


    EditText itemName;
    EditText textMemo;
    int selectedItem;
    int selectedCat;
    private AccessObject datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        datasource = new AccessObject(this);
        datasource.open();

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
        ItemObject item =datasource.getItemById(selectedItem);
        itemName.setText(item.getItem());
        textMemo.setText(item.getMemo());
    }

    /**
     * listener class
     */
    class ClickListener implements OnClickListener {
        public void onClick(View v){
            switch(v.getId())
            {
                case R.id.save:
                    String nameItem = itemName.getText().toString();
                    String memoItem = textMemo.getText().toString();
                    ItemObject item = new ItemObject(selectedItem, nameItem,memoItem);
                    if(nameItem.equals("")) {
                        Toast.makeText(getBaseContext(), "The name of the item cannot be empty. Failed to save.", Toast.LENGTH_LONG).show();
                    }else{
                        datasource.updateItem(item);
                        finish();
                    }
                    break;

                case R.id.delete:
                    datasource.deleteItemById(selectedItem);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    protected void onResume()
    {
        datasource.open();
        super.onResume();
    }
    @Override
    public void onPause() {
        datasource.close();
        super.onPause();
    }
}
