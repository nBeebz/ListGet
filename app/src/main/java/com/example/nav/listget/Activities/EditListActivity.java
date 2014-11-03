package com.example.nav.listget.Activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.nav.listget.DBHelper;
import com.example.nav.listget.R;
import com.example.nav.listget.parcelable.ListObject;

public class EditListActivity extends Activity {


    private ListObject selectedCat = null;
    private EditText editText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);
        setButtons();
        Intent intent = getIntent();
        selectedCat = ((ListObject) intent.getExtras().getSerializable("list"));

        editText = (EditText)findViewById(R.id.category);
        editText.setText(selectedCat.getCategory());

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

    class ClickListener implements OnClickListener {
        public void onClick(View v){
            //open database
            DBHelper helper = new DBHelper(getBaseContext());
            SQLiteDatabase db = helper.getReadableDatabase();
            ContentValues cv = new ContentValues();
            switch(v.getId())
            {
                case R.id.b_save:
                    selectedCat.setCategory(editText.getText().toString());
                    cv.put("category", selectedCat.getCategory() );
                    db.update("categories", cv, "categoryId = "+selectedCat.getCategoryId(), null);

                case R.id.delete:
                    db.delete("categories", "categoryId = " + selectedCat.getCategoryId(), null);
                    break;
                default:
                    break;
            }
            db.close();
            finish();
        }
    }
    public void setButtons(){
        ClickListener listener = new ClickListener();
        Button btn3 = (Button)findViewById(R.id.b_save);
        btn3.setOnClickListener(listener);
        Button btn4 = (Button)findViewById(R.id.b_delete);
        btn4.setOnClickListener(listener);
    }

    /**
     * 保存ボタン用listener（アップデート）
     */
    private void updateButton(){
        Button b_save = (Button) findViewById(R.id.b_save);
        b_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(editText.getText().toString().equals(""))){
                    selectedCat.setCategory(editText.getText().toString());

                    DBHelper helper = new DBHelper(getBaseContext());
                    SQLiteDatabase db = helper.getReadableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put("category", selectedCat.getCategory() );
                    db.update("categories", cv, "categoryId = "+selectedCat.getCategoryId(), null);
                    db.close();
                }
            }
        });
    }
    /**
     * delete button
     */
    private void deleteButton(){
        Button b_delete = (Button) findViewById(R.id.b_delete);
        b_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper helper = new DBHelper(getBaseContext());
                SQLiteDatabase db = helper.getReadableDatabase();
                db.delete("categories", "categoryId = " + selectedCat.getCategoryId(), null);
                db.close();
            }
        });

    }

}
