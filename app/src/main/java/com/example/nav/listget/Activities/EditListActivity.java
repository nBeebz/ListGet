package com.example.nav.listget.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nav.listget.AccessObject;
import com.example.nav.listget.R;
import com.example.nav.listget.parcelable.ListObject;

public class EditListActivity extends Activity {


    private ListObject selectedCat = null;
    private EditText editText = null;
    private AccessObject datasource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);
        datasource = new AccessObject(this);
        datasource.open();
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
            switch(v.getId())
            {
                case R.id.b_save:
                    if(editText.getText().toString() ==""){
                        Toast.makeText(getBaseContext(), "The name of the list cannot be empty. Failed to save.", Toast.LENGTH_LONG).show();
                    }else {
                        selectedCat.setCategory(editText.getText().toString());
                        datasource.updateListName(selectedCat.getCategory(), selectedCat.getCategoryId());
                        selectedCat.setCategory(editText.getText().toString());
                        finish();
                    }
                    break;

                case R.id.b_delete:
                    datasource.deleteListById(selectedCat.getCategoryId());
                    Intent intent = new Intent(getBaseContext(), List.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }
    public void setButtons(){
        ClickListener listener = new ClickListener();
        Button btn3 = (Button)findViewById(R.id.b_save);
        btn3.setOnClickListener(listener);
        Button btn4 = (Button)findViewById(R.id.b_delete);
        btn4.setOnClickListener(listener);
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
