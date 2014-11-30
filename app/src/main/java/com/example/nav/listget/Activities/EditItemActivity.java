package com.example.nav.listget.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nav.listget.Interfaces.MongoInterface;
import com.example.nav.listget.Mongo;
import com.example.nav.listget.R;
import com.example.nav.listget.parcelable.ItemObject;

public class EditItemActivity extends Activity implements MongoInterface {

    EditText textName;
    EditText textMemo;
    CheckBox checkbox;
    TextView checkedBy;
    String userid;
    ItemObject selectedItem;
    int itemPosition = -1;
    MongoInterface m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        m = this;

        MyOnFocusChangeListener focusChangeListener = new MyOnFocusChangeListener();
        //textName
        textName = (EditText)findViewById(R.id.item_value);
        textName.setOnFocusChangeListener(focusChangeListener);
        //memo
        textMemo = (EditText)findViewById(R.id.memo);
        textMemo.setOnFocusChangeListener(focusChangeListener);

        checkbox = (CheckBox)findViewById(R.id.checkbox);
        checkedBy = (TextView)findViewById(R.id.checkedBy);

        setButtons();
        Intent intent = getIntent();
        selectedItem = ((ItemObject) intent.getExtras().getParcelable("item"));
        itemPosition = intent.getExtras().getInt("position");
        userid = intent.getExtras().getString("userid");

        textMemo.setText(selectedItem.getMemo());
        textName.setText(selectedItem.getName());
        checkbox.setOnClickListener(new CheckClickedListener());
        if(!selectedItem.getCompleter().equals("")){
            checkbox.setChecked(true);
            if(selectedItem.getCompleter().equals(userid))
                checkedBy.setText("You completed");
            else
                checkedBy.setText(selectedItem.getCompleter()+" completed");
        }else {
            checkbox.setChecked(false);
            checkedBy.setText("Nobody completed this item");

        }
    }


    @Override
    public void processResult(String result) {

    }

    class CheckClickedListener implements OnClickListener {
        public void onClick(View v) {
            if(checkbox.isChecked()){
                checkbox.setChecked(true);
                selectedItem.setCompleter(userid);
                checkedBy.setText("You completed");

            }else {
                checkbox.setChecked(false);
                selectedItem.setCompleter("");
                checkedBy.setText("Nobody completed this item");
            }

        }
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
     * listener class
     */
    class ClickListener implements OnClickListener {
        public void onClick(View v){
            Intent data = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("position", itemPosition);

            switch(v.getId())
            {
                case R.id.save:
                    String nameItem = textName.getText().toString();
                    String memoItem = textMemo.getText().toString();
                    if(nameItem.equals("")) {
                        Toast.makeText(getBaseContext(), "The name of the item cannot be empty. Failed to save.", Toast.LENGTH_LONG).show();
                    }else{
                        selectedItem.setName(nameItem);
                        selectedItem.setMemo(memoItem);
                        Mongo.getMongo(m).put(Mongo.COLL_ITEMS,Mongo.KEY_ID,selectedItem.getId(),Mongo.KEY_NAME,nameItem);
                        Mongo.getMongo(m).put(Mongo.COLL_ITEMS,Mongo.KEY_ID,selectedItem.getId(),Mongo.KEY_MEMO,memoItem);
                        Mongo.getMongo(m).put(Mongo.COLL_ITEMS,Mongo.KEY_ID,selectedItem.getId(),Mongo.KEY_COMPLETED,selectedItem.getCompleter());

                        bundle.putParcelable("item", selectedItem);
                        data.putExtras(bundle);
                        setResult(RESULT_OK, data);

                        finish();
                    }
                    break;

                case R.id.delete:
                    Mongo.getMongo(m).delete(Mongo.COLL_ITEMS,selectedItem.getId());
                    data.putExtras(bundle);
                    setResult(RESULT_CANCELED, data);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

}
